package com.linker.app.core.network_api.api


import com.linker.app.core.data.request.PostJobRequest
import com.linker.app.core.data.responses.AllJobsResponse
import com.linker.app.core.data.responses.JobResponse
import com.linker.app.core.data.responses.UserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface JobApiService {
    @GET("user/job/filter/getActiveJobs/nearby")
    suspend fun getActiveJobs(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radius") radius: Double = 20.0,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): AllJobsResponse

    @POST("user/job/createJob")
    suspend fun postJob(
        @Body request: PostJobRequest
    ): UserResponse

    @DELETE("user/job/deleteJob/{jobId}")
    suspend fun deleteJob(
        @Path("jobId") jobId: Long
    ): JobResponse

    @GET("user/job/filter/getMyJobs")
    suspend fun getMyJobs(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): AllJobsResponse

    @GET("user/job/filter/search")
    suspend fun searchJobs(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radius") radius: Double = 100.0,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): AllJobsResponse

}