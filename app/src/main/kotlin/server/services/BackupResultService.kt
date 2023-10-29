package server.services

import com.google.inject.Inject
import server.models.BackupName
import server.models.BackupResult
import server.models.BackupResultName
import server.persistence.BackupResultsDal

class BackupResultService @Inject constructor(private val backupResultsDal: BackupResultsDal) {
  fun create(backupResult: BackupResult): BackupResult = backupResultsDal.create(backupResult)

  fun get(name: BackupResultName): BackupResult? = backupResultsDal.get(name)

  fun getMostRecentResult(parent: BackupName): BackupResult? =
      backupResultsDal.getMostRecentResult(parent)

  fun list(parent: BackupName): List<BackupResult> = backupResultsDal.list(parent)

  fun delete(name: BackupResultName) = backupResultsDal.delete(name)
}
