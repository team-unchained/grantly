package grantly.service.domain

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "서비스 도메인 모델")
class ServiceDomain(
    val id: Long,
    val slug: String,
    var name: String,
    var imageUrl: String? = null,
    var description: String? = null,
    val ownerId: Long,
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    var modifiedAt: OffsetDateTime? = null,
)
