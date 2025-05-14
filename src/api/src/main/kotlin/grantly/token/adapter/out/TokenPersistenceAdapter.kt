package grantly.token.adapter.out

import grantly.common.annotations.PersistenceAdapter
import grantly.token.application.port.out.TokenRepository
import grantly.token.domain.Token
import jakarta.persistence.EntityNotFoundException

@PersistenceAdapter
class TokenPersistenceAdapter(
    private val tokenJpaRepository: TokenJpaRepository,
    private val mapper: TokenMapper,
) : TokenRepository {
    override fun createToken(token: Token): Token {
        if (token.id != 0L) {
            throw IllegalArgumentException("Token ID must be 0 when creating a new token.")
        }
        val tokenJpaEntity = mapper.toEntity(token)
        val savedToken = tokenJpaRepository.save(tokenJpaEntity)
        return mapper.toDomain(savedToken)
    }

    override fun getToken(value: String): Token {
        val tokenEntity = tokenJpaRepository.findByToken(value)
        if (tokenEntity.isEmpty) {
            throw EntityNotFoundException("Token not found")
        }
        return mapper.toDomain(tokenEntity.get())
    }
}
