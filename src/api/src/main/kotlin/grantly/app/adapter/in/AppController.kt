package grantly.app.adapter.`in`

import grantly.app.application.port.`in`.CreateAppUseCase
import grantly.app.application.port.`in`.DeleteAppUseCase
import grantly.app.application.port.`in`.FindAppQuery
import grantly.app.application.port.`in`.UpdateAppUseCase
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@ResponseBody
@RequestMapping("/v1/apps")
@Tag(name = "앱", description = "클라이언트가 만든 앱")
class AppController(
    private val findAppQuery: FindAppQuery,
    private val updateAppUseCase: UpdateAppUseCase,
    private val deleteAppUseCase: DeleteAppUseCase,
    private val createAppUseCase: CreateAppUseCase,
) {
    @GetMapping("")
    fun getApps(): ResponseEntity<Void> {
        TODO("get all apps by request user")
    }

    @PostMapping("")
    fun createApp(): ResponseEntity<Void> {
        TODO("create app by request user")
    }

    @DeleteMapping("/{appId}")
    fun deleteApp(
        @PathVariable appId: Long,
    ): ResponseEntity<Void> {
        TODO("soft delete app by id")
    }

    @GetMapping("/{appId}")
    fun getApp(
        @PathVariable appId: Long,
    ): ResponseEntity<Void> {
        TODO("get app by id")
    }

    @PutMapping("/{appId}")
    fun updateApp(
        @PathVariable appId: Long,
    ): ResponseEntity<Void> {
        TODO("update app by id")
    }
}
