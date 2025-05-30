package grantly.member.application.port.`in`

import grantly.member.domain.Member

interface FindMemberQuery {
    fun findMemberById(id: Long): Member

    fun findMemberByEmail(email: String): Member

    fun findAllMembers(): List<Member>
}
