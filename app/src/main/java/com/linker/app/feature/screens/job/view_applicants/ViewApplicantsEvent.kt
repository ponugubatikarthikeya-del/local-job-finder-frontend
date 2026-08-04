package com.linker.app.feature.screens.job.view_applicants

sealed interface ViewApplicantsEvent {
    data class LoadApplicants(val jobId: Long) : ViewApplicantsEvent
    data class AcceptApplicant(val applicantId: Long) : ViewApplicantsEvent
    data class RejectApplicant(val applicantId: Long) : ViewApplicantsEvent
    data object ClearError : ViewApplicantsEvent
    data object ClearSuccessMessage : ViewApplicantsEvent
    data object OnBackClicked : ViewApplicantsEvent
}
