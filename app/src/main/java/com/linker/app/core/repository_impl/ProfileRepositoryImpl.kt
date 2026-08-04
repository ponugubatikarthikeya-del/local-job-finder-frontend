package com.linker.app.core.repository_impl

import com.linker.app.core.data.models.User
import com.linker.app.core.data.models.UserDto
import com.linker.app.core.data.request.AddressTakeRequest
import com.linker.app.core.data.request.EditProfileRequest
import com.linker.app.core.data.responses.ProfileResponse
import com.linker.app.core.database.dao.UserDao
import com.linker.app.core.database.entity.UserEntity
import com.linker.app.core.network_api.AppException
import com.linker.app.core.network_api.NetworkResult
import com.linker.app.core.network_api.api.ProfileApiService
import com.linker.app.core.network_api.repository.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ProfileApiService,
    private val userDao: UserDao,
    private val ioDispatcher: CoroutineDispatcher
) : ProfileRepository {

    override suspend fun updateAddress(request: AddressTakeRequest): NetworkResult<ProfileResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.updateUserAddress(request)
            NetworkResult.Success(response)
        } catch (e: retrofit2.HttpException) {
            NetworkResult.Failure(AppException.ServerException(e.code(), e.message()))
        } catch (e: java.io.IOException) {
            NetworkResult.Failure(AppException.NetworkException("No internet connection", e))
        } catch (e: Exception) {
            android.util.Log.e("PROFILE_REPO", "Deserialization / Mapping Error: ", e)
            NetworkResult.Failure(AppException.UnknownException("JSON Parsing Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun getMyProfile(): NetworkResult<Unit> = withContext(ioDispatcher) {
        try {
            val response = apiService.getMyProfile()
            response.data?.let { userDto ->
                userDao.upsertUser(userDto.toUserEntity())
            }
            NetworkResult.Success(Unit)
        } catch (e: retrofit2.HttpException) {
            NetworkResult.Failure(AppException.ServerException(e.code(), e.message()))
        } catch (e: java.io.IOException) {
            NetworkResult.Failure(AppException.NetworkException("No internet connection", e))
        } catch (e: Exception) {
            android.util.Log.e("PROFILE_REPO", "Error fetching profile: ", e)
            NetworkResult.Failure(AppException.UnknownException(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun getPublicProfile(jobId: Long): NetworkResult<com.linker.app.core.data.responses.PublicProfileResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.getPublicProfile(jobId)
            NetworkResult.Success(response)
        } catch (e: retrofit2.HttpException) {
            NetworkResult.Failure(AppException.ServerException(e.code(), e.message()))
        } catch (e: java.io.IOException) {
            NetworkResult.Failure(AppException.NetworkException("No internet connection", e))
        } catch (e: Exception) {
            NetworkResult.Failure(AppException.UnknownException(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun updateProfile(request: EditProfileRequest): NetworkResult<ProfileResponse> = withContext(ioDispatcher) {
        try {
            val response = apiService.updateProfile(request)
            // On successful update, re-fetch to sync Room
            getMyProfile()
            NetworkResult.Success(response)
        } catch (e: retrofit2.HttpException) {
            NetworkResult.Failure(AppException.ServerException(e.code(), e.message()))
        } catch (e: java.io.IOException) {
            NetworkResult.Failure(AppException.NetworkException("No internet connection", e))
        } catch (e: Exception) {
            android.util.Log.e("PROFILE_REPO", "Error updating profile: ", e)
            NetworkResult.Failure(AppException.UnknownException(e.localizedMessage ?: "Unknown error"))
        }
    }

    override fun getUserProfileFlow(): Flow<User?> {
        return userDao.getUserFlow().map { it?.toUser() }
    }

    override suspend fun clearProfile() = withContext(ioDispatcher) {
        userDao.deleteUser()
    }

    private fun UserDto.toUserEntity() = UserEntity(
        id = id,
        username = username,
        email = email,
        phone = phone,
        headline = headline,
        skills = skills ?: emptyList(),
        dateOfBirth = dateOfBirth,
        cityName = cityName,
        rating = rating,
        totalJobsCompleted = totalJobsCompleted,
        userDescription = userDescription,
        createdAt = createdAt
    )

    private fun UserEntity.toUser() = User(
        id = id,
        username = username,
        email = email,
        phone = phone,
        headline = headline,
        skills = skills,
        dateOfBirth = dateOfBirth,
        cityName = cityName,
        rating = rating,
        totalJobsCompleted = totalJobsCompleted,
        userDescription = userDescription,
        createdAt = createdAt
    )
}
