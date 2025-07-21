package grantly.config

import grantly.config.filter.CsrfValidationFilter
import grantly.config.filter.SessionContext
import grantly.config.filter.SessionValidationFilter
import grantly.member.application.port.out.MemberRepository
import grantly.session.application.service.SessionService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
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
    private val memberRepository: MemberRepository,
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
    @Profile("!test")
    fun csrfValidationFilter(csrfTokenRepository: CsrfTokenRepository): CsrfValidationFilter = CsrfValidationFilter(csrfTokenRepository)

    @Bean
    @Order(ORDER_PUBLIC_FILTER)
    fun publicFilterChain(
        http: HttpSecurity,
        csrfValidationFilter: CsrfValidationFilter?,
    ): SecurityFilterChain {
        http
            .securityMatcher(
                "/v*/system/**",
                "/admin/v*/auth/login",
                "/admin/v*/auth/signup",
                "/admin/v*/auth/csrf-token",
                "/admin/v*/auth/request-password-reset",
                "/admin/v*/auth/reset-password",
                "/docs/**",
            ).authorizeHttpRequests { it.anyRequest().permitAll() }
            .addFilterBefore(sessionContext, UsernamePasswordAuthenticationFilter::class.java)
        csrfValidationFilter?.let {
            http.addFilterAfter(it, UsernamePasswordAuthenticationFilter::class.java)
        }
        http.csrf { it.disable() }.cors { }
        return http.build()
    }

    @Bean
    @Order(ORDER_SECURE_FILTER)
    fun secureFilterChain(
        http: HttpSecurity,
        csrfValidationFilter: CsrfValidationFilter?,
    ): SecurityFilterChain {
        // SessionContext -> CsrfValidationFilter -> SessionValidationFilter
        http
            .authorizeHttpRequests { auth ->
                auth
                    .anyRequest()
                    .authenticated()
            }.addFilterBefore(sessionContext, UsernamePasswordAuthenticationFilter::class.java)
        csrfValidationFilter?.let {
            http.addFilterAfter(it, UsernamePasswordAuthenticationFilter::class.java)
        }
        http
            .addFilterAfter(
                SessionValidationFilter(sessionService, memberRepository),
                UsernamePasswordAuthenticationFilter::class.java,
            ).csrf { it.disable() }
            .cors { }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration =
            CorsConfiguration().apply {
                allowedOrigins = listOf("https://grantly.work", "https://grantly.unchainedevs.app")
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
