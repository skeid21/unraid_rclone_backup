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

  fun get(backupName: BackupName): Backup? {
    return backupsDAL.get(backupName)
  }

  fun list(): List<Backup> {
    return backupsDAL.list()
  }

  fun update(backup: Backup): Backup? {
    return backupsDAL.update(backup)
  }

  fun delete(backupName: BackupName) {
    backupsDAL.delete(backupName)
  }
}
