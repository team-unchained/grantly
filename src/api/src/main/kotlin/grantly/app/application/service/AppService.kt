package grantly.app.application.service

import grantly.app.application.port.`in`.CreateAppUseCase
import grantly.app.application.port.`in`.DeleteAppUseCase
import grantly.app.application.port.`in`.FindAppQuery
import grantly.app.application.port.`in`.UpdateAppUseCase
import grantly.app.application.port.`in`.dto.CreateAppParams
import grantly.app.application.port.`in`.dto.UpdateAppParams
import grantly.app.application.port.out.AppRepository
import grantly.app.application.service.exceptions.CannotDeleteLastActiveAppException
import grantly.app.domain.AppDomain
import grantly.common.annotations.UseCase
import jakarta.validation.ConstraintViolationException
import me.atrox.haikunator.Haikunator

private val log = mu.KotlinLogging.logger {}

@UseCase
class AppService(
    private val appRepository: AppRepository,
) : FindAppQuery,
    UpdateAppUseCase,
    DeleteAppUseCase,
    CreateAppUseCase {
    override fun findAppById(
        id: Long,
        ownerId: Long,
    ): AppDomain {
        val app = appRepository.getAppById(id)
        app.checkOwner(ownerId)
        return app
    }

    override fun findAppsByOwnerId(memberId: Long): List<AppDomain> = appRepository.getAppsByOwnerId(memberId)

    override fun updateApp(params: UpdateAppParams): AppDomain {
        val app = appRepository.getAppById(params.id)
        app.checkOwner(params.ownerId)
        app.name = params.name
        app.description = params.description
        app.imageUrl = params.imageUrl
        return appRepository.updateApp(app)
    }

    override fun deleteApp(
        id: Long,
        ownerId: Long,
    ): AppDomain {
        val app = appRepository.getAppById(id)
        app.checkOwner(ownerId)
        val appCount = appRepository.getActiveAppCountByOwnerId(ownerId)
        if (appCount <= 1) {
            throw CannotDeleteLastActiveAppException()
        }
        app.deactivate()
        return appRepository.updateApp(app)
    }

    override fun createApp(params: CreateAppParams): AppDomain {
        var retries = 5
        while (retries > 0) {
            val app =
                AppDomain(
                    name = params.name,
                    description = params.description,
                    slug = generateAppSlug(),
                    ownerId = params.ownerId,
                    imageUrl = params.imageUrl,
                )

            try {
                return appRepository.createApp(app)
            } catch (e: ConstraintViolationException) {
                retries--
            } catch (e: Exception) {
                break
            }
        }
        log.error { "Failed to create unique slug for app, aborting..." }
        throw IllegalStateException("Failed to create app")
    }

    private fun generateAppSlug(): String {
        val haikunator = Haikunator().setTokenHex(true)
        return haikunator.haikunate()
    }
}
