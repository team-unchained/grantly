package grantly.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class HttpConflictException : HttpException {
    constructor() : super(HttpErrorType.CONFLICT)
    constructor(message: String?) : super(HttpErrorType.CONFLICT, message)
    constructor(message: String, detail: Map<String, Any>) : super(HttpErrorType.CONFLICT, message, detail)
}
