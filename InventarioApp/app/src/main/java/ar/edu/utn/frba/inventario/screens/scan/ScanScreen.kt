package ar.edu.utn.frba.inventario.screens.scan

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.screens.BottomNavigationBar
import ar.edu.utn.frba.inventario.utils.ProductResultArgs
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.withNavArgs
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors


@Composable
fun ScanScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        ScanBodyContent(innerPadding, navController)
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScanBodyContent(innerPadding: PaddingValues, navController: NavController) {
    val context = LocalContext.current

    val hasCameraPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasCameraPermission.value = it }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission.value)
            permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (hasCameraPermission.value)
        ScanCameraContent(innerPadding, navController)
    else
        PermissionDeniedContent(innerPadding, navController)
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScanCameraContent(innerPadding: PaddingValues, navController: NavController) {
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val scannedCode = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        // CAMERA PREVIEW
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    val scanner = BarcodeScanning.getClient()

                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image

                        if (mediaImage != null && !scannedCode.value) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            scanner.process(image)
                                .addOnSuccessListener { scannedCodes ->
                                    if (!scannedCode.value && scannedCodes.isNotEmpty()) {
                                        scannedCode.value = true
                                        handleScanSuccess(scannedCodes, navController, context)
                                    }
                                }
                                .addOnFailureListener {
                                    Log.e("[ScanScreen]", "Scan failed", it)
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("[ScanScreen]", "Camera binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // OVERLAY UI
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val scanBoxSize = 250.dp
            val scanBoxPx = with(LocalDensity.current) { scanBoxSize.toPx() }

            Canvas(modifier = Modifier.fillMaxSize()) {
                // Fullscreen dim
                drawRect(color = Color.Black.copy(alpha = 0.6f))

                // Transparent rounded cutout (centered)
                val centerX = size.width / 2
                val centerY = size.height / 2

                val rectLeft = centerX - scanBoxPx / 2
                val rectTop = centerY - scanBoxPx / 2

                // Use RoundedCornerShape to clip the inside with curves
                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = rectLeft,
                            top = rectTop,
                            right = rectLeft + scanBoxPx,
                            bottom = rectTop + scanBoxPx,
                            cornerRadius = CornerRadius(32f, 32f)
                        )
                    )
                }

                drawPath(path = path, color = Color.Transparent, blendMode = BlendMode.Clear)
            }

            // Instruction text at the top center
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.scan_camera_instruction),
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Manual Input Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                ManualInputButton(navController)
            }
        }
    }
}

@Composable
fun ManualInputButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(Screen.ManualCode.route) },
        shape = RoundedCornerShape(50),
    ) {
        Text(stringResource(R.string.scan_manual_input_button))
    }
}

private fun handleScanSuccess(
    scannedCodes: List<Barcode>,
    navController: NavController,
    context: android.content.Context
) {
    val QR_CODE_PREFIX = "inv_T3eI5QJ868z40lY_"

    val validCode = scannedCodes.firstOrNull { barcode ->
        barcode.format == Barcode.FORMAT_QR_CODE || barcode.format == Barcode.FORMAT_EAN_13
    }

    if (validCode == null) {
        val destination = Screen.ProductResult.withNavArgs(
            ProductResultArgs.ErrorMessage to context.getString(R.string.scan_error_unsupported_code_format)
        )
        navController.navigate(destination)
        return
    }

    val code = validCode.rawValue ?: ""
    Log.d("[ScanScreen]", "Scanned code: $code")

    val destination = when {
        validCode.format == Barcode.FORMAT_EAN_13 -> {
            Screen.ProductResult.withNavArgs(
                ProductResultArgs.Code to code,
                ProductResultArgs.CodeType to "ean-13"
            )
        }

        code.startsWith(QR_CODE_PREFIX) -> {
            val id = code.substring(QR_CODE_PREFIX.length)
            Screen.ProductResult.withNavArgs(
                ProductResultArgs.Code to id,
                ProductResultArgs.CodeType to "qr"
            )
        }

        else -> {
            Screen.ProductResult.withNavArgs(
                ProductResultArgs.ErrorMessage to context.getString(R.string.scan_error_invalid_qr)
            )
        }
    }

    navController.navigate(destination)
}


@Composable
fun PermissionDeniedContent(innerPadding: PaddingValues, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.scan_camera_permission_denied_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.scan_camera_permission_denied_body),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        ManualInputButton(navController)
    }
}

