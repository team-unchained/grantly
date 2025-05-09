package grantly.token.application.port.out

import grantly.token.domain.Token

interface TokenRepository {
    fun createToken(token: Token): Token

    fun getToken(value: String): Token

    fun getTokenById(id: Long): Token

    fun deactivate(id: Long): Token
}
