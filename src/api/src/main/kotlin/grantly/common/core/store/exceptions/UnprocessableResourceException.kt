package grantly.common.core.store.exceptions

class UnprocessableResourceException : StoreOperationFailedException {
    constructor() : super("Resource cannot be processed")
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
