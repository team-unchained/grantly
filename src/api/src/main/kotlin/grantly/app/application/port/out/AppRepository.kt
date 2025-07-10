package grantly.app.application.port.out

import grantly.app.domain.AppDomain

interface AppRepository {
    fun getAppById(id: Long): AppDomain

    fun getAppsByOwnerId(ownerId: Long): List<AppDomain>

    fun createApp(appDomain: AppDomain): AppDomain

    fun updateApp(appDomain: AppDomain): AppDomain

    fun getActiveAppCountByOwnerId(ownerId: Long): Long

    fun getAppBySlug(slug: String): AppDomain
}
