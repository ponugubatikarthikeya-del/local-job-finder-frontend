package com.linker.app.core.network_api.api

import com.linker.app.core.data.models.JobCard
import retrofit2.http.GET

interface JobApiService {
    @GET("user/jobs/all")
    suspend fun getJobs(): List<JobCard>
}