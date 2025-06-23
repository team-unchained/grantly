package grantly.app.application.port.`in`

import grantly.app.domain.AppDomain

interface DeleteAppUseCase {
    fun deleteApp(
        slug: String,
        ownerId: Long,
    ): AppDomain
}
