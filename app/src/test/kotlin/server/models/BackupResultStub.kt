package server.models

import kotlin.random.Random
import server.instantForTest
import server.next

object BackupResultStub {
  fun get(backupName: BackupName) =
      BackupResult(
          name = "${backupName.value}/backupResults/${Random.next<String>()}".asBackupResultName(),
          startTime = instantForTest(),
          endTime = instantForTest(),
          result = BackupResultStatus.Success,
          output = "output",
      )
}
