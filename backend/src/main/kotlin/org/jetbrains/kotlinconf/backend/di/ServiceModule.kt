package org.jetbrains.kotlinconf.backend.di

import org.jetbrains.kotlinconf.backend.services.ArchivedDataService
import org.jetbrains.kotlinconf.backend.services.AssetService
import org.jetbrains.kotlinconf.backend.services.ConferenceInfoService
import org.jetbrains.kotlinconf.backend.services.SessionizeService
import org.jetbrains.kotlinconf.backend.services.TimeService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::TimeService)
    singleOf(::SessionizeService)
    singleOf(::ArchivedDataService)
    singleOf(::ConferenceInfoService)
    singleOf(::AssetService)
}
