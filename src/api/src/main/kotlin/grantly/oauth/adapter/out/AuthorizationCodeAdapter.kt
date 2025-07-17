package grantly.oauth.adapter.out

import grantly.common.annotations.PersistenceAdapter
import grantly.oauth.application.port.`in`.dto.AuthorizeClientParams
import grantly.oauth.application.port.out.AuthorizationCodeRepository
import org.springframework.data.redis.core.StringRedisTemplate
import java.util.concurrent.TimeUnit

@PersistenceAdapter
class AuthorizationCodeAdapter(
    private val redisTemplate: StringRedisTemplate,
) : AuthorizationCodeRepository {
    companion object {
        private const val AUTH_CODE_KEY = "oauth:auth_code:%s:%d" // client_id:user_id
        private const val EXPIRATION_TIME = 5L
    }

    override fun save(
        params: AuthorizeClientParams,
        authCode: String,
    ) {
        val key = AUTH_CODE_KEY.format(params.clientId, params.userId)
        redisTemplate.opsForValue().set(key, authCode)
        redisTemplate.expire(key, EXPIRATION_TIME, TimeUnit.MINUTES)
    }

    override fun getAndRemove(
        clientId: String,
        userId: Long,
    ): String? {
        val key = AUTH_CODE_KEY.format(clientId, userId)
        val authCode = redisTemplate.opsForValue().get(key)
        if (authCode != null) {
            redisTemplate.delete(key)
        }
        return authCode
    }
}
