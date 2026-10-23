package com.linker.app.feature.screens.job.view_applicants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.repository.ApplicationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ViewApplicantsViewModel @Inject constructor(
    private val applicationsRepository: ApplicationsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ViewApplicantsUiState())
    val state: StateFlow<ViewApplicantsUiState> = _state.asStateFlow()

    private val _effect = Channel<ViewApplicantsEffect>()
    val effect = _effect.receiveAsFlow()

    private val _jobIdParams = MutableStateFlow<Long?>(null)

    init {
        val pagingFlow = _jobIdParams
            .flatMapLatest { jobId ->
                if (jobId != null) {
                    applicationsRepository.getApplicantsFlow(jobId).cachedIn(viewModelScope)
                } else {
                    emptyFlow()
                }
            }

        _state.update { it.copy(applicantsPagingDataFlow = pagingFlow) }
    }

    fun onEvent(event: ViewApplicantsEvent) {
        when (event) {
            is ViewApplicantsEvent.OnRefresh -> {
                // Let the UI handle refresh using LazyPagingItems.refresh()
            }
            is ViewApplicantsEvent.LoadApplicants -> {
                _state.update { it.copy(jobId = event.jobId) }
                _jobIdParams.value = event.jobId
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
}
