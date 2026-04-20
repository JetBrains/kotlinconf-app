# Koin Migration Plan — Metro → Koin Annotations + Koin Compiler Plugin

Target: migrate the Kotlin Multiplatform project from Metro DI (`dev.zacsweers.metro`) to Koin Annotations with the Koin Compiler Plugin. Bridge strategy: Metro and Koin coexist during the migration; Metro is removed at the end.

Namespace: all classes live under `org.jetbrains.kotlinconf`; subpackage is shown per row.

---

## Module inventory

| Module | Metro used | Complexity | Notes |
|---|---|---|---|
| `:core` | No | Trivial | No DI. |
| `:backend` | No | Moderate | Already uses **Koin DSL** (6 modules). Separate sub-migration: DSL → Annotations. |
| `:app:ui-components` | No | Trivial | No DI. |
| `:app:shared` | Yes | Heavy | 4 graphs + 1 extension, full DI surface. See below. |
| `:app:androidApp` | Yes | Moderate | Single `AndroidAppGraph` + `MainActivity` binding. |
| `:app:desktopApp` | Yes | Trivial | Only `createGraphFactory<JvmAppGraph.Factory>()` in `main()`. |
| `:app:webApp` | Yes | Trivial | Only `createGraphFactory<WebAppGraph.Factory>()` in `main()`. |

---

## DI surface in `:app:shared`

Graph files (all in `org.jetbrains.kotlinconf.di`):
- `AppGraph.kt` (commonMain) — main graph, 6 `@Provides` + 1 `@Binds`, `@SingleIn(AppScope::class)` throughout.
- `YearGraph.kt` (commonMain) — child `@GraphExtension(YearScope::class)`, factory `create(@Provides @Year year: Int)`.
- `Qualifiers.kt` (commonMain) — `@Year`, `@FileStorageDir`, `@BaseUrl`.
- `BaseAndroidAppGraph.kt` (androidMain)
- `JvmAppGraph.kt` (jvmMain)
- `IosAppGraph.kt` (iosMain)
- `WebAppGraph.kt` (webMain)

---

## Full taxonomy (three buckets)

### Bucket 1 — ViewModels (17)

#### Plain (11) → `@KoinViewModel`

| VM | Package | Deps |
|---|---|---|
| `AboutConferenceViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` |
| `SpeakersViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` |
| `InfoViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` |
| `PartnersViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` |
| `GoldenKodeeViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` |
| `ScheduleViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService`, `TimeProvider` |
| `LicensesViewModel` | `org.jetbrains.kotlinconf.screens.licenses` | — |
| `SettingsViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` |
| `PrivacyNoticeViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` |
| `StartNotificationsViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` |
| `MapViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService`, `Logger` |

#### Assisted (6) → `@KoinViewModel` + `@InjectedParam`

| VM | Package | Injected deps | Assisted params |
|---|---|---|---|
| `SpeakerDetailViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` | `speakerId: SpeakerId` |
| `SessionViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` | `sessionId: SessionId` |
| `DocumentsViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` | `documentPath: String` |
| `FeedbackViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` | `sessionId: SessionId` |
| `GoldenKodeeFinalistViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` | `categoryId: AwardCategoryId`, `nomineeId: NomineeId` |
| `PartnerDetailViewModel` | `org.jetbrains.kotlinconf.screens` | `ConferenceService` | `partnerId: PartnerId` |

---

### Bucket 2 — AppScope singletons

#### Constructor-injected classes (10) → `@Single`

