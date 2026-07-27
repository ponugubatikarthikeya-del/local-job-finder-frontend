# Walkthrough - Unverified User Handling in Login

I have implemented the flow to handle unverified users during login. If a user attempts to log in and the backend returns a "not verified" error, the app will now automatically navigate them to the OTP verification screen, passing their email address.

## Changes Made

### 1. Data and Network Layer
- **[MODIFY] [AuthModels.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/domain/models/AuthModels.kt)**: Added `LoginRequest` data class.
- **[MODIFY] [AuthApiService.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/core/network/AuthApiService.kt)**: Added the `login` endpoint definition.
- **[MODIFY] [AuthRepository.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/domain/repository/AuthRepository.kt)** & **[AuthRepositoryImpl.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/core/repository/AuthRepositoryImpl.kt)**: Added the `login` method to handle API communication.

### 2. Login Logic
- **[MODIFY] [LoginEffect.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/auth/login/LoginEffect.kt)**: Updated `NavigateToOtpVerification` to include the user's `email`.
- **[MODIFY] [LoginViewModel.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/auth/login/LoginViewModel.kt)**:
    - Replaced simulated login with a real repository call.
    - Added logic to detect "not verified" errors from the server.
    - Triggers navigation to the OTP screen with the user's email if verification is required.
- **[MODIFY] [LoginLauncher.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/auth/login/LoginLauncher.kt)**: Updated to pass the email from the navigation effect.

### 3. Navigation and OTP Screen
- **[MODIFY] [AppNavGraph.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/navigation/AppNavGraph.kt)**: Configured the navigation route to pass the email to `SignupOtpVerificationScreen`.
- **[MODIFY] [OtpUiState.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/auth/OtpVerification/OtpUiState.kt)**: Removed the hardcoded default email address.
- **[MODIFY] [OtpViewModel.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/auth/OtpVerification/OtpViewModel.kt)**: Added support for the `onEmailChanged` event and updated OTP length validation to 6 digits.
- **[MODIFY] [OtpVerificationUi.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/auth/OtpVerification/OtpVerificationUi.kt)**: Increased OTP input boxes to 6 and adjusted styling for better fit.

## Verification
1.  **Login Attempt**: When you try to log in, if the server responds with a "not verified" message (e.g., "User not verified"), the app will now switch to the OTP screen.
2.  **Email Persistence**: The email used for login is carried over to the OTP screen, so the user doesn't have to re-enter it.
3.  **Hardcoded Email Removed**: The default "ponugubatikarthikeya@gmail.com" has been replaced with an empty string, and it will now be populated dynamically from the navigation arguments.
