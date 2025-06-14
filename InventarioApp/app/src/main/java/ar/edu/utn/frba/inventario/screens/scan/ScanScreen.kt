package ar.edu.utn.frba.inventario.screens.scan

import android.Manifest
import android.content.Context
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import ar.edu.utn.frba.inventario.R
import ar.edu.utn.frba.inventario.utils.ProductResultArgs
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.utils.withNavArgs
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executor
import java.util.concurrent.Executors


@Composable
fun ScanScreen(navController: NavController, origin: String) {
    val hasCameraPermission = rememberCameraPermissionState()

    if (hasCameraPermission)
        ScanCameraContent(navController, origin)
    else
        PermissionDeniedContent(navController, origin)
}

@Composable
fun rememberCameraPermissionState(): Boolean {
    val context = LocalContext.current

    val hasPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasPermission.value = it }
    )

    LaunchedEffect(Unit) {
        if (!hasPermission.value) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    return hasPermission.value
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScanCameraContent(navController: NavController, origin: String) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val scannedCode = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // CAMERA PREVIEW
        AndroidView(
            factory = { ctx ->
                createCameraPreviewView(ctx, lifecycleOwner, cameraExecutor, scannedCode, navController, origin)
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
                drawScanOverlay(scanBoxPx)
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
                    text = stringResource(
                        if (origin == "shipment")
                            R.string.scan_camera_instruction_ean_13
                        else
                            R.string.scan_camera_instruction_qr
                    ),
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

            if (origin == "shipment") {
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

@Composable
fun PermissionDeniedContent(navController: NavController, origin: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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

        if (origin == "shipment") {
            ManualInputButton(navController)
        }
    }
}

private fun handleScanSuccess(
    scannedCodes: List<Barcode>,
    navController: NavController,
    context: Context,
    origin: String
) {
    val QR_CODE_PREFIX = "inv_T3eI5QJ868z40lY_"

    val validCode = scannedCodes.firstOrNull { barcode ->
        when (origin) {
            "shipment" -> barcode.format == Barcode.FORMAT_EAN_13
            "order" -> barcode.format == Barcode.FORMAT_QR_CODE
            else -> false
        }
    }

    if (validCode == null) {
        val destination = Screen.ProductResult.withNavArgs(
            ProductResultArgs.ErrorMessage to context.getString(
                when (origin) {
                    "shipment" -> R.string.scan_error_expected_ean13
                    "order" -> R.string.scan_error_expected_qr
                    else -> R.string.scan_error_unsupported_code_format
                }
            )
        )
        navController.navigate(destination)
        return
    }

    val code = validCode.rawValue ?: ""
    Log.d("[ScanScreen]", "Scanned code: $code")

    val destination = when {
        origin == "shipment" -> {
            Screen.ProductResult.withNavArgs(
                ProductResultArgs.Code to code,
                ProductResultArgs.CodeType to "ean-13"
            )
        }

        origin == "order" && code.startsWith(QR_CODE_PREFIX) -> {
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

@OptIn(ExperimentalGetImage::class)
fun createCameraPreviewView(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraExecutor: Executor,
    scannedCode: MutableState<Boolean>,
    navController: NavController,
    origin: String
): PreviewView {
    val previewView = PreviewView(context)
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

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
                            handleScanSuccess(scannedCodes, navController, context, origin)
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
    }, ContextCompat.getMainExecutor(context))

    return previewView
}

fun DrawScope.drawScanOverlay(scanBoxPx: Float) {
    // Fullscreen dim
    drawRect(color = Color.Black.copy(alpha = 0.6f))

    // Transparent rounded cutout (centered)
    val centerX = size.width / 2
    val centerY = size.height / 2

    val rectLeft = centerX - scanBoxPx / 2
    val rectTop = centerY - scanBoxPx / 2

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
