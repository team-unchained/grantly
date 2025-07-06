package grantly.app.application.port.`in`

import grantly.app.application.port.`in`.dto.DeleteAppClientParams

interface DeleteAppClientUseCase {
    fun deleteAppClient(params: DeleteAppClientParams)
}
