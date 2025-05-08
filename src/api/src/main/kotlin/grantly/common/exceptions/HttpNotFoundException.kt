package grantly.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class HttpNotFoundException : HttpException {
    constructor() : super(HttpErrorType.INSTANCE_NOT_FOUND)
    constructor(message: String?) : super(HttpErrorType.INSTANCE_NOT_FOUND, message)
    constructor(message: String, detail: Map<String, Any>) : super(HttpErrorType.INSTANCE_NOT_FOUND, message, detail)
}
