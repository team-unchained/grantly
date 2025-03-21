package grantly.common.entity

import kotlin.reflect.KProperty1

// toString() 자동 생성 함수
fun <T : Any> T.entityToString(vararg properties: KProperty1<T, *>): String {
    val className = this::class.simpleName
    val propsString = properties.joinToString(", ") { "${it.name}=${it.get(this)}" }
    return "$className($propsString)"
}

// equals() 자동 생성 함수
fun <T : Any> T.entityEquals(
    other: Any?,
    idProperty: KProperty1<T, *>,
): Boolean {
    // 같은 객체인지 확인
    if (this === other) return true
    // 타입이 다르면 비교할 수 없음
    if (other == null || this::class != other::class) return false

    @Suppress("UNCHECKED_CAST")
    val otherObj = other as T
    val thisId = idProperty.get(this)
    val otherId = idProperty.get(otherObj)

    // 둘 다 아직 영속화 안 되었으면 같다고 할 수 없음
    if (thisId == null || otherId == null) return false

    return thisId == other
}

// hashCode() 자동 생성 함수
fun <T : Any> T.entityHashCode(idProperty: KProperty1<T, *>): Int {
    val id = idProperty.get(this)
    // 아직 영속화 안 되었으면 해시코드 0
    return id?.hashCode() ?: 0
}
