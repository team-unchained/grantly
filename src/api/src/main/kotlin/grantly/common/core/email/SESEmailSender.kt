package grantly.common.core.email

import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.ses.SesClient
import aws.sdk.kotlin.services.ses.model.Body
import aws.sdk.kotlin.services.ses.model.Content
import aws.sdk.kotlin.services.ses.model.Destination
import aws.sdk.kotlin.services.ses.model.Message
import aws.sdk.kotlin.services.ses.model.SendEmailRequest
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

private val log = KotlinLogging.logger {}

@Profile("prod")
@Component
class SESEmailSender(
    private val templateEngine: SpringTemplateEngine,
) : EmailSender {
    private val awsRegion = "ap-northeast-2"

    @Value("\${grantly.aws.ses.profile-name}")
    private lateinit var awsProfileName: String

    @Value("\${grantly.aws.ses.source}")
    private lateinit var awsSource: String

    override suspend fun send(
        to: String,
        subject: String,
        body: String,
    ): Boolean {
        val emailRequest =
            makeSESSendRequest(
                to = to,
                subjectStr = subject,
                bodyStr = body,
            )
        SesClient {
            region = awsRegion
            credentialsProvider = ProfileCredentialsProvider(profileName = awsProfileName)
        }.use { client ->
            return try {
                log.info { "sending email to $to" }
                client.sendEmail(emailRequest)
                true
            } catch (e: Exception) {
                log.error("Failed to send email", e)
                false
            }
        }
    }

    private fun makeSESSendRequest(
        to: String,
        subjectStr: String,
        bodyStr: String,
    ): SendEmailRequest =
        SendEmailRequest {
            destination =
                Destination {
                    toAddresses = listOf(to)
                }
            message =
                Message {
                    subject =
                        Content {
                            data = subjectStr
                        }
                    body =
                        Body {
                            html =
                                Content {
                                    data = bodyStr
                                }
                        }
                }
            source = "Grantly Support <$awsSource>"
        }

    override fun buildHTML(
        template: String,
        params: Map<String, Any>,
    ): String {
        val context = Context()
        context.setVariables(params)
        return templateEngine.process(template, context)
    }
}
