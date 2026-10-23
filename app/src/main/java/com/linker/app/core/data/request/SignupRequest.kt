package com.linker.app.core.data.request

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val username: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
)
