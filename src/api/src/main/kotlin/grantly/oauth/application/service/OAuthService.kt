package grantly.oauth.application.service

import grantly.app.application.port.out.AppClientRepository
import grantly.oauth.adapter.out.enums.OAuthGrantType
import grantly.oauth.application.port.`in`.AuthorizationCodeUseCase
import grantly.oauth.application.port.`in`.dto.AuthorizeClientParams
import grantly.oauth.application.port.out.AuthorizationCodeRepository
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.Base64

@Service
class OAuthService(
    private val appClientRepository: AppClientRepository,
    private val authorizationCodeRepository: AuthorizationCodeRepository,
) : AuthorizationCodeUseCase {
    companion object {
        private const val AUTH_CODE_LENGTH = 32 // Authorization Code 길이
    }

    override fun authorizeClient(params: AuthorizeClientParams): String {
        // 1. 클라이언트 조회
        val appClient = appClientRepository.findByClientId(params.clientId) // TODO: 예외 핸들링
        // 2. 유효성 검사
        appClient.validate(params)
        // 3. Authorization Code 생성 및 저장
        val authCode = generateAuthCode(params) // TODO: 실제 코드 생성 로직 구현
        authorizationCodeRepository.save(params, authCode)
        // 4. 최종 redirect URI 생성 및 반환
        val redirectUri = "${params.redirectUri}?code=$authCode&state=${params.state}"
        return redirectUri
    }

    override fun generateAuthCode(params: AuthorizeClientParams): String {
        val random = SecureRandom()
        val bytes = ByteArray(AUTH_CODE_LENGTH)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
