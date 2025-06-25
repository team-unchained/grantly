package grantly.common.core.store.exceptions

class NoSuchResourceException : StoreOperationFailedException {
    constructor() : super("Store resource not found")
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
