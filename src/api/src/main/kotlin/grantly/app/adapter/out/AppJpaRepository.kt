package grantly.app.adapter.out

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface AppJpaRepository : JpaRepository<AppJpaEntity, Long> {
    fun findByIdAndIsActiveIsTrue(id: Long): Optional<AppJpaEntity>

    fun findByOwnerIdAndIsActiveIsTrueOrderByIdDesc(ownerId: Long): Optional<List<AppJpaEntity>>

    fun countByIsActiveIsTrueAndOwnerId(ownerId: Long): Long
}
