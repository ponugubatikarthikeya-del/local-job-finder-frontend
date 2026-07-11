package com.linker.app.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.linker.app.core.network.connection.ConnectivityScreenLauncher
import com.linker.app.main.splash.SplashScreen
import com.linker.app.ui.theme.LocalJobFinderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Initialize the system SplashScreen API
        val systemSplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep the system splash screen on until the Compose splash is ready to be displayed
        systemSplashScreen.setKeepOnScreenCondition {
            !viewModel.isSplashFinished.value && !viewModel.isReady.value
        }

        setContent {
            LocalJobFinderTheme {
                // Observe states from ViewModel
                val isSplashFinished by viewModel.isSplashFinished.collectAsState()

                // Mark as ready once Compose starts rendering
                LaunchedEffect(Unit) {
                    viewModel.setReady(true)
                }

                // 3. Wrap everything in our Connectivity observer
                ConnectivityScreenLauncher {
                    // Use the background color from theme
                    Surface(color = MaterialTheme.colorScheme.background) {
                        if (!isSplashFinished) {
                            // 4. Show the custom animated splash screen
                            SplashScreen(
                                onFinished = { viewModel.splashFinished() }
                            )
                        } else {
                            // 5. App Content (Home/Navigation)
                            MainAppContent()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent() {
    // Placeholder for your main navigation/screens
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Main App Content Loaded", style = MaterialTheme.typography.headlineMedium)
    }
}
