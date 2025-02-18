package grantly

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class User(
    @Id
    val id: Long? = null,
    val email: String,
    val password: String,
    val name: String,
)
