package grantly.app.application.port.`in`

import grantly.app.domain.AppDomain

interface FindAppQuery {
    fun findAppById(id: Long): AppDomain

    fun findAppsByOwnerId(memberId: Long): List<AppDomain>
}
