package com.appdev.smartkisan.Utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechRecognitionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val speechRecognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    private val languageIdentifier: LanguageIdentifier by lazy {
        LanguageIdentification.getClient()
    }

    private val TAG = "SpeechRecognition"

    private val _speechAmplitude = MutableStateFlow(0f)
    val speechAmplitude: StateFlow<Float> = _speechAmplitude

    // Track if recognition is active
    private var isRecognitionActive = false

    // Store accumulated text
    private var accumulatedText = ""
    private var lastPartialText = ""
    private var recognizedLanguage = ""

    private var onResultCallback: ((String, String) -> Unit)? = null
    private var onErrorCallback: ((Int) -> Unit)? = null
    private var onPartialResultCallback: ((String) -> Unit)? = null

    init {
        setupSpeechRecognizer()
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // Ready for speech
            }

            override fun onBeginningOfSpeech() {
                // Speech started
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Ensure valid values - prevent NaN
                if (!rmsdB.isNaN()) {
                    // Convert RMS dB to a scale better suited for visualization (0-1)
                    val amplitude = (rmsdB + 10) / 30f // Normalize to roughly 0-1 range
                    _speechAmplitude.value = amplitude.coerceIn(0f, 1f)
                } else {
                    _speechAmplitude.value = 0.2f // Default value if NaN
                }
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                // Let the final results callback handle everything
                // Don't finalize here as we might not have received final results yet
            }

            override fun onError(error: Int) {
                if (error == SpeechRecognizer.ERROR_NO_MATCH ||
                    error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    // Still use partial results if we have them
                    if (lastPartialText.isNotEmpty()) {
                        // If no final result but we have partial text, use that
                        if (accumulatedText.isNotEmpty()) {
                            accumulatedText += " "
                        }
                        accumulatedText += lastPartialText
                        lastPartialText = ""
                    }
                }

                // Finalize with what we have so far
                finalizeRecognition()
                onErrorCallback?.invoke(error)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""

                Log.d(TAG, "onResult : $text")

                if (text.isNotEmpty()) {
                    // Append text to accumulated text with space
                    if (accumulatedText.isNotEmpty()) {
                        accumulatedText += " "
                    }
                    accumulatedText += text
                } else if (lastPartialText.isNotEmpty()) {
                    // If final result is empty but we have partial text, use that
                    if (accumulatedText.isNotEmpty()) {
                        accumulatedText += " "
                    }
                    accumulatedText += lastPartialText
                    lastPartialText = ""
                }

                // Send updated text
                onPartialResultCallback?.invoke(accumulatedText)

                // Finalize recognition
                finalizeRecognition()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                Log.d(TAG, "onPartial : $text")

                if (text.isNotEmpty()) {
                    // Store the latest partial result
                    lastPartialText = text

                    // Create temporary text for display
                    val tempText = if (accumulatedText.isEmpty()) {
                        text
                    } else {
                        "$accumulatedText $text"
                    }

                    onPartialResultCallback?.invoke(tempText)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun finalizeRecognition() {
        Log.d(TAG, "Finalizing recognition with text: $accumulatedText")
        isRecognitionActive = false

        if (accumulatedText.isNotEmpty()) {
            // If we already identified the language, use it
            if (recognizedLanguage.isNotEmpty() && recognizedLanguage != "und") {
                onResultCallback?.invoke(accumulatedText, recognizedLanguage)
            } else {
                // Otherwise identify it now
                identifyLanguage(accumulatedText) { languageCode ->
                    onResultCallback?.invoke(accumulatedText, languageCode)
                }
            }
        } else if (lastPartialText.isNotEmpty()) {
            // If we have partial text but no accumulated text, use that
            identifyLanguage(lastPartialText) { languageCode ->
                onResultCallback?.invoke(lastPartialText, languageCode)
            }
        } else {
            onResultCallback?.invoke("", "")
        }
    }

    private fun createRecognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "")  // Empty for auto-detection
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

            // Increase timeout values to let recognition run longer
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1500)

        }
    }


    fun startListening(
        initialText: String = "",
        onResult: (String, String) -> Unit,
        onError: (Int) -> Unit,
        onPartialResult: (String) -> Unit = {}
    ) {
        this.onResultCallback = onResult
        this.onErrorCallback = onError
        this.onPartialResultCallback = onPartialResult

        // Initialize with any existing text
        accumulatedText = initialText
        lastPartialText = ""
        recognizedLanguage = ""
        isRecognitionActive = true

        try {
            val intent = createRecognizerIntent()
            speechRecognizer.startListening(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognizer: ${e.message}")
            onError(SpeechRecognizer.ERROR_CLIENT)
        }
    }

    fun stopListening() {
        if (!isRecognitionActive) return

        try {
            Log.d(TAG, "Stopping speech recognition")
            speechRecognizer.stopListening()

            // We'll let onResults or onError handle finalization
            // Don't call finalizeRecognition() here, as it might
            // be called before onResults has a chance to process
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping speech recognizer: ${e.message}")
            // If stopping fails, still try to finalize with what we have
            finalizeRecognition()
        }
    }

    private fun identifyLanguage(text: String, callback: (String) -> Unit) {
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                recognizedLanguage = if (languageCode == "und") {
                    "en" // Default to English if undefined
                } else {
                    languageCode
                }
                callback(recognizedLanguage)
            }
            .addOnFailureListener {
                recognizedLanguage = "en" // Default to English on failure
                callback(recognizedLanguage)
            }
    }

    fun destroy() {
        isRecognitionActive = false
        speechRecognizer.destroy()
    }
}