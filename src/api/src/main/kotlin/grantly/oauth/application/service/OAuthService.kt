package grantly.oauth.application.service

import grantly.app.application.port.out.AppClientRepository
import grantly.oauth.adapter.out.OAuthConsentJpaRepository
import grantly.oauth.application.port.`in`.AuthorizationCodeUseCase
import grantly.oauth.application.port.`in`.dto.AuthorizeClientParams
import grantly.oauth.application.port.out.AuthorizationCodeRepository
import grantly.oauth.application.port.out.OAuthConsentRepository
import grantly.oauth.application.service.exceptions.ClientAuthorizationFailedException
import grantly.oauth.domain.OAuthConsentDomain
import grantly.oauth.domain.enums.OAuthClientScope
import grantly.oauth.domain.enums.OAuthGrantType
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.OffsetDateTime
import java.util.Base64


private val log = mu.KotlinLogging.logger {}

@Service
class OAuthService(
    private val appClientRepository: AppClientRepository,
    private val authorizationCodeRepository: AuthorizationCodeRepository,
    private val oAuthConsentJpaRepository: OAuthConsentRepository,
    private val clientValidator: OAuthClientValidator,
) : AuthorizationCodeUseCase {
    companion object {
        private const val AUTH_CODE_LENGTH = 32 // Authorization Code 길이
    }

    @Transactional
    override fun authorizeClient(params: AuthorizeClientParams): String {
        // 1. 클라이언트 조회
        val appClient =
            try {
                appClientRepository.findByClientId(params.clientId)
            } catch (e: EntityNotFoundException) {
                throw ClientAuthorizationFailedException("Client not found: ${params.clientId}", e)
            }
        // 2. 유효성 검사
        val validationResult = clientValidator.validateAuthorizationRequest(appClient, params)
        if (validationResult is ValidationResult.Failure) {
            throw ClientAuthorizationFailedException(
                "Invalid authorization request: ${
                    validationResult.errors.joinToString(
                        ", ",
                    )
                }",
            )
        }
        // 3. 동의 기록이 있는지 확인하고 없으면 생성
        try {
            oAuthConsentJpaRepository.findByAppClientIdAndUserId(appClient.id, params.userId)
        } catch (e: EntityNotFoundException) {
            oAuthConsentJpaRepository.createConsent(
                OAuthConsentDomain(
                    appClientId = appClient.id,
                    userId = params.userId,
                    grantedScopes = params.scopes.mapNotNull { OAuthClientScope.fromValue(it) }.toMutableList(),
                    consentedAt = OffsetDateTime.now(),
                ),
            )
        }
        // 4. Authorization Code 생성 및 저장
        val authCode = generateAuthCode(params)
        authorizationCodeRepository.save(params, authCode)
        // 5. 최종 redirect URI 생성 및 반환
        val redirectUri = "${params.redirectUri}?code=$authCode&state=${params.state}"
        log.info { "Authorization successful for client ${params.clientId}, redirecting to $redirectUri" }
        return redirectUri
    }

    override fun generateAuthCode(params: AuthorizeClientParams): String {
        val random = SecureRandom()
        val bytes = ByteArray(AUTH_CODE_LENGTH)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
