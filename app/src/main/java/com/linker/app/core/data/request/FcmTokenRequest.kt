package com.linker.app.core.data.request

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequest(
    val deviceFcmToken: String = ""
)