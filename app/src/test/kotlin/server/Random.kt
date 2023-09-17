package server

import java.util.UUID
import kotlin.random.Random

inline fun <reified T> Random.next(): T {
  return when (T::class) {
    String::class -> UUID.randomUUID().toString() as T
    Int::class -> Random.nextInt() as T
    else -> throw IllegalStateException("Unsupported type for random call")
  }
}
