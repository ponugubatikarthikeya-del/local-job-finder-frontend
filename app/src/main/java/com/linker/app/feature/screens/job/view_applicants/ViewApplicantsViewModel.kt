package com.linker.app.feature.screens.job.view_applicants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.network_api.repository.ApplicationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewApplicantsViewModel @Inject constructor(
    private val applicationsRepository: ApplicationsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ViewApplicantsUiState())
    val state: StateFlow<ViewApplicantsUiState> = _state.asStateFlow()

    private val _effect = Channel<ViewApplicantsEffect>()
    val effect = _effect.receiveAsFlow()

    private var currentJobId: Long? = null

    fun onEvent(event: ViewApplicantsEvent) {
        when (event) {
            is ViewApplicantsEvent.LoadApplicants -> {
                currentJobId = event.jobId
                loadApplicants(event.jobId)
            }
            is ViewApplicantsEvent.AcceptApplicant -> {
                currentJobId?.let { jobId ->
                    acceptApplicant(jobId, event.applicantId)
                }
            }
            is ViewApplicantsEvent.RejectApplicant -> {
                currentJobId?.let { jobId ->
                    rejectApplicant(jobId, event.applicantId)
                }
            }
            is ViewApplicantsEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
            is ViewApplicantsEvent.ClearSuccessMessage -> {
                _state.update { it.copy(successMessage = null) }
            }
            is ViewApplicantsEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _effect.send(ViewApplicantsEffect.NavigateBack)
                }
            }
        }
    }

    private fun loadApplicants(jobId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = applicationsRepository.viewApplicants(jobId)) {
                is NetworkResult.Success -> {
                    val response = result.data
                    _state.update { it.copy(
                        applicants = response.allJobs ?: emptyList(),
                        totalElements = response.totalElements,
                        totalPages = response.totalPages,
                        pageNumber = response.pageNumber,
                        isLast = response.isLast,
                        isLoading = false
                    ) }
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(isLoading = false, error = result.exception.message) }
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun acceptApplicant(jobId: Long, applicantId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = applicationsRepository.acceptApplicant(jobId, applicantId)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(successMessage = result.data.message ?: "Applicant accepted") }
                    // Re-fetch to get updated statuses
                    loadApplicants(jobId)
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(isLoading = false, error = result.exception.message) }
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun rejectApplicant(jobId: Long, applicantId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = applicationsRepository.rejectApplicant(jobId, applicantId)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(successMessage = result.data.message ?: "Applicant rejected") }
                    // Re-fetch to get updated statuses
                    loadApplicants(jobId)
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(isLoading = false, error = result.exception.message) }
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
