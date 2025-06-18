package grantly.common.core.store.exceptions

/**
 * store operation 이 실패했을 때 발생하는 기본 예외 클래스
 */
class StoreOperationFailedException : RuntimeException {
    constructor() : super("Failed to perform store operation")
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
