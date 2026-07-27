package com.linker.app.feature.screens.profile.private_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linker.app.core.datastore.TokenManager
import com.linker.app.core.network_api.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivateProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val jobRepository: JobRepository
) : ViewModel() {

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearTokens()
            jobRepository.clearJobs()
            onSuccess()
        }
    }
}
