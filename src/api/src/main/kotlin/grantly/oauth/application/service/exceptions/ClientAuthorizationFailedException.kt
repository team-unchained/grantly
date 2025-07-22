package grantly.oauth.application.service.exceptions

class ClientAuthorizationFailedException : RuntimeException {
    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
