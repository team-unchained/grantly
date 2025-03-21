package grantly.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class HttpInternalServerErrorException : HttpException {
    constructor() : super(HttpErrorType.INTERNAL_SERVER_ERROR)
    constructor(message: String?) : super(HttpErrorType.INTERNAL_SERVER_ERROR, message)
    constructor(message: String, detail: Map<String, Any>) : super(HttpErrorType.INTERNAL_SERVER_ERROR, message, detail)
}
