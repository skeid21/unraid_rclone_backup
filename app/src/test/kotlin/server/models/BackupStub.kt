package server.models

import java.nio.file.Files
import kotlin.random.Random
import server.next

object BackupStub {
  fun get(): Backup =
      Backup(
          name = Random.next(),
          displayName = Random.next(),
          cronSchedule = "0 * * * * ?",
          sourceDir = Files.createTempDirectory("com.unraid").toString(),
          destinationDir = Random.next(),
          config = Random.next())
}
