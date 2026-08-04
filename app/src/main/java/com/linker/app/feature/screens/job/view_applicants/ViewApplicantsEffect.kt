package com.linker.app.feature.screens.job.view_applicants

sealed interface ViewApplicantsEffect {
    data object NavigateBack : ViewApplicantsEffect
}
