package grantly.member.application.port.`in`

import grantly.member.application.port.`in`.dto.SignUpParams
import grantly.member.domain.MemberDomain

interface SignUpUseCase {
    fun signUp(params: SignUpParams): MemberDomain
}
