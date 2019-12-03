package org.jetbrains.kotlinconf

import io.ktor.http.*

val COMEBACK_LATER_STATUS = HttpStatusCode(477, "Come Back Later")
val TOO_LATE_STATUS = HttpStatusCode(478, "Too Late")

class UpdateProblem(override val cause: Throwable) : Throwable()
class Unauthorized : Throwable()
class CannotPostVote : Throwable()
class CannotDeleteVote : Throwable()
class CannotFavorite : Throwable()
class TooEarlyVote : Throwable()
class TooLateVote : Throwable()
