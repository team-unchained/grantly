package grantly.token.adapter.out

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface TokenJpaRepository : JpaRepository<TokenJpaEntity, Long> {
    fun findByToken(token: String): Optional<TokenJpaEntity>
}
