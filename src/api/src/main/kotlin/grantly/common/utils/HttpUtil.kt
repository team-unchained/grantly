package grantly.common.utils

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

class HttpUtil {
    companion object {
        fun buildLocationURI(location: String): URI {
            val re = Regex("/v[0-9]+/.*")
            if (!re.matches(location)) {
                throw Exception("location must include version information")
            } else {
                return ServletUriComponentsBuilder
                    .fromCurrentRequestUri()
                    .path(location)
                    .build()
                    .toUri()
            }
        }

        fun buildCookie(
            key: String,
            value: String,
        ) = CookieBuilder(key, value)

        fun getCookie(
            request: HttpServletRequest,
            key: String,
        ): Cookie? = request.cookies?.firstOrNull { it.name == key }
    }

    class CookieBuilder(
        name: String,
        value: String,
    ) {
        private val cookie = Cookie(name, value)

        fun maxAge(maxAge: Int) = apply { cookie.maxAge = maxAge }

        fun domain(domain: String) = apply { cookie.domain = domain }

        fun secure(secure: Boolean) = apply { cookie.secure = secure }

        fun httpOnly(httpOnly: Boolean) = apply { cookie.isHttpOnly = httpOnly }

        fun sameSite(sameSite: String) = apply { cookie.setAttribute("SameSite", sameSite) }

        fun build(response: HttpServletResponse) {
            response.addCookie(cookie)
        }
    }
}
