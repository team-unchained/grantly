package grantly.common.core.store.exceptions

class ResourceTooLargeException : StoreOperationFailedException {
    constructor() : super("Resource size exceeds the allowed limit")
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
