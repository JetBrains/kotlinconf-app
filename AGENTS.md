# KotlinConf App — Agent Instructions

KMP + Compose Multiplatform (Android, iOS, Desktop, Web) + Ktor Server (JVM).

## Build & Test Workflow

- **Build tool:** Kotlin Toolchain via `./kotlin` (not Gradle). Modules in `module.yaml` / `project.yaml`.
- **Dependencies:** `libs.versions.toml` (`$libs.*`). For new KMP libraries, query the `klibs` MCP server.

### 1. Compose Hot Reload (Primary Loop for Client & UI)
**Reuse the existing CHR session.** Check MCP `status` and `list_windows` before launching anything. Only when no app is connected and no app process is still running, start CHR in the background:
```bash
./kotlin run --compose-hot-reload
```
- **Compile & reload:** Inspect `status.buildContinuous`. When false, call `reload` after edits; when true, use `await_reload`. Check `lastErrorDetails` for compiler diagnostics. A successful compilation with `reloaded: false` does not prove the running UI reflects the edit; verify the changed UI.
- **UI verification:** Use `take_screenshot`, `get_semantic_tree`, `get_ui_error`, `click`, `type`, `scroll`.
- **Restarting:** If a reload leaves stale UI (including after adding resources), use MCP `restart` on the existing session, then fetch fresh window and semantic node IDs. Do not run a second `./kotlin run --compose-hot-reload` alongside the first.
- **Stopping / recovery:** The `build/hot-reload-app.pid` file is Java properties: read its `pid=` entry, stop that app, and wait for it to exit before relaunching. If the file is missing or MCP is disconnected, inspect processes scoped to this repository before starting another app. Concurrent instances share discovery/log files and can cause stale window IDs and request timeouts. Never kill unrelated Java processes.

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
