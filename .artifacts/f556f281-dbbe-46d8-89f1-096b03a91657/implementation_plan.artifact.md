# Implementation Plan - HomeUi Refinement & Bug Fixes

This plan addresses identified bugs and code quality issues in `HomeUi.kt`.

## User Review Required

> [!IMPORTANT]
> - I will be standardizing the navigation parameter name to `onNavigateToPublicProfile`.
> - I will be moving the hardcoded "Quick Apply" button color to a themed color for better dark mode support.

## Proposed Changes

### [Component] Home Feature

#### [MODIFY] [HomeUi.kt](file:///C:/Users/karth/AndroidStudioProjects/LocalJobFinder/app/src/main/java/com/linker/app/feature/screens/home/HomeUi.kt)
- **Fix Navigation Bug**: Correctly pass the navigation callback from `HomeUi` to `JobCardItem`.
- **State Optimization**: Wrap dummy data (`categories`, `jobs`) in `remember` to prevent unnecessary allocations during recomposition.
- **Clean Deprecations**: Replace deprecated `capitalize()` with Kotlin's modern string manipulation.
- **Theming Fixes**:
    - Replace hardcoded `Color.Green` in "Quick Apply" button with a theme-aware color (e.g., `MaterialTheme.colorScheme.primary` or a success color if defined).
    - Standardize spacing and padding in the `JobCardItem`.
- **Error Handling**: Fix the missing scope for `onNavigateToPublicProfile` inside the `JobCardItem` component.

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors remain.

### Manual Verification
- Deploy to device/emulator.
- Verify that clicking the "Person" icon or username in a Job Card triggers navigation (if hooked up in `AppNavGraph`).
- Verify that the layout remains consistent across different screen sizes.
