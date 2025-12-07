package com.workshop.recyclehelper.ui.screens

import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * CameraScreen - Full-screen camera with capture button
 *
 * Uses CameraX for camera operations (Android's modern camera API)
 * Similar to iOS CameraView with AVFoundation
 *
 * @param onPhotoCaptured Callback when photo is taken
 * @param onClose Callback when user cancels
 */
@Composable
fun CameraScreen(
    onPhotoCaptured: (Bitmap) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Camera state
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Executor for camera operations (background thread)
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Box allows layering (camera + UI overlay)
    Box(modifier = Modifier.fillMaxSize()) {
        // LAYER 1: Camera Preview (fullscreen)
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            onImageCaptureReady = { capture ->
                imageCapture = capture
                Log.d("CameraScreen", "✅ Camera ready")
            }
        )

        // LAYER 2: UI Overlay
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top bar with close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onClose,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Bottom section: Instructions + Capture button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Instructions
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Point camera at an item",
                        fontSize = 18.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                                blurRadius = 3f
                            )
                        )
                    )

                    Text(
                        text = "Tap the button below to capture",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                                blurRadius = 3f
                            )
                        )
                    )
                }

                // Capture button (circular, like iOS)
                CaptureButton(
                    onClick = {
                        // Capture photo when button clicked
                        capturePhoto(
                            imageCapture = imageCapture,
                            executor = cameraExecutor,
                            onPhotoCaptured = { bitmap ->
                                Log.d("CameraScreen", "📸 Photo captured: ${bitmap.width}x${bitmap.height}")
                                onPhotoCaptured(bitmap)
                            },
                            onError = { exception ->
                                Log.e("CameraScreen", "❌ Capture failed: ${exception.message}")
                            }
                        )
                    }
                )
            }
        }
    }

    // Cleanup when composable is removed
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            Log.d("CameraScreen", "🔒 Camera executor shut down")
        }
    }
}

/**
 * CameraPreview - The actual camera preview
 *
 * Uses AndroidView to embed the CameraX PreviewView
 * Similar to UIViewControllerRepresentable in SwiftUI
 */
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            // Create PreviewView (the view that shows camera feed)
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            // Start camera on background thread
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    bindCamera(
                        cameraProvider = cameraProvider,
                        previewView = previewView,
                        lifecycleOwner = lifecycleOwner,
                        onImageCaptureReady = onImageCaptureReady
                    )
                } catch (e: Exception) {
                    Log.e("CameraPreview", "❌ Camera binding failed: ${e.message}")
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )
}

/**
 * Bind camera to lifecycle
 *
 * STEPS:
 * 1. Create Preview use case (for displaying camera feed)
 * 2. Create ImageCapture use case (for taking photos)
 * 3. Select back camera
 * 4. Bind use cases to lifecycle
 */
private fun bindCamera(
    cameraProvider: ProcessCameraProvider,
    previewView: PreviewView,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    Log.d("CameraPreview", "📷 Binding camera...")

    // STEP 1: Create Preview use case
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }

    // STEP 2: Create ImageCapture use case
    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    // STEP 3: Select back camera
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    try {
        // STEP 4: Unbind all use cases before rebinding
        cameraProvider.unbindAll()

        // STEP 5: Bind use cases to lifecycle
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        Log.d("CameraPreview", "✅ Camera bound successfully")

        // Notify that ImageCapture is ready
        onImageCaptureReady(imageCapture)

    } catch (e: Exception) {
        Log.e("CameraPreview", "❌ Camera binding failed: ${e.message}")
    }
}

/**
 * Capture a photo
 *
 * STEPS:
 * 1. Take photo using ImageCapture
 * 2. Convert to Bitmap
 * 3. Return via callback
 */
private fun capturePhoto(
    imageCapture: ImageCapture?,
    executor: java.util.concurrent.Executor,
    onPhotoCaptured: (Bitmap) -> Unit,
    onError: (Exception) -> Unit
) {
    if (imageCapture == null) {
        Log.e("CameraScreen", "❌ ImageCapture not initialized")
        return
    }

    Log.d("CameraScreen", "📸 Capturing photo...")

    // Capture photo in memory (not saving to file)
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                Log.d("CameraScreen", "✅ Image captured, converting to Bitmap...")

                // Convert ImageProxy to Bitmap
                val bitmap = imageProxy.toBitmap()

                // Return bitmap via callback
                onPhotoCaptured(bitmap)

                // Clean up
                imageProxy.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "❌ Capture error: ${exception.message}")
                onError(exception)
            }
        }
    )
}

/**
 * CaptureButton - Circular capture button
 *
 * Styled like iOS camera capture button
 * White circle with outer ring
 */
@Composable
fun CaptureButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Outer ring
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(5.dp, Color.White)
            ) {}

            // Inner filled circle
            Surface(
                modifier = Modifier.size(65.dp),
                shape = CircleShape,
                color = Color.White
            ) {}
        }
    }
}
