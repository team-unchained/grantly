package grantly.config

import grantly.session.domain.SubjectType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthenticationEntity(
    private val id: Long,
    private val name: String,
    private val email: String,
    private val role: SubjectType,
) : UserDetails {
    override fun getAuthorities() =
        listOf<GrantedAuthority>(
            SimpleGrantedAuthority("ROLE_${role.name}"),
        )

    override fun getPassword() = null

    override fun getUsername(): String = name

    fun getId(): Long = id

    fun getEmail(): String = email

    override fun isEnabled(): Boolean = true
}
