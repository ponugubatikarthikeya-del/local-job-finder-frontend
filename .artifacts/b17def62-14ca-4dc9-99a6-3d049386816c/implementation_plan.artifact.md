# Fix ManagedChannel Leak (gRPC)

The error `*~*~*~ Previous channel {0} was garbage collected without being shut down! ~*~*~*` is a resource leak reported by gRPC. It occurs when a `ManagedChannel` (used internally by Firebase Messaging and other Play Services) is garbage collected before being explicitly shut down.

This is a known issue in older versions of Firebase and Play Services. Upgrading to the latest stable versions and adding the `grpc-android` helper library is the recommended fix.

## Proposed Changes

### [Component Name] Dependency Updates

Updating Firebase BOM, Coroutines Play Services, and adding gRPC Android support.

#### [MODIFY] [libs.versions.toml](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/gradle/libs.versions.toml)
- Upgrade `firebaseBom` from `33.1.2` to `34.17.0`.
- Upgrade `firebaseMessagingKtx` from `24.1.0` to `24.1.2`.
- Upgrade `kotlinxCoroutinesPlayServices` from `1.8.1` to `1.11.0`.
- Add `grpc-android` library version `1.83.1`.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/build.gradle.kts)
- Add `implementation(libs.grpc.android)` to ensure gRPC is aware of the Android lifecycle and connectivity, which helps in proper channel shutdown.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure dependencies resolve correctly.

### Manual Verification
- Deploy the app and monitor Logcat for the `ManagedChannel allocation site` error.
- Perform a logout (which triggers FCM token deletion) and verify no gRPC leak warnings appear.
