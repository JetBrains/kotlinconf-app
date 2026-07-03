# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This Is

The official KotlinConf app — a Kotlin Multiplatform project serving Android, iOS, JVM desktop, and WebAssembly clients, backed by a Ktor server. All client UI is shared via Compose Multiplatform.

## Build & Run Commands

```bash
# Run desktop app (hot reload enabled)
./gradlew :app:desktopApp:hotRun -DmainClass=org.jetbrains.kotlinconf.MainKt

# Run web app (wasmJs target, development mode)
./gradlew :app:webApp:wasmJsBrowserDevelopmentRun

# Run backend server
./gradlew :backend:run

# Run backend tests
./gradlew :backend:test

# Run shared module tests (JVM target)
./gradlew :app:shared:jvmTest

# Run core module tests
./gradlew :core:jvmTest

# Bump version across all platforms + generate library definitions
./gradlew prepareRelease
```

Android uses the `app.androidApp` run configuration in IDE; iOS uses `KotlinConfAppScheme`.

## Module Structure

```
:core                 — Shared data models (Conference, Session, Speaker, VoteInfo, Score, AppConfig, etc.)
                        Used by both client and backend. No UI, no platform code.

:app:ui-components    — Reusable Compose UI components (KotlinConfTheme, typography, colors, buttons, cards, etc.)
                        Has its own resource class (UiRes) separate from the main app.

:app:shared           — Main KMP client: all screens, ViewModels, navigation, ConferenceService, DI graph.
                        Targets: android, jvm, iosArm64, iosSimulatorArm64, wasmJs, js.

:app:androidApp       — Android entry point only. Wires up AndroidAppGraph.
:app:desktopApp       — JVM desktop entry point. Uses Compose Desktop.
:app:webApp           — Wasm+JS entry points. Targets wasmJs and js, both browser.

:backend              — Ktor/Netty server. Fetches data from Sessionize, stores votes/users in DB.
```

## Client Architecture

**Dependency Injection — Metro**

The app uses [Metro](https://github.com/ZacSweers/metro) (not Koin) for DI on the client. Key interfaces:

- `AppGraph` — top-level DI graph (`AppScope`), one per app process. Holds `ConferenceService`, `FlagsManager`, `TimeProvider`, etc.
- `YearGraph` — a *scoped sub-graph* (`YearScope`) created per active conference year. Holds `YearlyApi` and `YearlyStorage`. Created by `YearGraph.Factory` inside `ConferenceService`.
- Platform-specific graph (e.g. `AndroidAppGraph`) — annotated `@DependencyGraph(AppScope::class)`, created at app startup, implements `AppGraph`.

ViewModels are registered into Metro's map multibinding:
```kotlin
@ContributesIntoMap(AppScope::class)
@ViewModelKey
class ScheduleViewModel(...) : ViewModel()
```

**ConferenceService**

`ConferenceService` is the central app singleton. It:
- Fetches `AppConfig` (current year) from the backend on startup and stores it locally.
- Creates a `YearGraph` for the active year, giving access to that year's `YearlyApi` and `YearlyStorage`.
- Exposes `StateFlow`s: `agenda`, `speakers`, `conferenceInfo`, `goldenKodeeData`, `votes`, `currentYear`.
- Handles favorites, voting, policy acceptance, notification scheduling, and asset caching.

**Navigation**

Uses `androidx.navigation3`. Routes are `@Serializable sealed interface AppRoute` types defined in `Routes.kt`. `TopLevelRoute` marks bottom-nav destinations. `NavHost.kt` maps routes to screen composables.

**Screens / ViewModels pattern**

Each screen has a corresponding `*ViewModel` that takes `ConferenceService` as a dependency, maps service `StateFlow`s into `uiState: StateFlow<ErrorLoadingState<...>>`, and exposes user-action functions. The screen Composable receives the ViewModel via Metro's ViewModel factory.

**Flags**

`Flags` is a `@Serializable data class` stored via `FlagsManager`. Compose screens access it via `LocalFlags`. Includes developer-mode options like `useFakeTime`, `debugLogging`, and `useFakeGoldenKodeeData`.

**Source Set Hierarchy**

The `app:shared` module defines custom intermediate source sets beyond the default hierarchy:
- `nonAndroidMain` — shared by `iosMain`, `jvmMain`, and `webMain` (excludes Android)
- `nonWebMain` — shared by `androidMain`, `iosMain`, and `jvmMain` (has `okio` dependency; web can't use it)
- `webMain` — shared by `wasmJsMain` and `jsMain` (JS-specific timezone shim, browser APIs)

## Backend Architecture

- **Framework**: Ktor with Netty engine
- **DI**: Koin (`diModule()` in `DiModule.kt`)
- **Database**: Exposed ORM + HikariCP. PostgreSQL in production; H2 file-based fallback when no `database.host` is configured (used for local dev and tests).
- **Migrations**: `MigrationRunner` applies SQL migrations in order.

**Route structure** (`RoutesModule.kt`):
- Year-agnostic: `/healthz`, `/time`, `/admin/*`, `/config`
- Year-prefixed: `/{year}/conference`, `/{year}/conference-info`, `/{year}/schedule`, `/{year}/vote`, `/{year}/sign`, `/{year}/golden-kodee`, `/{year}/documents/*`, etc.

**Data flow**: `SessionizeService` polls the Sessionize API on a configurable interval and caches schedule data in-memory. `KotlinConfRepository` handles all DB operations (users, votes, feedback, signed policies) using `suspendTransaction`.

## Key Conventions

**Version management**: Version numbers must stay in sync across Android `build.gradle.kts`, iOS `project.pbxproj`, iOS `Info.plist`, and `app/shared/src/commonMain/composeResources/values/version.xml`. Use `./gradlew prepareRelease` (which calls `updateVersion` and `exportLibraryDefinitions`) rather than editing these files manually.

**Resources**: `app:shared` uses resource class `org.jetbrains.kotlinconf.generated.resources` (auto-generated). `app:ui-components` uses its own `UiRes` / `org.jetbrains.kotlinconf.ui.generated.resources`. Don't mix them.

**Backend tests**: Use `ktor-server-test-host` with `test-application.yaml` config (H2 database, no external dependencies).
