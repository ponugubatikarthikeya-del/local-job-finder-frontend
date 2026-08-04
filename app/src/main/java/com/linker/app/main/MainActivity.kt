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


import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.linker.app.fcm.FcmTokenManager
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    // 🟢 Inject FcmTokenManager to sync token with backend on app launch
    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

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

                // Request Notification Permission on Android 13+ (API 33+)
                RequestNotificationPermission()

                // Mark as ready once Compose starts rendering
                LaunchedEffect(Unit) {
                    viewModel.setReady(true)

                    // 🟢 Sync FCM token with Spring Boot backend upon app launch
                    fcmTokenManager.syncCurrentToken()

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

@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    android.util.Log.d("FCM", "Notification permission granted")
                } else {
                    android.util.Log.w("FCM", "Notification permission denied")
                }
            }
        )

        LaunchedEffect(Unit) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

