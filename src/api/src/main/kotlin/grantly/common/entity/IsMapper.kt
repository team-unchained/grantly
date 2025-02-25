package grantly.common.entity

interface IsMapper<E, D> {
    fun toDomain(entity: E): D
    fun toEntity(domain: D): E
}