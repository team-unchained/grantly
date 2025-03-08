package grantly.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Grantly API")
                    .description("Grantly 서비스의 API 문서")
                    .version("v1.0.0")
                    .contact(Contact().name("Grantly Team").email("team@grantly.com"))
                    .license(License().name("MIT License").url("https://opensource.org/licenses/MIT")),
            )
}
