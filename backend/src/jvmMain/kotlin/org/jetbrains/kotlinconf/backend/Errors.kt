package org.jetbrains.kotlinconf.backend

class ServiceUnavailable: Throwable()
class BadRequest: Throwable()
class Unauthorized: Throwable()
class NotFound: Throwable()
class SecretInvalidError: Throwable()
