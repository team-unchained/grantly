package grantly.common.exceptions

/**
 * 모든 HTTP exception 의 부모 클래스
 * @param errorType HTTP 에러 타입
 * @param message 에러 메시지
 * @param detail 에러 상세 정보
 */
open class HttpException(
    val errorType: HttpErrorType,
    override val message: String? = null,
    val detail: Map<String, Any>? = null,
) : RuntimeException(message)
