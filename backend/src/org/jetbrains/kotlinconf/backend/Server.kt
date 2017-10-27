package org.jetbrains.kotlinconf.backend

import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, commandLineEnvironment(args))
    server.start()
}
