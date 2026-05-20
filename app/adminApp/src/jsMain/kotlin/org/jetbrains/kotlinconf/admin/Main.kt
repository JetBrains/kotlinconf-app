// ABOUTME: Entry point and top-level state for the Compose HTML admin SPA.
// ABOUTME: Holds the load form, fetched data, and which screen (overview vs. a single user) is shown.

package org.jetbrains.kotlinconf.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        AdminApp()
    }
}

private sealed interface Screen {
    data object Overview : Screen
    data class User(val userId: String) : Screen
}

@Composable
private fun AdminApp() {
    var baseUrl by remember { mutableStateOf(localStorage.getItem(PREF_BASE_URL) ?: window.location.origin) }
    var token by remember { mutableStateOf("") }
    var year by remember { mutableStateOf(localStorage.getItem(PREF_YEAR)?.toIntOrNull() ?: DEFAULT_YEAR) }
    var data by remember { mutableStateOf<AggregatedData?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var screen by remember { mutableStateOf<Screen>(Screen.Overview) }
    val scope = rememberCoroutineScope()

    fun load() {
        localStorage.setItem(PREF_BASE_URL, baseUrl)
        localStorage.setItem(PREF_YEAR, year.toString())
        loading = true
        error = null
        scope.launch {
            try {
                val api = AdminApi(baseUrl.trimEnd('/'), token)
                val conference = api.conference(year)
                val votes = api.votes(year)
                val feedback = api.feedback(year)
                data = aggregate(conference, votes, feedback)
                screen = Screen.Overview
            } catch (e: Throwable) {
                error = e.message ?: "Failed to load data"
                data = null
            } finally {
                loading = false
            }
        }
    }

    H1 { Text("KotlinConf Admin") }

    LoadBar(
        baseUrl = baseUrl, onBaseUrl = { baseUrl = it },
        token = token, onToken = { token = it },
        year = year, onYear = { year = it },
        loading = loading, error = error, onLoad = ::load,
    )

    val loaded = data ?: return
    when (val current = screen) {
        Screen.Overview -> OverviewScreen(
            data = loaded,
            year = year,
            onOpenUser = { screen = Screen.User(it) },
        )

        is Screen.User -> UserScreen(
            data = loaded,
            userId = current.userId,
            onBack = { screen = Screen.Overview },
        )
    }
}

@Composable
private fun LoadBar(
    baseUrl: String, onBaseUrl: (String) -> Unit,
    token: String, onToken: (String) -> Unit,
    year: Int, onYear: (Int) -> Unit,
    loading: Boolean, error: String?,
    onLoad: () -> Unit,
) {
    Div(attrs = { classes("card") }) {
        Div(attrs = { classes("form-grid") }) {
            Div {
                Label(forId = "baseUrl") { Text("Backend base URL") }
                Input(type = InputType.Text) {
                    id("baseUrl")
                    value(baseUrl)
                    onInput { onBaseUrl(it.value) }
                }
            }
            Div {
                Label(forId = "token") { Text("Admin token") }
                Input(type = InputType.Password) {
                    id("token")
                    value(token)
                    onInput { onToken(it.value) }
                }
            }
            Div {
                Label(forId = "year") { Text("Year") }
                Select(attrs = {
                    id("year")
                    onChange { event -> event.value?.toIntOrNull()?.let(onYear) }
                }) {
                    listOf(2026, 2025).forEach { option ->
                        Option(option.toString(), attrs = { if (option == year) selected() }) {
                            Text(option.toString())
                        }
                    }
                }
            }
            Div {
                Button(attrs = {
                    if (loading || token.isBlank()) disabled()
                    onClick { onLoad() }
                }) {
                    Text(if (loading) "Loading…" else "Load data")
                }
            }
        }
        if (error != null) {
            Div(attrs = { classes("status") }) {
                Span(attrs = { classes("status-item", "err") }) { Text(error) }
            }
        }
    }
}

private const val PREF_BASE_URL = "kc-admin-baseUrl"
private const val PREF_YEAR = "kc-admin-year"
private const val DEFAULT_YEAR = 2026
