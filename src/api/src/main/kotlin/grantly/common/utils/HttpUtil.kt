package grantly.common.utils

import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

class HttpUtil {
    companion object {
        fun buildLocationURI(location: String): URI {
            val currentPath = ServletUriComponentsBuilder.fromCurrentRequestUri().build().path
            // 현재 버전이 나타난 부분 추출 /v1/
            val versionPath = currentPath?.let { Regex("/v\\d+/").find(it)?.value } ?: "/v1/"
            val newPath: String =
                if (location.startsWith("/")) {
                    versionPath + location.substring(1)
                } else {
                    versionPath + location
                }
            return ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath(newPath)
                .build()
                .toUri()
        }
    }
}
