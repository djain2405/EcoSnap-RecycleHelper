package com.workshop.recyclehelper.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.workshop.recyclehelper.models.RecycleResult

/**
 * HomeScreen - The main landing screen
 *
 * Shows:
 * - App logo and title
 * - "Take Photo" button
 * - Classification results (if available)
 * - Processing state (if classifying)
 *
 * Same as iOS ContentView
 */
@Composable
fun HomeScreen(
    result: RecycleResult?,
    isProcessing: Boolean,
    capturedImageBitmap: android.graphics.Bitmap?,
    onTakePhotoClick: () -> Unit,
    onChoosePhotoClick: () -> Unit
) {
    // Box allows layering (like ZStack in SwiftUI)
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Beautiful gradient background (like iOS version)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF4CAF50).copy(alpha = 0.6f),  // Green
                        Color(0xFF2196F3).copy(alpha = 0.4f)   // Blue
                    )
                )
            )
    ) {
        // Main content column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Top section: Logo and title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App Icon/Logo
                Text(
                    text = "♻️",
                    fontSize = 100.sp
                )

                // Title
                Text(
                    text = "Recycle Helper",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "AI-Powered Recycling Assistant",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            // Middle section: Results or captured image
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Show result if available
                if (result != null) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = true,
                        enter = scaleIn() + fadeIn()
                    ) {
                        ResultCard(result = result)
                    }
                }
                // Show captured image while processing
                else if (capturedImageBitmap != null) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = true,
                        enter = scaleIn()
                    ) {
                        Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    bitmap = capturedImageBitmap.asImageBitmap(),
                                    contentDescription = "Captured image",
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .shadow(5.dp, RoundedCornerShape(15.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = "Processing...",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
            }

            // Bottom section: Action buttons and workshop info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main "Take Photo" button
                Button(
                    onClick = onTakePhotoClick,
                    enabled = !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3),  // Blue
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(15.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Take Photo to Classify",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // "Choose from Photos" button
                Button(
                    onClick = onChoosePhotoClick,
                    enabled = !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C27B0),  // Purple
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(15.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Choose from Photos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Workshop info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "IEEE Workshop Demo",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "On-Device ML with TensorFlow Lite",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Loading overlay (if processing)
        if (isProcessing) {
            LoadingOverlay()
        }
    }
}

/**
 * ResultCard - Displays classification result
 *
 * Shows:
 * - Category (Recyclable/Landfill/Not Sure) with emoji
 * - Confidence percentage and progress bar
 * - Helper message
 * - Technical details
 */
@Composable
fun ResultCard(result: RecycleResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category name with emoji
            Text(
                text = result.category.displayName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = result.category.color
            )

            // Confidence percentage and bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Confidence: ${result.confidencePercentage}%",
                    fontSize = 14.sp,
                    color = Color.White
                )

                // Confidence progress bar
                LinearProgressIndicator(
                    progress = result.confidence,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = result.category.color,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }

            // Helper message
            Text(
                text = result.category.message,
                fontSize = 12.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.3f))

            // Technical details (for learning)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "🔍 Detected: ${result.detectedLabel}",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "Via TensorFlow Lite MobileNet",
                    fontSize = 10.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * LoadingOverlay - Shows processing indicator
 *
 * Displayed when the ML model is classifying an image
 */
@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(40.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            )
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = Color.White
                )

                Text(
                    text = "Analyzing...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Text(
                    text = "Using on-device AI",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
