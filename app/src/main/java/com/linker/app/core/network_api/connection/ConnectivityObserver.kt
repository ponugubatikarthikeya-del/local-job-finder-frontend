package com.linker.app.core.network_api.connection

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    fun observe() : Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost, Connecting
    }

}
