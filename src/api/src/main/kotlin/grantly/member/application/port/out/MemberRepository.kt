package grantly.member.application.port.out

import grantly.member.domain.Member

interface MemberRepository {
    fun createMember(member: Member): Member

    fun getMember(id: Long): Member

    fun getMemberByEmail(email: String): Member

    fun getAllMembers(): List<Member>

    fun updateMember(member: Member): Member
}
