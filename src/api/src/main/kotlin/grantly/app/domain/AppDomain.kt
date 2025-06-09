package grantly.app.domain

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "앱 도메인 모델")
data class AppDomain(
    val id: Long,
    val slug: String,
    var name: String,
    var imageUrl: String? = null,
    var description: String? = null,
    val ownerId: Long,
    var isActive: Boolean = true,
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    var modifiedAt: OffsetDateTime? = null,
) {
    fun deactivate() {
        isActive = false
    }
}
