# Walkthrough - Fixing HTTP 500 and Redundant API Calls

I have implemented a set of fixes to address the HTTP 500 error when fetching nearby jobs and to optimize the job fetching logic to prevent redundant API calls.

## Changes Made

### 1. HTTP 500 Fix (Query Parameter Renaming)
The backend was failing because of incorrect query parameter names. I updated the `JobApiService` to use standard naming:
- `lat` -> `latitude`
- `lng` -> `longitude`
- Coordinates are now passed as `Double` instead of `BigDecimal` for cleaner URL serialization.

### 2. Redundant API Call Optimization
To stop the app from fetching jobs "again and again" during navigation, logout, or state updates:
- **`TokenManager`**: Added `distinctUntilChanged()` to location flows (`latitude`, `longitude`, `address`). This ensures that if the DataStore emits the same value (e.g., when the app starts or a preference is set to its existing value), the downstream collectors are not re-triggered.
- **`HomeViewModel`**:
    - Added `distinctUntilChanged()` to the combined location flow.
    - Implemented a `lastFetchedCoords` cache to explicitly prevent re-fetching if the location hasn't changed since the last network request.
- **`LoadingViewModel`**: Removed the parallel job fetch. Since `HomeViewModel` now efficiently handles the fetch on arrival, the pre-fetch was redundant and often caused a double-triggering of the API.

## Verification Results

### Automated Check
- The `JobRepositoryImpl` now correctly calls the updated API interface.
- Unused imports and properties in `LoadingViewModel` and `JobRepositoryImpl` have been cleaned up.

### Manual Verification Guidance
- **Screen Entry**: Observe that `fetchNearbyJobs` is called exactly once when the Home screen is opened after a fresh login.
- **Navigation**: Navigate to the Profile screen and back to Home. Observe that no new API call is triggered.
- **Logout**: Perform a logout. Observe that no job fetch is triggered during the logout process.
- **Refresh**: Verify that swipe-to-refresh on the Home screen still triggers a manual fetch as expected.
