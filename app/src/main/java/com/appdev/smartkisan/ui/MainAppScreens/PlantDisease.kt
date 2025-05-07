package com.appdev.smartkisan.ui.MainAppScreens

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.Actions.DiseaseDetectActions
import com.appdev.smartkisan.Actions.ProductActions
import com.appdev.smartkisan.Actions.UserAuthAction
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.DiseaseDetectState
import com.appdev.smartkisan.ViewModel.DiseaseDetectViewModel
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.appdev.smartkisan.ui.OtherComponents.CustomLoader
import com.appdev.smartkisan.ui.navigation.Routes


@Composable
fun PlantDiseaseRoot(
    controller: NavHostController,
    diseaseDetectViewModel: DiseaseDetectViewModel = hiltViewModel()
) {
    PlantDisease(diseaseDetectViewModel.detectUiState) { action ->
        when (action) {
            is DiseaseDetectActions.GoBack -> {
                controller.navigateUp()
            }

            else -> diseaseDetectViewModel.onAction(action)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDisease(detectUiState: DiseaseDetectState, onAction: (DiseaseDetectActions) -> Unit) {


    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onAction.invoke(DiseaseDetectActions.AddSelectedImage(uri))
            onAction.invoke(
                DiseaseDetectActions.ExtractedBitmap(
                    bitmap = if (Build.VERSION.SDK_INT < 28) {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val drawable = android.graphics.drawable.Drawable.createFromStream(
                            inputStream,
                            uri.toString()
                        )
                        inputStream?.close()
                        drawable?.toBitmap()
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    }
                )
            )
        }
    }

    LaunchedEffect(detectUiState.error) {
        detectUiState.error?.let { error ->
            Log.d("myerror",error)
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            onAction(DiseaseDetectActions.ClearValidationError)
        }
    }

    LaunchedEffect(detectUiState.diagnosisResult) {
        detectUiState.diagnosisResult?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tempicon),
                    contentDescription = "",
                    modifier = Modifier.size(40.dp)
                )
                Text(text = "Disease Detection", fontSize = 19.sp, fontWeight = FontWeight.Bold)
            }
        },colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent))
    }) { paddings ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddings),
            contentAlignment = Alignment.Center
        ) {
            if (detectUiState.isLoading) {
                CustomLoader()
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF66BB6A))) {
                    Text(
                        text = "Instructions: Make sure the whole leaf is clear and looks like the sample shown and put the leaf on a plain background for better results.",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(10.dp),
                        lineHeight = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    border = BorderStroke(
                        width = 4.dp,
                        color = Color(0xFF2E7D32)
                    )
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(detectUiState.selectedImageUri)
                            .build(),
                        placeholder = painterResource(R.drawable.placholder),
                        error = painterResource(R.drawable.placholder),
                        contentDescription = "", modifier = Modifier.size(200.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                imageGet(
                    text = "Take picture",
                    subtitle = "of your plant",
                    icon = R.drawable.camshot
                ) {}
                imageGet(
                    text = "Import picture",
                    subtitle = "from your gallery",
                    icon = R.drawable.importgallery
                ) {
                    launcher.launch("image/*")
                }
                Spacer(modifier = Modifier.height(20.dp))
                CustomButton(onClick = {
                    onAction(DiseaseDetectActions.StartDiagnosis(context))
                }, text = "Diagnose", width = 1f)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun imageGet(text: String, subtitle: String, icon: Int, onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xff8ad167)),
        modifier = Modifier.padding(top = 10.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = text,
                    fontSize = 19.sp, color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp, color = Color(0xFF2E7D32)
                )
            }
            Image(
                painter = painterResource(id = icon),
                contentDescription = "",
                modifier = Modifier.size(40.dp) // Size of the actual image
            )
        }
    }
}