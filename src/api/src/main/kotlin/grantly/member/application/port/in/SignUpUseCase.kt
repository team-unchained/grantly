package grantly.member.application.port.`in`

import grantly.member.application.port.`in`.dto.SignUpParams
import grantly.member.domain.Member

interface SignUpUseCase {
    fun signUp(params: SignUpParams): Member
}
