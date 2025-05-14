package grantly.user.application.port.`in`

/**
 * 비밀번호 초기화 흐름을 정의하는 UseCase 인터페이스
 */
interface PasswordResetUseCase {
    fun requestPasswordReset(email: String): Boolean
}
