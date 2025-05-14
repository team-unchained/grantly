package grantly.common.core.email

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!prod")
@Component
class ConsoleEmailSender : EmailSender {
    override fun send(
        to: String,
        subject: String,
        body: String,
    ): Boolean {
        println("Sending email to: $to")
        println("Subject: $subject")
        println("Body: $body")
        return true
    }
}
