package com.linker.app.core.data.request

import com.linker.app.core.util.serializers.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class AddressTakeRequest(
    val cityName: String,

    @Serializable(with = BigDecimalSerializer::class)
    val latitude: BigDecimal,

    @Serializable(with = BigDecimalSerializer::class)
    val longitude: BigDecimal
)
