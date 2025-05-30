package grantly.member.adapter.out

import grantly.common.annotations.PersistenceAdapter
import grantly.member.application.port.out.MemberRepository
import grantly.member.domain.Member
import jakarta.persistence.EntityNotFoundException

@PersistenceAdapter
class MemberPersistenceAdapter(
    private val memberJpaRepository: MemberJpaRepository,
    private val memberMapper: MemberMapper,
) : MemberRepository {
    override fun createMember(member: Member): Member {
        val memberEntity = memberJpaRepository.save(memberMapper.toEntity(member))
        return memberMapper.toDomain(memberEntity)
    }

    override fun getMember(id: Long): Member {
        val memberEntity = memberJpaRepository.findById(id)
        if (memberEntity.isEmpty) {
            throw EntityNotFoundException("member not found")
        }
        return memberMapper.toDomain(memberEntity.get())
    }

    override fun getMemberByEmail(email: String): Member {
        val memberEntity = memberJpaRepository.findByEmail(email)
        if (memberEntity.isEmpty) {
            throw EntityNotFoundException("member not found")
        }
        return memberMapper.toDomain(memberEntity.get())
    }

    override fun getAllMembers(): List<Member> {
        val members = memberJpaRepository.findAll()
        return members.map { memberMapper.toDomain(it) }
    }

    override fun updateMember(member: Member): Member {
        var memberEntity = memberMapper.toEntity(member)
        memberEntity = memberJpaRepository.save(memberEntity)
        return memberMapper.toDomain(memberEntity)
    }
}
