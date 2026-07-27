# Implementation Plan - Splash Screen Bug Fix & Flow Verification

Address the issue where the "reconnecting" screen overrides the splash screen on app open, and verify the integrated login-to-home flow.

## User Review Required

> [!IMPORTANT]
> The Global Network Overlay will be suppressed while the splash animation is running to ensure the user always sees the logo first.

## Proposed Changes

### Feature: Splash UI Fix

#### [MODIFY] [AppNavGraph.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/navigation/AppNavGraph.kt)
- Add a check to the **GLOBAL NETWORK OVERLAY** conditional.
- Only show the overlay if:
  - (Network is unavailable OR stabilization is active)
  - **AND** (The current destination is NOT `SplashScreen` OR `isAnimationFinished` is true).
- This ensures that on cold start, the user sees the logo animation even if they are offline. The error screen will only appear *after* the animation finishes.

### Feature: Flow Verification

#### [VERIFY] [OtpViewModel.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/auth/OtpVerification/OtpViewModel.kt)
- Confirm `performSignupOtpVerification` calls `repository.verifyOtp`.
- Confirm it saves tokens on success.
- Confirm it sends `VerificationSuccess`.

#### [VERIFY] [AppNavGraph.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/navigation/AppNavGraph.kt)
- Confirm `SignupOtpVerificationScreen`'s success callback navigates to `LoadingScreen`.
- Confirm `LoadingScreen` correctly handles initialization before moving to `HomeScreen`.

#### [VERIFY] [MainViewModel.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/main/MainViewModel.kt)
- Confirm `checkAuthStatus` correctly routes `Authenticated` users to `SplashState.Authenticated`.
- Confirm `AppNavGraph` reacts to `Authenticated` by navigating to `LoadingScreen`.

## Verification Plan

### Manual Verification
1.  **Cold Start (Offline)**: Turn off internet, open app.
    - *Expected*: Splash logo animation plays first.
    - *Expected*: Only after animation finishes, the "No Internet Connection" screen appears.
2.  **Login Recovery Flow**: Login with unverified account.
    - *Expected*: Moves to OTP screen.
    - *Expected*: Verifying OTP moves to Loading screen, then Home.
3.  **App Re-open (Authenticated)**: Close app, re-open.
    - *Expected*: Splash animation plays -> Loading screen -> Home.
