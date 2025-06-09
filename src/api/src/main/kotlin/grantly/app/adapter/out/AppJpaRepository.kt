package grantly.app.adapter.out

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface AppJpaRepository : JpaRepository<AppJpaEntity, Long> {
    fun findByOwnerId(ownerId: Long): Optional<List<AppJpaEntity>>
}
