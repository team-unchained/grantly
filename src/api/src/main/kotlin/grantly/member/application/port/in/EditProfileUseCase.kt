package grantly.member.application.port.`in`

import grantly.member.domain.MemberDomain

interface EditProfileUseCase {
    fun update(
        id: Long,
        name: String,
    ): MemberDomain
}
