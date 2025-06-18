package grantly.common.core.store.exceptions

import java.io.IOException

class NoSuchResourceException : IOException {
    constructor() : super("Store resource not found")
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
