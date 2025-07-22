package grantly.oauth.application.service

import grantly.app.domain.AppClientDomain
import grantly.oauth.application.port.`in`.dto.AuthorizeClientParams
import grantly.oauth.domain.enums.OAuthGrantType
import grantly.oauth.domain.enums.OAuthClientScope
import org.springframework.stereotype.Component

@Component
class OAuthClientValidator {

    fun validateAuthorizationRequest(
        client: AppClientDomain,
        params: AuthorizeClientParams
    ): ValidationResult {
        val errors = mutableListOf<String>()

        // Grant Type 검증
        if (client.grantType != OAuthGrantType.AUTHORIZATION_CODE) {
            errors.add("Invalid grant type: ${client.grantType}")
        }

        // Redirect URI 검증
        if (!client.redirectUris.contains(params.redirectUri)) {
            errors.add("Invalid redirect URI: ${params.redirectUri}")
        }

        // Scope 검증
        val scopeValidation = validateScopes(client.scopes, params.scopes)
        if (!scopeValidation.isValid) {
            errors.add("Invalid scopes: ${scopeValidation.invalidScopes}")
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }

    private fun validateScopes(
        clientScopes: List<OAuthClientScope>,
        requestScopes: List<String>
    ): ScopeValidationResult {
        if (requestScopes.isEmpty()) return ScopeValidationResult(true, emptyList())

        val invalidScopes = mutableListOf<String>()

        requestScopes.forEach { scopeString ->
            val scope = OAuthClientScope.fromValue(scopeString)
            if (scope == null) {
                // 존재하지 않는 scope
                invalidScopes.add(scopeString)
            } else if (!clientScopes.contains(scope)) {
                // 클라이언트가 허용하지 않은 scope
                invalidScopes.add(scopeString)
            }
        }

        return ScopeValidationResult(
            isValid = invalidScopes.isEmpty(),
            invalidScopes = invalidScopes
        )
    }
}

data class ScopeValidationResult(
    val isValid: Boolean,
    val invalidScopes: List<String>
)

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(val errors: List<String>) : ValidationResult()
}
