package com.linker.app.core.data.request

import kotlinx.serialization.Serializable

@Serializable
data class PasswordRequest(

    val resetKey: String = "",

    val resetPassword: String = "",

    val confirmPassword: String = ""
)
