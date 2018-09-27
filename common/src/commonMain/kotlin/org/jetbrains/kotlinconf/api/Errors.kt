package org.jetbrains.kotlinconf.api

class UpdateProblem : Throwable()
class Unauthorized : Throwable()
class CannotPostVote : Throwable()
class CannotDeleteVote : Throwable()
class CannotFavorite : Throwable()
class TooEarlyVote : Throwable()
class TooLateVote : Throwable()
