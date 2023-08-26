package server.services

import com.google.inject.Inject
import server.models.Backup
import server.models.BackupName
import server.persistence.BackupsDAL

class BackupService
@Inject
constructor(
    val backupsDAL: BackupsDAL,
) {

  fun create(backup: Backup): Backup {
    return backupsDAL.create(backup)
  }

  fun list(): List<Backup> {
    return backupsDAL.list()
  }

  fun get(backupName: BackupName): Backup? {
    return backupsDAL.get(backupName)
  }

  fun delete(backupName: BackupName) {
    backupsDAL.delete(backupName)
  }
}
