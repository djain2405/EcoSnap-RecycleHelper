package com.workshop.recyclehelper

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.workshop.recyclehelper.ml.RecycleClassifier
import com.workshop.recyclehelper.models.RecycleResult
import com.workshop.recyclehelper.ui.screens.CameraScreen
import com.workshop.recyclehelper.ui.screens.HomeScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

/**
 * RecycleHelperApp - Main app composable
 *
 * This is the root of our app's UI
 * Handles:
 * - Navigation between Home and Camera screens
 * - Camera permissions
 * - Image classification
 * - State management
 *
 * Similar to iOS ContentView with navigation logic
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecycleHelperApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State management
    var showCamera by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<RecycleResult?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // ML Classifier (created once and reused)
    val classifier = remember { RecycleClassifier(context) }

    // Camera permission state
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Photo selected! Load and classify
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    capturedImageBitmap = bitmap
                    isProcessing = true

                    // Classify on background thread
                    scope.launch {
                        val classificationResult = withContext(Dispatchers.Default) {
                            classifier.classify(bitmap)
                        }

                        // Update UI on main thread
                        result = classificationResult
                        isProcessing = false
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("RecycleHelper", "Error loading image from gallery", e)
            }
        }
    }

    // Main app UI
    if (showCamera && cameraPermissionState.status.isGranted) {
        // Show camera screen
        CameraScreen(
            onPhotoCaptured = { bitmap ->
                // Photo captured! Close camera and classify
                capturedImageBitmap = bitmap
                showCamera = false
                isProcessing = true

                // Classify on background thread
                scope.launch {
                    val classificationResult = withContext(Dispatchers.Default) {
                        classifier.classify(bitmap)
                    }

                    // Update UI on main thread
                    result = classificationResult
                    isProcessing = false
                }
            },
            onClose = {
                // User canceled - go back to home
                showCamera = false
            }
        )
    } else {
        // Show home screen
        HomeScreen(
            result = result,
            isProcessing = isProcessing,
            capturedImageBitmap = capturedImageBitmap,
            onTakePhotoClick = {
                // Clear previous state
                result = null
                capturedImageBitmap = null

                // Check camera permission
                if (cameraPermissionState.status.isGranted) {
                    // Permission granted - show camera
                    showCamera = true
                } else {
                    // Request permission
                    cameraPermissionState.launchPermissionRequest()
                }
            },
            onChoosePhotoClick = {
                // Clear previous state
                result = null
                capturedImageBitmap = null

                // Launch photo picker
                photoPickerLauncher.launch("image/*")
            }
        )
    }

    // Cleanup when app is disposed
    DisposableEffect(Unit) {
        onDispose {
            classifier.close()
        }
    }
}
