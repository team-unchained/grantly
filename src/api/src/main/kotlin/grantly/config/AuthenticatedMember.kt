package grantly.config

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AuthenticatedMember(
    private val id: Long,
    private val name: String,
    private val email: String,
) : UserDetails {
    override fun getAuthorities() = emptyList<GrantedAuthority>()

    override fun getPassword() = null

    override fun getUsername(): String = name

    fun getId(): Long = id

    fun getEmail(): String = email

    override fun isEnabled(): Boolean = true
}
