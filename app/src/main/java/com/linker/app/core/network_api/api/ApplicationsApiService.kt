package com.linker.app.core.network_api.api

import com.linker.app.core.data.models.ApplicantDto
import com.linker.app.core.data.responses.JobPagingResponse
import com.linker.app.core.data.responses.JobResponse
import com.linker.app.core.data.responses.PagedResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApplicationsApiService {
    @POST("user/job/applications/applyJob/{jobId}")
    suspend fun applyJob(
        @Path("jobId") jobId: Long
    ): JobResponse

    @GET("user/job/applications/viewApplicants/{jobId}")
    suspend fun viewApplicants(
        @Path("jobId") jobId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): JobPagingResponse<PagedResponse<ApplicantDto>>

    @POST("user/job/applications/acceptApplicant/{jobId}/{applicantId}")
    suspend fun acceptApplicant(
        @Path("jobId") jobId: Long,
        @Path("applicantId") applicantId: Long
    ): JobResponse

    @POST("user/job/applications/rejectApplicant/{jobId}/{applicantId}")
    suspend fun rejectApplicant(
        @Path("jobId") jobId: Long,
        @Path("applicantId") applicantId: Long
    ): JobResponse
}
