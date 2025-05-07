package com.appdev.smartkisan.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.appdev.smartkisan.Actions.ChatBotScreenActions
import com.appdev.smartkisan.Actions.DiseaseDetectActions
import com.appdev.smartkisan.ModelThings.classifyDisease
import com.appdev.smartkisan.Repository.Repository
import com.appdev.smartkisan.States.ChatBotUiState
import com.appdev.smartkisan.States.DiseaseDetectState
import com.appdev.smartkisan.data.Chat
import com.appdev.smartkisan.data.ChatRoleEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiseaseDetectViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    var detectUiState by mutableStateOf(DiseaseDetectState())
        private set


    fun onAction(action: DiseaseDetectActions) {
        when (action) {
            is DiseaseDetectActions.AddSelectedImage -> {
                detectUiState = detectUiState.copy(selectedImageUri = action.uri)
            }

            DiseaseDetectActions.GoBack -> {

            }

            is DiseaseDetectActions.StartDiagnosis -> {
                detectUiState = detectUiState.copy(isLoading = true)
                try {
                    detectUiState.selectedImageBitmap?.let { bitmap ->
                        val diseaseName = classifyDisease(action.context, bitmap)
                        Log.d("JNAMZ", diseaseName)
                        detectUiState =
                            detectUiState.copy(diagnosisResult = diseaseName, isLoading = false)
                    }
                } catch (e: Exception) {
                    detectUiState =
                        detectUiState.copy(error = e.toString(), isLoading = false)
                }
            }

            is DiseaseDetectActions.ExtractedBitmap -> {
                detectUiState = detectUiState.copy(selectedImageBitmap = action.bitmap)

            }

            else -> {}
        }
    }
}