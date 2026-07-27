package com.linker.app.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import com.linker.app.navigation.AppNavGraph
import com.linker.app.ui.theme.LocalJobFinderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Initialize the system SplashScreen API
        val systemSplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep the system splash screen on until the Compose content is ready
        systemSplashScreen.setKeepOnScreenCondition {
            !viewModel.isReady.value
        }

        setContent {
            LocalJobFinderTheme {
                // Mark as ready once Compose starts rendering
                LaunchedEffect(Unit) {
                    viewModel.setReady(true)
                }

//                // 3. Wrap everything in our Connectivity observer
//                ConnectivityScreenLauncher {
                    // Use the background color from theme
                    Surface(color = MaterialTheme.colorScheme.background) {
                        // 5. App Content (Home/Navigation)
                        AppNavGraph()

                    }
            }
        }
    }
}

