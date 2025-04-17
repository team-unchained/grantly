package grantly.config

import grantly.config.filter.CsrfValidationFilter
import grantly.config.filter.SessionContext
import grantly.config.filter.SessionValidationFilter
import grantly.user.application.port.out.UserRepository
import grantly.user.application.service.SessionService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.security.SecureRandom

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val redisTemplate: StringRedisTemplate,
    private val sessionContext: SessionContext,
    private val sessionService: SessionService,
    private val userRepository: UserRepository,
) {
    companion object {
        private const val ORDER_PUBLIC_FILTER = 1
        private const val ORDER_SECURE_FILTER = 2
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(12, SecureRandom())

    @Bean
    fun csrfTokenRepository(): CsrfTokenRepository = RedisCsrfTokenRepository(redisTemplate)

    @Bean
    @Order(ORDER_PUBLIC_FILTER)
    fun publicFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher(
                "/v*/auth/login",
                "/v*/auth/signup",
                "/v*/auth/csrf-token",
                "/docs/**",
            ).authorizeHttpRequests { it.anyRequest().permitAll() }
            .addFilterBefore(sessionContext, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAfter(
                CsrfValidationFilter(csrfTokenRepository()),
                UsernamePasswordAuthenticationFilter::class.java,
            ).csrf { it.disable() }
        return http.build()
    }

    @Bean
    @Order(ORDER_SECURE_FILTER)
    fun secureFilterChain(http: HttpSecurity): SecurityFilterChain {
        // SessionContext -> CsrfValidationFilter -> SessionValidationFilter
        http
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .addFilterBefore(sessionContext, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAfter(
                CsrfValidationFilter(csrfTokenRepository()),
                UsernamePasswordAuthenticationFilter::class.java,
            ).addFilterAfter(
                SessionValidationFilter(sessionService, userRepository),
                UsernamePasswordAuthenticationFilter::class.java,
            ).csrf { it.disable() }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration =
            CorsConfiguration().apply {
                allowedOrigins = listOf("https://grantly.work")
                allowedMethods = listOf("POST", "GET", "DELETE", "PUT", "PATCH", "OPTIONS")
                allowedHeaders =
                    listOf("Authorization", "Content-Type", "X-Requested-With", "X-CSRF-TOKEN", "User-Agent")
                allowCredentials = true
            }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
