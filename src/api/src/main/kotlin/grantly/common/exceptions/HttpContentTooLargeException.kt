package grantly.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
class HttpContentTooLargeException : HttpException {
    constructor() : super(HttpErrorType.CONTENT_TOO_LARGE)
    constructor(message: String?) : super(HttpErrorType.CONTENT_TOO_LARGE, message)
    constructor(message: String, detail: Map<String, Any>) : super(HttpErrorType.CONTENT_TOO_LARGE, message, detail)
}
