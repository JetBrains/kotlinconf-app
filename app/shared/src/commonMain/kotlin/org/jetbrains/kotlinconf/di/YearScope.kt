package org.jetbrains.kotlinconf.di

import org.jetbrains.kotlinconf.network.YearlyApi
import org.jetbrains.kotlinconf.storage.YearlyStorage
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.annotation.Module
import org.koin.core.annotation.Scoped
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatform
import org.koin.plugin.module.dsl.typeQualifier

@Module
@Configuration
class YearScopeModule {

    @OptIn(KoinInternalApi::class)
    @org.koin.core.annotation.Scope(YearScope::class)
    @Scoped
    @Year
    fun providesYear(scope : Scope) : Int =  (scope.sourceValue as? YearScope)?.year ?: error("YearScope not found in scope hierarchy")
}

class YearScope(
    val year: Int,
) : KoinScopeComponent {
    override val scope: Scope = KoinPlatform.getKoin().getOrCreateScope("$year", typeQualifier(YearScope::class),this)
    val storage: YearlyStorage by scope.inject()
    val api: YearlyApi by scope.inject()
    // Any need to release scope?
    fun close(){
        scope.close()
    }
}
