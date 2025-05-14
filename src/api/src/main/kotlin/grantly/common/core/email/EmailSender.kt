package grantly.common.core.email

interface EmailSender {
    fun send(
        to: String,
        subject: String,
        body: String,
    ): Boolean
}
