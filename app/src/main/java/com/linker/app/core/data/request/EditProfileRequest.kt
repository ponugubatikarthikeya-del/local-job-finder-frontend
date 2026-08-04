package com.linker.app.core.data.request

import kotlinx.serialization.Serializable

@Serializable
data class EditProfileRequest(
    val username: String? = null,
    val phone: String? = null,
    val headline: String? = null,
    val dateOfBirth: String? = null,
    val skills: List<String>? = null,
    val cityName: String? = null,
    val userDescription: String? = null
)
