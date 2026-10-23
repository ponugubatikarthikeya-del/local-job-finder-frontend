# 💼 Local Job Finder (Android App)

**Local Job Finder** is a modern, production-grade native Android application designed to connect local workers and gig job seekers with nearby employment and service opportunities in real time. 

Built with **Kotlin**, **Jetpack Compose + Material 3**, **Clean Architecture + MVI (Model-View-Intent)**, **Paging 3 + RemoteMediator** for offline-first caching, **Room Database**, **Dagger Hilt**, **Retrofit**, and **OkHttp Authenticator**.

---

## ✨ Key Features

- 📍 **Location-Based Job Discovery**: Discover nearby gig work and services based on real-time geographical coordinates with accurate distance calculations.
- 🌍 **Global Geocoding (3-Tier Lookup)**: Flexible 3-tier location lookup supporting any city on Earth (Open-Meteo REST API + Android Native Geocoder + Region Fallback).
- 🏷️ **Category & Keyword Search**: Filter available job listings by categories (*Plumbing, Cleaning, Electrician, Painting, Gardening, Carpentry, etc.*) or explicit keyword search (triggered via keyboard Enter, search arrow, or category pills).
- 🔄 **Offline-First Pagination (Paging 3 + RemoteMediator)**: Smooth infinite-scrolling feeds backed by Room database caching for offline capability and single-source-of-truth network sync with zero out-of-band cache mutations.
- ➕ **Job Posting & Management**: Post new job listings with salary, schedule time, payment type choice chips, and interactive description inputs.
- 📝 **My Jobs Dashboard**: Manage your posted jobs, monitor status (`OPEN`, `COMPLETED`), or delete outdated listings.
- 👥 **Applicant Tracking & Management**: View applicants for your posted jobs with real-time status management (`Accept` / `Reject`) and visual status indicators (Green Checkmark for Accepted, Red Cross for Rejected).
- 👤 **Smart Profile Navigation**: Contextual profile navigation—tapping a job poster's avatar automatically opens your **Private Profile** if it's your own job, or their **Public Profile** if posted by another user.
- 🔐 **Authentication & Security**: Email & OTP verification, JWT access/refresh token management with OkHttp native `AuthAuthenticator` (double-checked locking, zero `runBlocking` deadlocks), and secure DataStore storage.
- 🎨 **Material 3 Design & Dual Theme**: Full Light & Dark theme support, Poppins typography, high-contrast Slate design palette, and smooth Compose state animations.

---

## 🛠️ Architecture & Tech Stack

### **Architecture Pattern: Clean Architecture + MVI (Model-View-Intent)**
The app strictly adheres to Unidirectional Data Flow (UDF) with MVI:
- **UiState**: Immutable data class holding screen state and Paging flows exposed via `StateFlow`.
- **UiEvent**: Sealed interfaces representing user actions/intents.
- **UiEffect**: Sealed interfaces representing one-off side effects (navigation, toasts, dialogs) delivered over `Channel`.

```
┌───────────────────────────────────────────────────────────┐
│                    UI Layer (Jetpack Compose)             │
│   Screen Composables ◄─── UiState / UiEffect ─── ViewModel│
└─────────────────────────────┬─────────────────────────────┘
                              │ UiEvent
                              ▼
┌───────────────────────────────────────────────────────────┐
│                    Domain & Data Layer                    │
│   Repository ◄─── Paging3 Pager ◄─── RemoteMediator       │
│        │                                    │             │
│        ▼                                    ▼             │
│   TokenManager (DataStore)           Room DB / Retrofit   │
└───────────────────────────────────────────────────────────┘
```

### **Tech Stack Matrix**

| Layer / Concern | Tech / Library | Purpose |
| :--- | :--- | :--- |
| **Language** | Kotlin 2.2 | Pure Kotlin codebase |
| **UI Framework** | Jetpack Compose + Material 3 | Declarative UI rendering |
| **Typography** | Poppins Font Family | Custom typography scale across all text elements |
| **Architecture** | Clean Architecture + MVI | Unidirectional Data Flow (State, Event, Effect) |
| **Dependency Injection** | Dagger Hilt | Compile-time dependency injection |
| **Pagination & Caching** | Paging 3 + `RemoteMediator` | Offline-first pagination with Room database |
| **Local Database** | Room Database (v13) | SQLite ORM for caching jobs, applicants, and user profiles |
| **Local Persistence** | Jetpack DataStore Preferences | Thread-safe storage for JWT tokens (@Volatile cache), location, and device IDs |
| **Networking** | Retrofit 2 + OkHttp + Gson | HTTP REST client with native `AuthAuthenticator`, `AuthInterceptor` & `AuthEventBus` |
| **Navigation** | Navigation Compose | Type-safe declarative screen routing |

