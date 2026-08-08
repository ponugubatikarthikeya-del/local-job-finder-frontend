package com.linker.app.feature.screens.job.view_applicants

import com.linker.app.core.data.models.ApplicantDto

data class ViewApplicantsUiState(
    val jobId: Long = 0,
    val applicants: List<ApplicantDto> = emptyList(),
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val pageNumber: Int = 0,
    val isLast: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
