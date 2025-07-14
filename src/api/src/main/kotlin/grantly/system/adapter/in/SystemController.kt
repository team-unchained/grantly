package grantly.system.adapter.`in`

import io.sentry.Sentry
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@ResponseBody
@RequestMapping("/admin/v1/system")
@Tag(name = "시스템", description = "시스템 관련 API")
class SystemController {
    @Operation(
        summary = "서버 상태 확인",
        responses = [
            ApiResponse(responseCode = "200", description = "서버 상태 확인 성공"),
        ],
    )
    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> = ResponseEntity.ok(mapOf("status" to "all is well"))

    @Operation(
        summary = "Sentry 테스트",
        responses = [
            ApiResponse(responseCode = "200", description = "Sentry 테스트 성공"),
        ],
    )
    @GetMapping("/sentry")
    fun sentry(): ResponseEntity<Map<String, String>> {
        try {
            throw Exception("This is a sentry test.")
        } catch (e: Exception) {
            Sentry.captureException(e)
        }

        return ResponseEntity.ok(mapOf("status" to "sentry test success"))
    }
}
