package grantly.member.adapter.out

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface AuthSessionJpaRepository : JpaRepository<AuthSessionJpaEntity, Long> {
    fun findByToken(token: String): Optional<AuthSessionJpaEntity>

    fun findByMemberId(userId: Long): Optional<AuthSessionJpaEntity>
}
