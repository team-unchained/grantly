package grantly.app.adapter.`in`

import grantly.app.application.port.`in`.FindAppQuery
import grantly.app.domain.AppDomain
import grantly.common.exceptions.HttpForbiddenException
import grantly.common.exceptions.PermissionDeniedException
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Component

@Component
class AppGuard(
    private val findAppQuery: FindAppQuery,
) {
    /**
     * 앱에 대한 접근 권한을 확인하고 앱 객체를 반환합니다.
     */
    fun checkAccess(
        appSlug: String,
        memberId: Long,
    ): AppDomain =
        try {
            findAppQuery.findAppBySlugAndOwnerId(appSlug, memberId)
        } catch (_: EntityNotFoundException) {
            throw HttpForbiddenException("App not found.")
        } catch (_: PermissionDeniedException) {
            throw HttpForbiddenException()
        }
}