---

## ⚡ Concurrency & Synchronization Architecture

### 1. Zero `runBlocking` Thread Safety
- Token reads inside OkHttp interceptors and authenticators use `@Volatile` in-memory cached fields from `TokenManager`.
- Network request threads execute O(1) synchronous token lookups without blocking thread pools or causing deadlocks.

### 2. Double-Checked Mutual Exclusion Token Refresh
- 401 Unauthorized responses are intercepted by OkHttp's native `AuthAuthenticator`.
- Employs a synchronized `ReentrantLock` block to ensure only a single concurrent request performs token refresh while queued requests replay using the updated access token.
- Refresh HTTP client is strictly isolated from `AuthInterceptor`/`AuthAuthenticator` to prevent infinite refresh loops.

### 3. Scoped Paging 3 Cache Isolation
- `LoadType.REFRESH` always fetches starting page (`page = 0`) to guarantee cache consistency.
- Cache wiping and insertions are scoped (e.g., `clearRemoteKeysForJob(jobId)`) and wrapped in atomic `database.withTransaction {}` blocks.

---

## 📂 Project Structure

```
com.linker.app
│
├── core/                         # Core infrastructure & shared modules
│   ├── data/                     # DTOs, Requests, and Paged Responses
│   ├── database/                 # Room Database setup
│   │   ├── app_database/         # JobDatabase class declaration (v13)
│   │   ├── converters/           # TypeConverters for Instant & BigDecimal
│   │   ├── dao/                  # Room DAOs (JobDao, MyJobDao, ApplicantDao, UserDao)
│   │   ├── entity/               # Entities & RemoteKeys tables
│   │   └── paging/               # RemoteMediator implementations
│   ├── datastore/                # TokenManager with @Volatile thread-safe cache
│   ├── network_api/              # Retrofit API Services, AuthAuthenticator, AuthInterceptor & Hilt DI
│   ├── repository/               # Repository interfaces
│   └── repository_impl/          # Repository implementations
│
├── feature/                      # Feature Modules
│   ├── auth/                     # Auth feature (Login, Signup, OTP, Reset Password, Loading)
│   └── screens/                  # Application Screens
│       ├── home/                 # Home Screen (Job discovery, category filtering, search)
│       ├── job/                  # Post Job, My Jobs, Public/Private Details, View Applicants
│       └── profile/              # Private Profile, Public Profile, Profile Update with DatePicker
│
├── main/                         # MainActivity, MainViewModel, Splash
├── navigation/                   # AppNavGraph, AppNavHost, ScreenNames
├── ui/                           # Themes, Color Palette, Poppins Typography, Shared Components
└── LocalJobFinderApplication.kt  # Application Entry Point (@HiltAndroidApp)
```

---

## 💾 Database Schema (`JobDatabase`, Version 13)

| Table | Primary Key | Description |
| :--- | :--- | :--- |
| `jobs` | `id` (Long) | Nearby public jobs cached for offline browsing with category & time indexing |
| `jobs_remote_keys` | `jobId` (Long) | Paging3 prev/next page keys for jobs feed |
| `my_jobs` | `id` (Long) | User's own posted jobs cached for offline management |
| `my_jobs_remote_keys` | `id` (Long) | Paging3 prev/next page keys for user's posted jobs |
| `applicants` | `applicantId` (Long) | Applicants for a specific job with candidate ID preservation & job status tracking |
| `applicants_remote_keys` | `applicantId` (Long) | Paging3 prev/next page keys scoped per `jobId` |
| `user_profile` | `id` (Long) | User profile details |

---

## 🚀 Getting Started & Building

### **Prerequisites**
- **Android Studio**: Ladybug (2024.2.1) or newer
- **JDK Version**: Java 17 or Java 21
- **Min SDK**: Android 6.0 (API Level 26)
- **Target SDK**: Android 15 / API Level 37

### **Build Commands**

```bash
# Clone the repository
git clone https://github.com/your-username/local-job-finder-frontend.git
cd local-job-finder-frontend

# Build Debug APK
./gradlew app:assembleDebug

# Run Unit Tests
./gradlew testDebugUnitTest
```

---

## Related Project

The Backend client is maintained in a separate repository.

- Backend repo : [local job finder backend](https://github.com/ponugubatikarthikeya-del/local-job-finder-frontend)

---

## 📄 License & Credits

Built with ❤️ using modern native Android technologies.
