package grantly.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class HttpBadRequestException : HttpException {
    constructor() : super(HttpErrorType.BAD_REQUEST)
    constructor(message: String?) : super(HttpErrorType.BAD_REQUEST, message)
    constructor(message: String, detail: Map<String, Any>) : super(HttpErrorType.BAD_REQUEST, message, detail)
}
