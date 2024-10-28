package com.moim.core.common.exception

open class NetworkException(message: String?, throwable: Throwable?) : RuntimeException(message, throwable)

// HTTP STATUS CODE 400
class BadRequestException(message: String?, throwable: Throwable?) : NetworkException(message, throwable)

// HTTP STATUS CODE 401
class UnAuthorizedException(message: String?, throwable: Throwable?) : NetworkException(message, throwable)

// HTTP STATUS CODE 403
class ForbiddenException(message: String?, throwable: Throwable?) : NetworkException(message, throwable)

// HTTP STATUS CODE 404
class NotFoundException(message: String?, throwable: Throwable?) : NetworkException(message, throwable)

// HTTP STATUS CODE 409
class ConflictException(message: String?, throwable: Throwable?) : NetworkException(message, throwable)

// HTTP STATUS CODE 500
class ServerErrorException(message: String?, throwable: Throwable?) : NetworkException(message, throwable)

// Unknown
class UnknownErrorException(message: String?, throwable: Throwable?) : NetworkException(message, throwable)