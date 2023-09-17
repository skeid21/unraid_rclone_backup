package server.models

import kotlin.random.Random
import server.instantForTest
import server.next

object BackupResultStub {
  fun get() =
      BackupResult(
          name = "backups/${Random.next<String>()}".asBackupResultName(),
          startTime = instantForTest(),
          endTime = instantForTest(),
          result = BackupResult.Result.Success,
          output = "output",
      )
}
