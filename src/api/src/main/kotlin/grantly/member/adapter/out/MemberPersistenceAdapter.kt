package grantly.member.adapter.out

import grantly.common.annotations.PersistenceAdapter
import grantly.member.application.port.out.MemberRepository
import grantly.member.domain.MemberDomain
import jakarta.persistence.EntityNotFoundException

@PersistenceAdapter
class MemberPersistenceAdapter(
    private val memberJpaRepository: MemberJpaRepository,
    private val memberMapper: MemberMapper,
) : MemberRepository {
    override fun createMember(member: MemberDomain): MemberDomain {
        val memberEntity = memberJpaRepository.save(memberMapper.toEntity(member))
        return memberMapper.toDomain(memberEntity)
    }

    override fun getMember(id: Long): MemberDomain {
        val memberEntity = memberJpaRepository.findById(id)
        if (memberEntity.isEmpty) {
            throw EntityNotFoundException("member not found")
        }
        return memberMapper.toDomain(memberEntity.get())
    }

    override fun getMemberByEmail(email: String): MemberDomain {
        val memberEntity = memberJpaRepository.findByEmail(email)
        if (memberEntity.isEmpty) {
            throw EntityNotFoundException("member not found")
        }
        return memberMapper.toDomain(memberEntity.get())
    }

    override fun getAllMembers(): List<MemberDomain> {
        val members = memberJpaRepository.findAll()
        return members.map { memberMapper.toDomain(it) }
    }

    override fun updateMember(member: MemberDomain): MemberDomain {
        var memberEntity = memberMapper.toEntity(member)
        memberEntity = memberJpaRepository.save(memberEntity)
        return memberMapper.toDomain(memberEntity)
    }
}
