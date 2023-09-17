package server.persistence

data class EntityRef<T>(val entity: T, val primaryId: Int)

fun <T> List<EntityRef<T>>.toEntityList() = this.map { it.entity }
