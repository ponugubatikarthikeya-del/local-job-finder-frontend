package com.linker.app.core.network_api.connection

import androidx.compose.runtime.compositionLocalOf

val LocalConnectivityStatus = compositionLocalOf<ConnectivityObserver.Status> {
    ConnectivityObserver.Status.Available
}
