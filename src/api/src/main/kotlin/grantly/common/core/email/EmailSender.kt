package grantly.common.core.email

interface EmailSender {
    suspend fun send(
        to: String,
        subject: String,
        body: String,
    ): Boolean

    fun buildHTML(
        template: String,
        params: Map<String, Any>,
    ): String
}
