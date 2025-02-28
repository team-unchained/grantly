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
    vararg properties: KProperty1<T, *>,
): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    @Suppress("UNCHECKED_CAST")
    val otherObj = other as T
    val thisId = idProperty.get(this)
    val otherId = idProperty.get(otherObj)

    return if (thisId != null && otherId != null) {
        thisId == otherId // ID가 있으면 ID만 비교
    } else {
        properties.all { it.get(this) == it.get(otherObj) } // ID가 없으면 모든 필드 비교
    }
}

// hashCode() 자동 생성 함수
fun <T : Any> T.entityHashCode(vararg properties: KProperty1<T, *>): Int {
    // 0 에서 시작하는 누적값(acc)과 소수 31을 곱한 값에 각 프로퍼티의 해시코드를 더하는 과정을 반복한 값을 반환
    return properties.fold(0) { acc, prop -> 31 * acc + (prop.get(this)?.hashCode() ?: 0) }
}
