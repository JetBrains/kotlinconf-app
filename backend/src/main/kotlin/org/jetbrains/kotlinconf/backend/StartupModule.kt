package org.jetbrains.kotlinconf.backend

import io.ktor.server.application.Application
import org.jetbrains.kotlinconf.backend.services.ArchivedDataService
import org.jetbrains.kotlinconf.backend.services.DocumentsService
import org.koin.ktor.ext.get as koinGet

fun Application.startupModule() {
    koinGet<ArchivedDataService>().validateArchives()
    koinGet<DocumentsService>().validateDocuments()
}
