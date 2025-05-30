package grantly.config

import org.springframework.security.test.context.support.WithSecurityContext

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION) // 클래스와 함수에 적용
@Retention(AnnotationRetention.RUNTIME) // 런타임까지 유지
@WithSecurityContext(factory = WithSessionSecurityContextFactory::class) // 커스텀 SecurityContextFactory 설정
annotation class WithTestSessionMember(
    val email: String = "test@email.com", // 기본 이메일
    val name: String = "testUser", // 기본 사용자 이름
)
