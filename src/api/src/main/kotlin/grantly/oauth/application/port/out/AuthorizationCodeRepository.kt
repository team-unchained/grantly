package grantly.oauth.application.port.out

import grantly.oauth.application.port.`in`.dto.AuthorizeClientParams

interface AuthorizationCodeRepository {
    fun save(
        params: AuthorizeClientParams,
        authCode: String,
    )

    fun getAndRemove(
        clientId: String,
        userId: Long,
    ): String?
}
