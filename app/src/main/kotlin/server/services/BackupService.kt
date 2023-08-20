package server.services

import server.models.Backup

class BackupService {
  companion object {
    val backups =
        mutableMapOf(
            "test1" to Backup("backups/test1", "test1", "config"),
            "test2" to Backup("backups/test2", "test2", "config"))
  }

  fun list(): List<Backup> {
    return backups.values.toList()
  }

  fun get(backupId: String): Backup? {
    return backups[backupId]
  }

  fun delete(backupId: String) {
    backups.remove(backupId)
  }
}
