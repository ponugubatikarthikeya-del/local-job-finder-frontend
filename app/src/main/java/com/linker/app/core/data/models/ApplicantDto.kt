package com.linker.app.core.data.models

import com.linker.app.core.util.serializers.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ApplicantDto(

    @SerialName("id")
    val id: Long = 0,

    @SerialName("applicationId")
    val applicationId: Long = 0,

    @SerialName("applicantId")
    val backendApplicantId: Long = 0,

    val userId: Long = 0,
    val username: String = "",
    val headline: String? = null,
    val status: String? = "PENDING",

    @Serializable(with = InstantSerializer::class)
    val appliedAt: Instant? = null

) {
    /**
     * Unique key used for local Room primary keys and Compose list keys.
     */
    val uniqueRoomKey: Long get() {
        return if (id != 0L) id
        else if (applicationId != 0L) applicationId
        else if (backendApplicantId != 0L) backendApplicantId
        else userId
    }

    /**
     * Exact candidate applicant ID expected by backend REST endpoints (acceptApplicant / rejectApplicant).
     * Prioritizes backend's "applicantId" serial field and userId.
     */
    val targetApplicantId: Long get() {
        return if (backendApplicantId != 0L) backendApplicantId
        else if (userId != 0L) userId
        else if (id != 0L) id
        else applicationId
    }

    val displayApplicantId: Long get() = targetApplicantId
}
