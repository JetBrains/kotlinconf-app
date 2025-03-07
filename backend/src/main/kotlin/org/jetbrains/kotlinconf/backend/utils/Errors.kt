package org.jetbrains.kotlinconf.backend.utils

class ServiceUnavailable: Throwable()
class BadRequest: Throwable()
class Unauthorized: Throwable()
class NotFound: Throwable()
class SecretInvalidError: Throwable()
