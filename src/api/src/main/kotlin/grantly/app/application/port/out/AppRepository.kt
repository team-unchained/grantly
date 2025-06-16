package grantly.app.application.port.out

import grantly.app.domain.AppDomain

interface AppRepository {
    fun getAppById(id: Long): AppDomain

    fun getAppsByOwnerId(memberId: Long): List<AppDomain>

    fun createApp(appDomain: AppDomain): AppDomain

    fun updateApp(appDomain: AppDomain): AppDomain
}
