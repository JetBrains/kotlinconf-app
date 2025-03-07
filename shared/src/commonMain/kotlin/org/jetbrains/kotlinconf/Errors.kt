package org.jetbrains.kotlinconf

import io.ktor.http.HttpStatusCode

val COMEBACK_LATER_STATUS = HttpStatusCode(477, "Come Back Later")
val TOO_LATE_STATUS = HttpStatusCode(478, "Too Late")

sealed class APIException : RuntimeException()
class Unauthorized : APIException()
class TooEarlyVote : APIException()
class TooLateVote : APIException()
