package grantly.config

object TestSessionTokenHolder {
    private val token = ThreadLocal<String>()

    fun set(tokenValue: String) {
        token.set(tokenValue)
    }

    fun get(): String = token.get() ?: error("No session token set")

    fun clear() {
        token.remove()
    }
}
