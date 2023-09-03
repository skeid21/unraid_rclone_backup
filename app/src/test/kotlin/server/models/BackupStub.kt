package server.models

import kotlin.random.Random
import server.next

object BackupStub {
  fun get(): Backup =
      Backup(
          name = Random.next(),
          displayName = Random.next(),
          cronSchedule = "0 * * * * ?",
          config = Random.next())
}
