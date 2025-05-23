package grantly.token.application.port.out

import grantly.token.domain.Token

interface TokenRepository {
    fun create(token: Token): Token

    fun get(value: String): Token

    fun deactivate(token: Token): Token
}
