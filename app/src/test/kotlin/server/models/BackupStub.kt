package server.models

import java.nio.file.Files
import kotlin.random.Random
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import server.next

object BackupStub {
  fun get(): Backup =
      Backup(
          name = Random.next(),
          createTime = Clock.System.now(),
          displayName = Random.next(),
          cronSchedule = "0 * * * * ?",
          sourceDir = Files.createTempDirectory("com.unraid").toString(),
          destinationDir = Random.next(),
          config = Random.next())
}

fun Backup.toCompare(): Backup = this.copy(createTime = Instant.fromEpochMilliseconds(0))

fun List<Backup>.toCompare(): List<Backup> = this.map { it.toCompare() }
