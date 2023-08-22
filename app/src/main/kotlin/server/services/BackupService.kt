package server.services

import com.google.inject.Inject
import server.models.Backup
import server.models.BackupName
import server.models.asBackupName
import server.persistence.BackupsDAL

class BackupService
@Inject
constructor(
  val backupsDAL: BackupsDAL,
)
{
  init {
  	if(backupsDAL.get("backups/test1".asBackupName()) == null) {
      backupsDAL.create(Backup("backups/test1".asBackupName(), "test1", "config"))
    }


    if(backupsDAL.get("backups/test2".asBackupName()) == null) {
      backupsDAL.create(Backup("backups/test2".asBackupName(), "test2", "config"))
    }
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