| Class | Package | Source set | Deps | Binds |
|---|---|---|---|---|
| `ConferenceService` | `org.jetbrains.kotlinconf` | commonMain | `ApplicationApi`, `ApplicationStorage`, `TimeProvider`, **`YearGraph.Factory`**, `LocalNotificationService`, `FlagsManager`, `CoroutineScope`, `Logger` | — |
| `FlagsManager` | `org.jetbrains.kotlinconf.flags` | commonMain | `Flags`, `ApplicationStorage`, `CoroutineScope` | — |
| `BufferedDelegatingLogger` | `org.jetbrains.kotlinconf.utils` | commonMain | `CoroutineScope` | `Logger` (via `@Binds`) |
| `ApplicationApi` | `org.jetbrains.kotlinconf.network` | commonMain | `HttpClient`, `Logger` | — |
| `ApplicationStorageImpl` | `org.jetbrains.kotlinconf.storage` | commonMain | `ObservableSettings`, `CoroutineScope`, `Logger` | `ApplicationStorage` |
| `AndroidLogger` | `org.jetbrains.kotlinconf.utils` | androidMain | — | — |
| `PermissionHandler` | `org.jetbrains.kotlinconf` | androidMain | `Logger` | — |
| `AndroidLocalNotificationService` | `org.jetbrains.kotlinconf` | androidMain | `TimeProvider`, `PermissionHandler`, `Context`, `@NotificationIcon Int`, `Logger` | `LocalNotificationService` |
| `IOSLocalNotificationService` | `org.jetbrains.kotlinconf` | iosMain | `TimeProvider`, `Logger` | `LocalNotificationService` |
| `ServiceWorkerLocalNotificationService` | `org.jetbrains.kotlinconf` | webMain | `TimeProvider` | `LocalNotificationService` |

#### Provider-based singletons (13) → `@Single` functions

**Common (`AppGraph`)** — `org.jetbrains.kotlinconf.di`:

| Provides | Deps |
|---|---|
| `CoroutineScope` (app) | — (`SupervisorJob + Dispatchers.Default`) |
| `@BaseUrl String` | `ApplicationStorage`, `Flags` |
| `HttpClient` | `ApplicationStorage`, `@BaseUrl String`, `Logger` |
| `TimeProvider` | `ApplicationStorage`, `Lazy<Logger>`, `Lazy<ApplicationApi>` |
| `Logger` (`@Binds`) | ← `BufferedDelegatingLogger` |
| `MetroViewModelFactory` | **DELETE** after migration — replaced by `koinViewModel()` |

**Android (`BaseAndroidAppGraph`)** — `org.jetbrains.kotlinconf.di`:

| Provides | Deps |
|---|---|
| `Context` | `Application` (graph input) |
| `ObservableSettings` | `Application` |
| `@FileStorageDir String` | `Application` |
| `NotificationPlatformConfiguration` | `@NotificationIcon Int` (graph input) |

**JVM (`JvmAppGraph`)** — `org.jetbrains.kotlinconf.di`:

| Provides | Deps |
|---|---|
| `ObservableSettings` | — |
| `@FileStorageDir String` | — |
| `LocalNotificationService` | — (returns `EmptyLocalNotificationService`) |
| `NotificationPlatformConfiguration` | — |

**iOS (`IosAppGraph`)** — `org.jetbrains.kotlinconf.di`:

| Provides | Deps |
|---|---|
| `ObservableSettings` | — |
| `@FileStorageDir String` | — |
| `NotificationPlatformConfiguration` | — |

**Web (`WebAppGraph`)** — `org.jetbrains.kotlinconf.di`:

| Provides | Deps |
|---|---|
| `ObservableSettings` | — |
| `NotificationPlatformConfiguration` | — |

#### Factory (unscoped, 1) → `@Factory`

| Class | Package | Source set | Deps | Notes |
|---|---|---|---|---|
| `AlarmBroadcastReceiver` | `org.jetbrains.kotlinconf` | androidMain | `LocalNotificationService` | Multibound as `BroadcastReceiver` via `@BroadcastReceiverKey(AlarmBroadcastReceiver::class)`. In Koin: register by qualifier, or keep a manual `Map<KClass<*>, BroadcastReceiver>` provider. |

---

### Bucket 3 — YearScope singletons (4) → `@Scope(YearScope::class) @Scoped`

| Class | Package | Source set | Deps | Binds |
|---|---|---|---|---|
| `YearlyApi` | `org.jetbrains.kotlinconf.network` | commonMain | `@Year Int`, `HttpClient`, `ApplicationStorage`, `Logger` | — |
| `YearlyStorageImpl` | `org.jetbrains.kotlinconf.storage` | commonMain | `@Year Int`, `ObservableSettings`, `AssetStorage` | `YearlyStorage` |
| `OkioAssetStorage` | `org.jetbrains.kotlinconf.storage` | nonWebMain | `@Year Int`, `@FileStorageDir String`, `Logger` | `AssetStorage` |
| `SettingsAssetStorage` | `org.jetbrains.kotlinconf.storage` | webMain | `@Year Int`, `ObservableSettings` | `AssetStorage` |

