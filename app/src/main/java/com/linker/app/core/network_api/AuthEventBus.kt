package com.linker.app.core.network_api

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Global event bus to communicate authentication state changes (e.g., forced logout on 401 token refresh failure)
 * asynchronously without blocking interceptor threads.
 */
@Singleton
class AuthEventBus @Inject constructor() {
    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    fun emitLogout() {
        _logoutEvent.tryEmit(Unit)
    }
}
