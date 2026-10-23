package com.linker.app.core.data.request

import java.math.BigDecimal

data class JobFilterCriteria(

    val search: String?,
    val category: String?,
    val salary : String?,

    val lat: BigDecimal,
    val lng: BigDecimal,
    val radius: Double,

    val page: Int,
    val size: Int

)
