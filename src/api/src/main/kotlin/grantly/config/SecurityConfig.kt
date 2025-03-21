package grantly.config

import grantly.user.application.port.out.AuthSessionRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.security.SecureRandom

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authSessionRepository: AuthSessionRepository,
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(12, SecureRandom())

    @Bean
    fun securityFilterChainDSL(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // 기본 csrf 설정 비활성화
            .authorizeHttpRequests { auth ->
                auth
                    .anyRequest()
                    .permitAll()
            }.addFilterBefore(CustomAuthFilter(authSessionRepository), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
