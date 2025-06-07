package grantly.member.application.port.`in`

import grantly.member.domain.MemberDomain

interface FindMemberQuery {
    fun findMemberById(id: Long): MemberDomain

    fun findMemberByEmail(email: String): MemberDomain

    fun findAllMembers(): List<MemberDomain>
}
