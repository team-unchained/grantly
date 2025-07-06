package grantly.app.adapter.out

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AppClientJpaRepository : JpaRepository<AppClientJpaEntity, Long> {
    fun findByAppId(appId: Long): List<AppClientJpaEntity>

    fun findByAppIdAndClientId(
        appId: Long,
        clientId: String,
    ): Optional<AppClientJpaEntity>

    fun existsByClientId(clientId: String): Boolean

    fun existsByAppIdAndTitle(
        appId: Long,
        title: String,
    ): Boolean
}