Scope parameter: `@Year year: Int`. In Koin, the scope is keyed by `scopeId` (e.g. `"2026"`), and the year can be passed via `parametersOf(year)` or stored as a scope property. Scope qualifier: `named<YearScope>()`.

---

## Qualifiers & scopes

All defined in `org.jetbrains.kotlinconf.di`:

| Metro | Koin equivalent |
|---|---|
| `@BaseUrl` | `named("baseUrl")` or type alias |
| `@FileStorageDir` | `named("fileStorageDir")` |
| `@Year` | Scope id on `YearScope` (or `named("year")` for the Int itself) |
| `AppScope` | Koin root (default `Single` lifetime). No scope block needed. |
| `YearScope` | `scope<YearScope> { ... }` — created per year, closed on year change |

---

## Summary counts

- **17 ViewModels** (11 plain + 6 assisted)
- **10 constructor-injected singletons** (AppScope)
- **13 provider functions** across 4 platform graphs (AppScope)
- **1 unscoped factory** (`AlarmBroadcastReceiver`)
- **4 constructor-injected singletons** (YearScope)
- **3 qualifiers** + **2 scopes** (`AppScope`, `YearScope`)

---

## Three buckets view

| Bucket | Count | Koin mapping |
|---|---|---|
| ViewModel | 17 (11 plain + 6 assisted) | `@KoinViewModel` (+ `@InjectedParam` for assisted) |
| AppScope singleton | 23 (10 `@Inject` classes + 13 `@Provides` fns) | `@Single` at module root |
| AppScope factory | 1 (`AlarmBroadcastReceiver`) | `@Factory` at module root |
| YearScope singleton | 4 | `@Scope(YearScope::class) @Scoped` inside a scoped block |

AppScope is just "Koin root" — no scope block needed, no scope creation/close to manage. The only real scope work is the YearScope block (4 classes + lifecycle at call sites).

---

## Design note — `ConferenceService` → `YearGraph.Factory`

The one genuine design smell in the DI graph: `ConferenceService` (AppScope) depends on `YearGraph.Factory` (child-graph factory). A root-scope service holding a child-graph factory is essentially a service-locator — the service reaches back into the container to spawn/replace scopes as the active year changes.

It exists because the active year is mutable state owned by the service. When the user switches year, `ConferenceService` tears down the old `YearGraph` and creates a new one.

**Cleaner targets** (post-migration, not during):

1. **`CurrentYearHolder` / `YearScopeManager`** — owns the active `Scope`, exposes `yearlyApi: StateFlow<YearlyApi>` / `yearlyStorage: StateFlow<YearlyStorage>`. `ConferenceService` depends on *that*, never on the factory.
2. **Lambda indirection** — inject `() -> YearlyApi` / `() -> YearlyStorage` into `ConferenceService`. The lambda resolves from the current scope. Service stays ignorant of scopes.
3. **Push year outward** — make year a method parameter on `ConferenceService`; caller opens the scope. Biggest refactor, cleanest result.

**For this migration**: keep the smell. In Koin that becomes injecting the `Koin` instance (or a `KoinComponent`) so `ConferenceService` can call `getKoin().createScope(...)`. Same shape, same smell, same behavior — guaranteed equivalence.

File the cleanup pass as follow-up after Metro is fully removed. Mixing a DI migration with an architectural refactor is how migrations go sideways.

---

## Migration plan

### Phase 0 — Toolchain (prerequisite)

1. Add Koin dependencies to `:app:shared` `build.gradle.kts`:
   - `io.insert-koin:koin-core`
   - `io.insert-koin:koin-annotations`
   - Koin Compiler Plugin (KSP2)
   - `io.insert-koin:koin-compose-viewmodel` for VMs in Compose
2. Create empty `@Module @ComponentScan("org.jetbrains.kotlinconf") class AppKoinModule` in `org.jetbrains.kotlinconf.di`.
3. Bootstrap `startKoin { modules(AppKoinModule().module) }` from `AppInit.kt` — Koin runs alongside Metro.
4. Pilot one VM end-to-end (`AboutConferenceViewModel`) to validate the toolchain.

