package org.jetbrains.kotlinconf.backend.di

import org.jetbrains.kotlinconf.backend.services.NewsService
import org.jetbrains.kotlinconf.backend.services.SessionizeService
import org.jetbrains.kotlinconf.backend.services.TimeService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::TimeService)
    singleOf(::NewsService)
    singleOf(::SessionizeService)
}