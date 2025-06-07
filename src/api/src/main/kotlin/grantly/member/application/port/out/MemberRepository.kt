package grantly.member.application.port.out

import grantly.member.domain.MemberDomain

interface MemberRepository {
    fun createMember(member: MemberDomain): MemberDomain

    fun getMember(id: Long): MemberDomain

    fun getMemberByEmail(email: String): MemberDomain

    fun getAllMembers(): List<MemberDomain>

    fun updateMember(member: MemberDomain): MemberDomain
}
