# Implementation Plan - Login to OTP for Unverified Users

This plan addresses the requirement to navigate unverified users to the OTP screen during login, passing their email address. It also ensures that the OTP screen correctly receives and displays the email from both the Signup and Login flows.

## Proposed Changes

### [Component: Data Models]

#### [MODIFY] [AuthModels.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/domain/models/AuthModels.kt)
- Add `LoginRequest` data class.

### [Component: Network Layer]

#### [MODIFY] [AuthApiService.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/core/network/AuthApiService.kt)
- Add `login` method to `AuthApiService`.

### [Component: Repository Layer]

#### [MODIFY] [AuthRepository.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/domain/repository/AuthRepository.kt)
- Add `login` method to the interface.

#### [MODIFY] [AuthRepositoryImpl.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/core/repository/AuthRepositoryImpl.kt)
- Implement `login` method in `AuthRepositoryImpl`.

### [Component: Auth - Login Feature]

#### [MODIFY] [LoginEffect.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/auth/login/LoginEffect.kt)
- Change `NavigateToOtpVerification` to include an `email` parameter: `NavigateToOtpVerification(val email: String)`.

#### [MODIFY] [LoginViewModel.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/auth/login/LoginViewModel.kt)
- Inject `AuthRepository` and `TokenManager`.
- Update `perform()` to call `repository.login()`.
- Handle "User not verified" error (e.g., if message contains "not verified") and trigger `NavigateToOtpVerification(email)`.

#### [MODIFY] [LoginLauncher.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/auth/login/LoginLauncher.kt)
- Update `NavigateToOtpVerification` callback to accept an `email` string.

### [Component: Navigation]

#### [MODIFY] [AppNavGraph.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/navigation/AppNavGraph.kt)
- Update `LoginLauncher`'s `NavigateToOtpVerification` callback in `AppNavGraph` to navigate to `SignupOtpVerificationScreen(email)`.

### [Component: Auth - OTP Feature]

#### [MODIFY] [OtpViewModel.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/auth/OtpVerification/OtpViewModel.kt)
- Ensure the email passed via `SignupOtpVerificationScreen` is correctly set in the state. (This should already work but I'll double check).

## Verification Plan

### Manual Verification
1. **Signup Flow**: Sign up with a new email. Verify it navigates to OTP screen with the email pre-filled.
2. **Login Flow (Unverified)**: Attempt to log in with an unverified account. Verify it navigates to OTP screen with the correct email pre-filled.
3. **Login Flow (Verified)**: Attempt to log in with a verified account. Verify it navigates to Home screen.
