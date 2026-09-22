# KotlinConf App — Agent Instructions

KMP + Compose Multiplatform (Android, iOS, Desktop, Web) + Ktor Server (JVM).

## Build & Test Workflow

- **Build tool:** Kotlin Toolchain via `./kotlin` (not Gradle). Modules in `module.yaml` / `project.yaml`.
- **Dependencies:** `libs.versions.toml` (`$libs.*`). For new KMP libraries, query the `klibs` MCP server.

### 1. Compose Hot Reload (Primary Loop for Client & UI)
**Start CHR in background before starting tasks:**
```bash
./kotlin run --compose-hot-reload
```
- **Auto-compile & instant feedback:** File changes compile automatically on save. Check MCP `status` (`lastErrorDetails`) or call `reload` for compiler diagnostics — faster than running `./kotlin build`.
- **UI verification:** Use `take_screenshot`, `get_semantic_tree`, `get_ui_error`, `click`, `type`, `scroll`.
- **Stopping:** Kill processes via PID in `build/hot-reload-app.pid`.

### 2. Targeted Commands (Non-shared / Backend / Platform / Tests)
*Use only when changing non-shared code, platform actuals, backend, or running tests:*

| Task | Build | Test |
|---|---|---|
| Backend | `./kotlin build -m backend` | `./kotlin test -m backend` |
| Core | `./kotlin build -m core` | `./kotlin test -m core -p jvm` |
| Shared (JVM) | — | `./kotlin test -m shared -p jvm` |
| Android | `ANDROID_HOME=… ./kotlin build -m androidApp` | `./kotlin test -m shared -p android` |
| iOS | `./kotlin build -m shared -p iosSimulatorArm64` | `./kotlin test -m shared -p iosSimulatorArm64` |
| Full Check | `./kotlin build` | `./kotlin check` |

## Project Structure & Architecture

- `core`: Shared models (`ConferenceInfo`, `GoldenKodeeData`), timezone handling, serialization.
- `app/ui-components`: Compose theme (`KotlinConfTheme`, `Colors`, `Typography`) & reusable UI widgets.
- `app/shared`: Screens, ViewModels, navigation (`androidx.navigation3`), Metro DI (`@DependencyGraph`), Ktor client, Settings.
- `app/desktopApp`, `app/androidApp`, `app/iosApp`, `app/webApp`: Platform entry points.
- `backend`: Ktor server with Exposed, Koin DI, HikariCP, H2/PostgreSQL.
- **Layout:** `<module>/src/` (common), `src@<platform>/` (platform-specific), `composeResources/` (`Res`).
