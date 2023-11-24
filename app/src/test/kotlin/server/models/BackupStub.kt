package server.models

import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.random.Random
import kotlinx.datetime.Instant
import server.TestHarness
import server.instantForTest
import server.next

object BackupStub {
  fun get(
      cronSchedule: String = "* * * ? * *",
      schedulePaused: Boolean = false,
      config: String = Random.next(),
      sourceDir: String =
          Paths.get(TestHarness.TESTING_TEMP_DIR, Random.next<String>())
              .createDirectories()
              .toString(),
      destinationDir: String = Random.next()
  ): Backup =
      Backup(
          name = "backups/${Random.next<String>()}".asBackupName(),
          createTime = instantForTest(),
          displayName = Random.next(),
          cronSchedule = cronSchedule,
          schedulePaused = schedulePaused,
          sourceDir = sourceDir,
          destinationDir = destinationDir,
          config = config)
}

fun Backup.toCompare(): Backup = this.copy(createTime = Instant.fromEpochMilliseconds(0))

fun List<Backup>.toCompare(): List<Backup> = this.map { it.toCompare() }
