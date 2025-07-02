package grantly.session.adapter.out

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface AuthSessionJpaRepository : JpaRepository<AuthSessionJpaEntity, Long> {
    fun findByToken(token: String): Optional<AuthSessionJpaEntity>

    fun findBySubjectIdAndSubjectType(
        subjectId: Long,
        subjectType: Int,
    ): Optional<AuthSessionJpaEntity>
}