### Phase 1 — AppScope root definitions (23 + 1)

Mechanical 3-line edits per class:
- `@Inject` + `@SingleIn(AppScope::class)` → `@Single`
- `@ContributesBinding(AppScope::class)` → `@Single` + `@Binds` (or use interface in class declaration)
- `@Provides` in graph files → top-level `@Single` function in a `@Module` per source set

Order: commonMain first, then per-platform modules. Metro graph keeps running; Koin picks up each migrated binding.

### Phase 2 — ViewModels (17)

Even faster — this phase *removes* more code than it adds:
- 11 plain VMs: swap class-level annotations → `@KoinViewModel`
- 6 assisted VMs: same, plus mark route params `@InjectedParam`, delete nested `Factory` interfaces
- Replace call sites: `viewModel(factory = metroFactory)` → `koinViewModel()`, assisted calls pass `parametersOf(routeArg)`
- Delete `MetroViewModelFactory`, `provideMetroViewModelFactory`, all `@ViewModelKey` / `@ContributesIntoMap` annotations, `metrox-viewmodel-compose` dependency

### Phase 3 — YearScope (4 + lifecycle)

Only tricky phase:
- Wrap the 4 classes in a `scope<YearScope> { ... }` block with `scoped` bindings
- Replace `YearGraph.Factory.create(year)` call sites with `koin.createScope(scopeId = "$year", qualifier = named<YearScope>())`
- Ensure symmetric `scope.close()` at the lifecycle points where the old `YearGraph` was released (leak risk otherwise)
- In `ConferenceService`, swap the `YearGraph.Factory` dependency for `Koin` (or a `KoinComponent`) — see design note above

### Phase 4 — Remove Metro

- Delete all `@DependencyGraph` interfaces (`AppGraph`, `JvmAppGraph`, `IosAppGraph`, `WebAppGraph`, `BaseAndroidAppGraph`, `AndroidAppGraph`, `YearGraph`)
- Drop Metro Gradle plugin + dependencies
- Replace `createGraphFactory<...>()` in `:app:desktopApp` / `:app:webApp` / `:app:androidApp` with `startKoin { modules(...) }`

### Phase 5 — `:backend` (separate effort)

`:backend` already uses Koin DSL (6 modules). Sub-migration DSL → Annotations is independent and can happen before or after the Metro work. Same target: `@Module @ComponentScan` + `@Single` / `@Factory`.

---

## Dynamic parts (non-standard DI behavior)

Beyond `initFlagsAndLogging`, `initNotifier`, and YearScope, there are other runtime behaviors worth calling out:

| # | Area | Description | Koin impact |
|---|---|---|---|
| 1 | Late-bound logger | `BufferedDelegatingLogger` buffers until `.attach(realLogger)` is called post-flags-load. Real impl chosen at runtime (`DebugLogger` vs `NoopProdLogger`). | None — stays a `@Single`; `.attach()` happens at the same call site. |
| 2 | `NotifierManager` | Static singleton; `initialize(config)` + `addListener(...)` run as side effects at startup. Only `NotificationPlatformConfiguration` is DI-managed. | None — preserve `initNotifier` verbatim. |
| 3 | YearGraph reactive lifecycle | `ConferenceService.init { }` recreates `YearGraph` whenever stored config changes, via `yearGraphFactory.create(year)` into `MutableStateFlow<YearGraph?>`. **No `.close()` is ever called on the previous graph.** | **High risk.** Replace with `koin.createScope(scopeId = "$year", qualifier = named<YearScope>())` and **`scope.close()` the previous one** on each update — Koin scopes don't self-collect. |
| 4 | `ConferenceService.init` startup jobs | Launches 4 coroutines: fetch remote config, watch local config → recreate YearGraph + load data + preload assets + verify policy + sync votes, run `timeProvider.run()`, sync notification settings to Firebase topics. | None — constructor stays the same. Flag as a latent design issue (heavy `init { }` block). |
| 5 | `applicationStorage.initialize()` | Manual imperative init called from `ConferenceService.init`. | None. |
| 6 | `Lazy<T>` in `provideTimeProvider` | Metro's `Lazy<Logger>` / `Lazy<ApplicationApi>` break a construction-time cycle. | Replace with `() -> X` parameters or plain `get<X>()` calls inside the provider — Koin resolves lazily through `get()` naturally. |
| 7 | Runtime-branching `@Provides` | `provideTimeProvider` and `provideBaseUrl` read `applicationStorage.getFlagsBlocking()` and branch between impls. | Direct — `single { if (…) A() else B() }` works the same. |
| 8 | Graph factory inputs | Every platform graph factory accepts runtime inputs: Android (`Application`, `@NotificationIcon Int`, `Flags`), JVM/iOS/Web (`Flags`). | Mixed idiom — see next section. |
| 9 | `PermissionHandler.initialize(activity)` | Takes an `Activity` after injection, tied to Android lifecycle. | None. |
| 10 | `AlarmBroadcastReceiver` multibinding | Registered via `@ContributesIntoMap(..., binding = binding<BroadcastReceiver>())` keyed by `@BroadcastReceiverKey(...)`. | Register as `@Factory` by qualifier, or keep a manual `Map<KClass<*>, BroadcastReceiver>` provider. |
| 11 | `LicensesViewModel` I/O at construction | Reads `Res.readBytes("files/aboutlibraries.json")` in flow init. Not DI-dynamic but worth knowing. | None. |

