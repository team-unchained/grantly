package grantly.token.application.port.out

import grantly.token.domain.TokenDomain

interface TokenRepository {
    fun create(token: TokenDomain): TokenDomain

    fun get(value: String): TokenDomain

    fun deactivate(token: TokenDomain): TokenDomain
}
