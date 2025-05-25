package ar.edu.utn.frba.inventario.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
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
        onResult = { granted ->
            hasCameraPermission.value = granted

            if (!granted) {
                Toast.makeText(context, "Camera permission is required", Toast.LENGTH_LONG).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission.value) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission.value) {
        ScanCameraContent(innerPadding, navController)
    } else {
        PermissionDeniedContent(innerPadding)
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScanCameraContent(innerPadding: PaddingValues, navController: NavController) {
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        // 🔴 CAMERA PREVIEW
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
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            scanner.process(image)
                                .addOnSuccessListener { handleScanSuccess(it, navController) }
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

        // 🔴 OVERLAY UI
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
                    text = "Escaneá un QR or Código de barras",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

private fun handleScanSuccess(
    barcodes: List<Barcode>,
    navController: NavController
) {
    val QR_CODE_PREFIX = "inv_T3eI5QJ868z40lY_"

    val valid = barcodes.firstOrNull { barcode ->
        barcode.format == Barcode.FORMAT_QR_CODE || barcode.format == Barcode.FORMAT_EAN_13
    }

    if (valid != null) {
        val code = valid.rawValue ?: ""
        Log.d("[ScanScreen]", "Scanned code: $code")

        if (valid.format == Barcode.FORMAT_EAN_13) {
            val destination = "scan_result?result=${Uri.encode(code)}&codeType=ean-13"
            navController.navigate(destination)
        } else {
            if (code.startsWith(QR_CODE_PREFIX)) {
                val id = code.substring(QR_CODE_PREFIX.length)

                val destination = "scan_result?result=${Uri.encode(id)}&codeType=qr"
                navController.navigate(destination)
            } else {
                navController.navigate("scan_result?errorMessage=${Uri.encode("Código QR inválido.")}")
            }
        }
    } else if (barcodes.isNotEmpty()) {
        navController.navigate("scan_result?errorMessage=${Uri.encode("Formato no soportado.")}")
    }
}


@Composable
fun PermissionDeniedContent(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Camera permission not granted")
    }
}
