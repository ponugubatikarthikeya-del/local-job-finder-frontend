package com.linker.app.core.data.request

import kotlinx.serialization.Serializable

@Serializable
data class OAuthLoginRequest(
    val token : String = "",
    val provider : String = "",
)
