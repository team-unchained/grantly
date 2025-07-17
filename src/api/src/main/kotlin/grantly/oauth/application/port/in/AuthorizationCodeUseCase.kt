package grantly.oauth.application.port.`in`

import grantly.oauth.application.port.`in`.dto.AuthorizeClientParams

interface AuthorizationCodeUseCase {
    fun authorizeClient(params: AuthorizeClientParams): String // TODO: 손봐야함

    fun generateAuthCode(params: AuthorizeClientParams): String
}
