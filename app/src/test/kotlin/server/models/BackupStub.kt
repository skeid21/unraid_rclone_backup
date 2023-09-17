package server.models

import java.nio.file.Files
import kotlin.random.Random
import kotlinx.datetime.Instant
import server.instantForTest
import server.next
import server.persistence.BackupRef

object BackupStub {
  fun get(): Backup =
      Backup(
          name = "backups/${Random.next<String>()}".asBackupName(),
          createTime = instantForTest(),
          lastSuccessfulRunTime = null,
          displayName = Random.next(),
          cronSchedule = "0 * * * *",
          sourceDir = Files.createTempDirectory("com.unraid").toString(),
          destinationDir = Random.next(),
          config = Random.next())
}

fun Backup.toCompare(): Backup = this.copy(createTime = Instant.fromEpochMilliseconds(0))

fun List<Backup>.toCompare(): List<Backup> = this.map { it.toCompare() }

fun BackupRef.toCompare(): Backup = this.entity.toCompare()
