package com.linker.app.feature.screens.job.post_jobs

import com.linker.app.core.util.enums.JobTypeEnum
import java.time.Instant

sealed interface PostJobEvent {
    data class OnTitleChanged(val value: String) : PostJobEvent
    data class OnDescriptionChanged(val value: String) : PostJobEvent
    data class OnCategoryChanged(val value: String) : PostJobEvent
    data class OnPayAmountChanged(val value: String) : PostJobEvent
    data class OnJobTypeChanged(val value: JobTypeEnum) : PostJobEvent
    data class OnAddressChanged(val value: String) : PostJobEvent
    data class OnJobTimeChanged(val value: Instant) : PostJobEvent
    data object OnPostClicked : PostJobEvent
    data object OnBackClicked : PostJobEvent
}
