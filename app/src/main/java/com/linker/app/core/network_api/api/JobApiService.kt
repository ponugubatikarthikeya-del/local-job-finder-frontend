package com.linker.app.core.network_api.api


import com.linker.app.core.data.models.JobDto
import com.linker.app.core.data.request.PostJobRequest
import com.linker.app.core.data.responses.JobDataResponse
import com.linker.app.core.data.responses.JobPagingResponse
import com.linker.app.core.data.responses.JobResponse
import com.linker.app.core.data.responses.PagedResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface JobApiService {
    @GET("user/job/filter/getActiveJobs/nearby")
    suspend fun getActiveJobs(
        @Query("search") search: String?,
        @Query("category") category: String?,
        @Query("salary") salary : Double? = null,
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radius") radius: Double = 50.0,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): JobPagingResponse<PagedResponse<JobDto>>

    @GET("user/job/filter/getMyJobs")
    suspend fun getMyJobs(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): JobPagingResponse<PagedResponse<JobDto>>


    @POST("user/job/createJob")
    suspend fun postJob(
        @Body request: PostJobRequest
    ): JobDataResponse<JobDto>

    @DELETE("user/job/deleteJob/{jobId}")
    suspend fun deleteJob(
        @Path("jobId") jobId: Long
    ): JobResponse

    @PATCH("user/job/job-completed/{jobId}")
    suspend fun completeJob(
        @Path("jobId") jobId : Long
    ) : JobResponse


}
