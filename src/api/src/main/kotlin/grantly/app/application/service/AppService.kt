package grantly.app.application.service

import grantly.app.application.port.`in`.CreateAppUseCase
import grantly.app.application.port.`in`.DeleteAppUseCase
import grantly.app.application.port.`in`.FindAppQuery
import grantly.app.application.port.`in`.UpdateAppUseCase
import grantly.app.application.port.`in`.dto.CreateAppParams
import grantly.app.application.port.`in`.dto.UpdateAppParams
import grantly.app.application.port.out.AppRepository
import grantly.app.domain.AppDomain
import grantly.common.annotations.UseCase

@UseCase
class AppService(
    private val appRepository: AppRepository,
) : FindAppQuery,
    UpdateAppUseCase,
    DeleteAppUseCase,
    CreateAppUseCase {
    override fun findAppById(id: Long): AppDomain = appRepository.getAppById(id)

    override fun findAppsByOwnerId(memberId: Long): List<AppDomain> = appRepository.getAppsByOwnerId(memberId)

    override fun updateApp(params: UpdateAppParams): AppDomain {
        TODO("Not yet implemented")
    }

    override fun deleteApp(id: Long): AppDomain {
        TODO("Not yet implemented")
    }

    override fun createApp(params: CreateAppParams): AppDomain {
        TODO("Not yet implemented")
    }
}
