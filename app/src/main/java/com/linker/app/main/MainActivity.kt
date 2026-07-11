package com.linker.app.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Enables Hilt injection
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val systemSplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        systemSplashScreen.setKeepOnScreenCondition { false }

        setContent {

            }

    }
}