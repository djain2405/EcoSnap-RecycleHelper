package com.workshop.recyclehelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.workshop.recyclehelper.ui.theme.RecycleHelperTheme

/**
 * MainActivity - Entry point for the Recycle Helper Android app
 *
 * This is the main activity that hosts all our Compose UI
 * Think of it like the SceneDelegate in iOS
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display (like iOS)
        enableEdgeToEdge()

        // Set up Compose UI
        setContent {
            // Apply our app theme (colors, typography, etc.)
            RecycleHelperTheme {
                // Surface provides background color
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Main app content - the navigation and screens
                    RecycleHelperApp()
                }
            }
        }
    }
}
