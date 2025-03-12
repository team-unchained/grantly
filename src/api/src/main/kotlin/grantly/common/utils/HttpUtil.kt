package grantly.common.utils

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
    }
}
