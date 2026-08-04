# Fix NullPointerException in Profile Fetching

The application crashes with a `NullPointerException` when fetching the user profile because the `dateOfBirth` field is returned as `null` from the API, but it's defined as a non-null `String` in the data models. Since the project uses Gson for Retrofit conversion, Kotlin's non-nullability constraints are bypassed during deserialization into `UserDto`, but enforced when `UserDto` is mapped to `UserEntity`.

## Proposed Changes

### Core Models

#### [MODIFY] [UserDto.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/core/data/models/UserDto.kt)
Make `dateOfBirth` nullable to allow for cases where the API returns `null`.

#### [MODIFY] [UserEntity.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/core/database/entity/UserEntity.kt)
Make `dateOfBirth` nullable to support storing profiles with missing birth dates in the local database.

#### [MODIFY] [UserModels.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/core/data/models/UserModels.kt)
Make `User.dateOfBirth` nullable.

---

### UI Components

#### [MODIFY] [PrivateProfileUi.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/screens/profile/private_profile/PrivateProfileUi.kt)
Handle the nullable `dateOfBirth` when displaying it in the `DetailCard`, providing a fallback string like "Not provided".

## Verification Plan

### Automated Tests
- I will attempt to build the project to ensure no compilation errors were introduced by the change in types.

### Manual Verification
- Deploy the app and navigate to the profile screen.
- Verify that the crash no longer occurs when `dateOfBirth` is missing.
- Verify that "Not provided" (or similar) is shown in the UI for the Birth Date.
