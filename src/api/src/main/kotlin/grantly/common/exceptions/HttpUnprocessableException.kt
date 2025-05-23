package grantly.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
class HttpUnprocessableException : HttpException {
    constructor() : super(HttpErrorType.UNPROCESSABLE_ENTITY)
    constructor(message: String?) : super(HttpErrorType.UNPROCESSABLE_ENTITY, message)
    constructor(message: String, detail: Map<String, Any>) : super(HttpErrorType.UNPROCESSABLE_ENTITY, message, detail)
}
