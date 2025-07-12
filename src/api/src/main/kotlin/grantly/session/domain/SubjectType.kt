package grantly.session.domain

enum class SubjectType(
    val type: Int,
) {
    MEMBER(0),
    USER(1),
    ;

    companion object {
        fun from(type: Int): SubjectType =
            SubjectType.entries.find { it.type == type }
                ?: throw IllegalArgumentException("Unknown SubjectType: $type")
    }
}
