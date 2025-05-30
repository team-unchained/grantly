package grantly.member.application.port.`in`

import grantly.member.domain.Member

interface EditProfileUseCase {
    fun update(
        id: Long,
        name: String,
    ): Member
}
