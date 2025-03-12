package grantly.common.exceptions

open class HttpExceptionResponse(
    val code: Int,
    val message: String,
    val detail: Map<String, Any>? = null,
)
