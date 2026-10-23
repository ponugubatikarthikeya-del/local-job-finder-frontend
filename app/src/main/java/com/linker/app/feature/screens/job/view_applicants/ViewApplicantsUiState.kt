package com.linker.app.feature.screens.job.view_applicants

import com.linker.app.core.data.models.ApplicantDto

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class ViewApplicantsUiState(
    val jobId: Long = 0,
    val applicantsPagingDataFlow: Flow<PagingData<ApplicantDto>> = emptyFlow(),
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val pageNumber: Int = 0,
    val isLast: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
