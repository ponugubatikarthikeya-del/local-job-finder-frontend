package com.linker.app

import kotlinx.serialization.Serializable

@Serializable
object SplashScreen

@Serializable
object SignupScreen

@Serializable
object LoginScreen

@Serializable
object ResetPasswordEmailSendScreen

@Serializable
data class ResetOtpVerificationScreen(val email: String)

@Serializable
data class SignupOtpVerificationScreen(val email: String = "")

@Serializable
data class ResetPasswordScreen(val email: String, val otp: Int)

@Serializable
object SkipperScreen

@Serializable
object DetailsTakeScreen

@Serializable
object LoadingScreen

@Serializable
object HomeScreen

@Serializable
object ProfileUpdateScreen

@Serializable
data class PublicProfileScreen(val jobId: Long)

@Serializable
object PrivateProfileScreen

@Serializable
object PostJobScreen

@Serializable
data class PublicJobDetailsScreen(
    val jobId: Long,
    val showApplyButton: Boolean = true,
    val showDeleteButton: Boolean = false
)

@Serializable
object PrivateJobDetails

@Serializable
object MyJobsScreen

@Serializable
data class ViewApplicantsScreen(val jobId: Long)

@Serializable
object NotificationScreen