### Graph factory inputs — Koin idioms

Three different mechanisms depending on the input's shape:

| Metro graph input | Koin idiom | Reason |
|---|---|---|
| `Application` | `androidContext(application)` at `startKoin` | Built-in Koin-Android helper; gives access to both `Application` and `Context` |
| `@NotificationIcon Int` | `@Property("notification.icon")` | Primitive config value, fits Koin properties natively |
| `Flags` (platform) | `@Single` in a startup module | `Flags` is compared as a whole (`flags != platformFlags`) and flows through `StateFlow<Flags>` — spreading to properties would break identity comparison and the reactive flow |

Bootstrap shape:

```kotlin
startKoin {
    androidContext(application)
    properties(mapOf("notification.icon" to R.drawable.ic_notification))
    modules(
        module { single { platformFlags } },     // pre-built object
        AppKoinModule().module,
        AndroidKoinModule().module,
    )
}
```

Consumer in `AndroidKoinModule`:

```kotlin
@Single
fun notificationPlatformConfiguration(
    context: Context,
    @Property("notification.icon") iconRes: Int,
): NotificationPlatformConfiguration = NotificationPlatformConfiguration.Android(
    notificationIconResId = iconRes,
    showPushNotificationWhenAppInForeground = false,
)
```

Same `Flags` single pattern applies to JVM / iOS / Web `startKoin` blocks — each platform's `main()` provides its own `Flags` instance.

---

## Ranked migration risks

| Risk | Area | Level |
|---|---|---|
| YearGraph reactive lifecycle + missing `scope.close()` | Dynamic part #3 | **High** — latent leak, will surface in Koin |
| Graph factory inputs (Application, icon, Flags) | Dynamic part #8 | **Medium** — needs a startup block per platform |
| `ConferenceService → YearGraph.Factory` coupling | Design note | **Medium** — preserve during migration, refactor after |
| `Lazy<T>` → Koin lambda / `get()` | Dynamic part #6 | **Low** — mechanical |
| Runtime branching + multibinding | Dynamic parts #7, #10 | **Low** — direct Koin equivalents |
| Init sequencing outside DI (logger attach, notifier, storage init, permissions) | Dynamic parts #1, #2, #4, #5, #9 | **None** — preserve as-is |

---

## Suggested module layout (Koin side)

One `@Module` per source set, all in `org.jetbrains.kotlinconf.di`:

- `AppKoinModule` (commonMain) — common singletons, common VMs, YearScope block
- `AndroidKoinModule` (androidMain) — android singletons + `BaseAndroidAppGraph` providers
- `JvmKoinModule` (jvmMain) — JVM singletons + `JvmAppGraph` providers
- `IosKoinModule` (iosMain) — iOS singletons + `IosAppGraph` providers
- `WebKoinModule` (webMain) — web singletons + `WebAppGraph` providers

Each includes `AppKoinModule` via `includes = [...]` or the platform `startKoin` call passes both modules.