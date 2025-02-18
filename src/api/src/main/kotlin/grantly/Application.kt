package grantly

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application {
    private val log = LoggerFactory.getLogger(Application::class.java)
}

fun main() {
    runApplication<Application>()
}
