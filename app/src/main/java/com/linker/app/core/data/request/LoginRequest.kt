package com.linker.app.core.data.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(

    val email: String = "",

    val password: String = "",

)
