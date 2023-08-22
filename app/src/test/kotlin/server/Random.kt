package server

import java.util.UUID
import kotlin.random.Random
import server.models.BackupName
import server.models.asBackupName

inline fun <reified T> Random.next(): T {
 return when(T::class) {
	 String::class -> UUID.randomUUID().toString() as T
	 BackupName::class -> UUID.randomUUID().toString().asBackupName() as T
	 else -> throw IllegalStateException("Unsupported type for random call")
 }
}