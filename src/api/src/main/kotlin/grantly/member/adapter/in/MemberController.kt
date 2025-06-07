package grantly.member.adapter.`in`

import grantly.common.exceptions.HttpNotFoundException
import grantly.config.AuthenticatedMember
import grantly.member.adapter.`in`.dto.SignUpRequest
import grantly.member.adapter.`in`.dto.UpdateMemberRequest
import grantly.member.adapter.out.dto.MemberResponse
import grantly.member.application.port.`in`.EditProfileUseCase
import grantly.member.application.port.`in`.FindMemberQuery
import grantly.member.application.port.`in`.SignUpUseCase
import grantly.member.application.port.`in`.dto.SignUpParams
import grantly.member.domain.MemberDomain
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/v1/members")
@Tag(name = "서비스 멤버 API", description = "멤버 관련 API")
class MemberController(
    private val signUpUseCase: SignUpUseCase,
    private val findMemberQuery: FindMemberQuery,
    private val editProfileUseCase: EditProfileUseCase,
) {
    @GetMapping("/me")
    fun getCurrentMember(): ResponseEntity<MemberResponse> {
        val requestMember = SecurityContextHolder.getContext().authentication.principal as AuthenticatedMember
        val member: MemberDomain
        try {
            member = findMemberQuery.findMemberById(requestMember.getId())
        } catch (e: EntityNotFoundException) {
            throw HttpNotFoundException("Member not found")
        }
        return ResponseEntity.ok(
            MemberResponse(
                id = member.id,
                email = member.email,
                name = member.name,
            ),
        )
    }

    @GetMapping
    fun findAll(): List<MemberDomain> {
        log.info { "멤버 전체 목록을 조회합니다" }
        val members = findMemberQuery.findAllMembers()
        log.debug { "조회된 멤버 수: ${members.size}" }
        return members
    }

    @GetMapping("/{memberId}")
    fun findById(
        @PathVariable memberId: Long,
    ): MemberDomain = findMemberQuery.findMemberById(memberId)

    @PostMapping
    fun signUp(
        @RequestBody body: SignUpRequest,
    ): MemberDomain = signUpUseCase.signUp(SignUpParams(body.email, body.name, body.password))

    @PatchMapping("/{memberId}")
    fun update(
        @PathVariable memberId: Long,
        @RequestBody body: UpdateMemberRequest,
    ): MemberDomain = editProfileUseCase.update(memberId, body.name)
}
