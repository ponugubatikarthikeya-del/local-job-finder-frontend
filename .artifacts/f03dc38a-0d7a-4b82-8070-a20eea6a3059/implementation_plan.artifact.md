# Implementation Plan - Smart AUTO-Header Feature

Implement a "Smart Header" system where `X-Device-Id` and JWT `Authorization` tokens are only sent if the API interface requests them using placeholders.

## User Review Required

> [!IMPORTANT]
> - **AUTO-Fill Logic**: I will change the Interceptor to only send `X-Device-Id` and `Authorization` tokens if the request already contains a placeholder value (like `DEFAULT_DEVICE` or `Bearer AUTO`).
> - **Default-Off**: If an endpoint (like `refreshTokens`) does not include these headers in its interface, they will **not** be sent by the Interceptor.

## Proposed Changes

### Network Layer

#### [MODIFY] [AuthInterceptor.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/core/network_api/Interceptor/AuthInterceptor.kt)
- Remove global/automatic header attachment.
- Add logic to:
  1. Detect if `X-Device-Id` is `DEFAULT_DEVICE` and replace it with the real Device ID.
  2. Detect if `Authorization` is `Bearer AUTO` and replace it with the real Access Token.
  3. This ensures headers are ONLY sent when the backend "wants" them (as signaled by the API interface).

#### [MODIFY] [AuthApiService.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/core/network_api/api/AuthApiService.kt)
- Update endpoints to use placeholders where needed.
- `logout` will use `@Header("X-Device-Id") deviceId: String = "DEFAULT_DEVICE"` and `@Header("Authorization") token: String = "Bearer AUTO"`.
- `refreshTokens` will remain as is (only receiving the Refresh Token explicitly).

#### [MODIFY] [TokenRefreshService.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/core/network_api/api/TokenRefreshService.kt)
- Ensure it remains clean of `X-Device-Id`.

### Navigation & State

#### [MODIFY] [AppNavGraph.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/navigation/AppNavGraph.kt)
- Add global logout observer: If `splashState` becomes `Unauthenticated`, navigate to `LoginScreen`.

## Verification Plan

### Automated Tests
- Build verification for header replacement logic.

### Manual Verification
1. **Placeholder Test**: Verify that sending `DEFAULT_DEVICE` in Retrofit results in the real ID being sent over the wire.
2. **Refresh Test**: Verify `refresh/token` carries only the Refresh Token.
3. **Logout Test**: Verify `logout` carries both the real Device ID and the real Access Token.
