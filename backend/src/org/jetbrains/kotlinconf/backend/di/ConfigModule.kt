package org.jetbrains.kotlinconf.backend.di

import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val configModule = module {
    singleOf(::ConferenceConfig)
}