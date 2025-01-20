package org.jetbrains.kotlinconf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.jetbrains.kotlinconf.navigation.KotlinConfNavHost
import org.jetbrains.kotlinconf.ui.theme.KotlinConfTheme
import kotlin.random.Random

const val apiEndpoint = "https://kotlinconf-app-prod.labs.jb.gg"

// TODO replace with real data store
private object FakeDataStore {
    fun isOnboardingComplete(): Flow<Boolean> = flow {
        delay(200L)
        emit(Random.nextBoolean())
    }
}

@Composable
fun App(context: ApplicationContext) {
    DevelopmentEntryPoint {
        KotlinConfTheme {
            val service = remember { ConferenceService(context, apiEndpoint) }

            val isOnboardingComplete = remember { FakeDataStore.isOnboardingComplete() }
                .collectAsState(initial = null)
                .value

            Box(
                Modifier
                    .fillMaxSize()
                    .background(KotlinConfTheme.colors.mainBackground)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                if (isOnboardingComplete != null) {
                    KotlinConfNavHost(service, isOnboardingComplete)
                }
            }
        }
    }
}
