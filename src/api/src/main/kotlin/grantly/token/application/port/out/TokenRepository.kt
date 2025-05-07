package grantly.token.application.port.out

import grantly.token.domain.Token

interface TokenRepository {
    fun createToken(token: Token): Token

    fun getToken(value: String): Token
}
