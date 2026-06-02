package com.briel.marnisos.brielapp.ui.views.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors

@ExperimentalGetImage
@Composable
internal fun CupsScannerView(
    isUploadingReport: Boolean,
    onBack: () -> Unit,
    onCupsConfirmed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var cupsCodeText by remember { mutableStateOf("") }
    var wasEditedManually by remember { mutableStateOf(false) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(recognizer, cameraExecutor) {
        onDispose {
            recognizer.close()
            cameraExecutor.shutdown()
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = onBack) {
                Text("Volver")
            }
            Text(
                text = "Escanear CUPS",
                style = MaterialTheme.typography.titleMedium,
            )
            val isValid = CupsCodeParser.isValid(cupsCodeText)
            Button(
                enabled = !isUploadingReport && isValid,
                onClick = {
                    onCupsConfirmed(CupsCodeParser.normalize(cupsCodeText))
                },
            ) {
                Text(if (isUploadingReport) "Procesando..." else "Confirmar")
            }
        }

        if (!hasCameraPermission) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Necesitamos permiso de cámara para escanear el CUPS")
                Button(
                    modifier = Modifier.padding(top = 12.dp),
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                ) {
                    Text("Permitir cámara")
                }
            }
            return@Column
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage == null) {
                                        imageProxy.close()
                                        return@setAnalyzer
                                    }

                                    val image = InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees,
                                    )

                                    recognizer.process(image)
                                        .addOnSuccessListener(ContextCompat.getMainExecutor(context)) { visionText ->
                                            val candidate = CupsCodeParser.extractBestCandidate(visionText.text)
                                            if (candidate != null && !wasEditedManually) {
                                                cupsCodeText = candidate
                                            }
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                }
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis,
                        )
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(16.dp),
                tonalElevation = 6.dp,
                shadowElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "Escanea o introduce tu código CUPS",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    val fieldIsValid = CupsCodeParser.isValid(cupsCodeText)
                    OutlinedTextField(
                        value = cupsCodeText,
                        onValueChange = { newValue ->
                            cupsCodeText = newValue
                            wasEditedManually = newValue.isNotEmpty()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("ES0021000012345678XX") },
                        singleLine = true,
                        isError = cupsCodeText.isNotEmpty() && !fieldIsValid,
                        supportingText = {
                            Text(
                                text = when {
                                    cupsCodeText.isEmpty() -> "Apunta al CUPS impreso en la factura"
                                    fieldIsValid -> "Formato válido"
                                    else -> "Formato no válido"
                                },
                            )
                        },
                    )
                }
            }
        }
    }
}
