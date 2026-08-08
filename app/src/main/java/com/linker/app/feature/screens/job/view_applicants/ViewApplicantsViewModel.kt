package com.linker.app.feature.screens.job.view_applicants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.data.models.ApplicantDto
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.network_api.repository.ApplicationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    fun onEvent(event: ViewApplicantsEvent) {
        when (event) {
            is ViewApplicantsEvent.LoadApplicants -> {
                _state.update { it.copy(jobId = event.jobId) }
                observeApplicants(event.jobId)
            }
            is ViewApplicantsEvent.AcceptApplicant -> {
                val jobId = _state.value.jobId
                if (jobId != 0L) {
                    acceptApplicant(jobId, event.applicantId)
                } else {
                    _state.update { it.copy(error = "Job ID missing") }
                }
            }
            is ViewApplicantsEvent.RejectApplicant -> {
                val jobId = _state.value.jobId
                if (jobId != 0L) {
                    rejectApplicant(jobId, event.applicantId)
                } else {
                    _state.update { it.copy(error = "Job ID missing") }
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

    private fun observeApplicants(jobId: Long) {
        applicationsRepository.getApplicantsFlow(jobId)
            .onEach { list ->
                _state.update { it.copy(applicants = sortApplicants(list)) }
                
                // 🟢 Only sync from network if Room is empty and we're not already loading
                if (list.isEmpty() && !_state.value.isLoading) {
                    syncApplicants(jobId)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun syncApplicants(jobId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = applicationsRepository.syncApplicants(jobId)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
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
        if (applicantId == 0L) {
            _state.update { it.copy(error = "Invalid Application ID") }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = applicationsRepository.acceptApplicant(jobId, applicantId)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(
                        successMessage = result.data.message ?: "Applicant accepted",
                        isLoading = false
                    ) }
                    // Status is updated in Room by the repository, UI updates via Flow
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = result.exception.message
                    ) }
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun rejectApplicant(jobId: Long, applicantId: Long) {
        if (applicantId == 0L) {
            _state.update { it.copy(error = "Invalid Application ID") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = applicationsRepository.rejectApplicant(jobId, applicantId)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(
                        successMessage = result.data.message ?: "Applicant rejected",
                        isLoading = false
                    ) }
                }
                is NetworkResult.Failure -> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = result.exception.message
                    ) }
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun sortApplicants(list: List<ApplicantDto>): List<ApplicantDto> {
        return list.sortedWith(compareBy { 
            if (it.status?.uppercase() == "PENDING") 0 else 1 
        })
    }
}
