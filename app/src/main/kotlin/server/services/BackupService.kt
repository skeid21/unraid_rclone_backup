package server.services

import com.google.inject.Inject
import io.ktor.server.plugins.BadRequestException
import org.quartz.CronExpression
import server.models.Backup
import server.models.BackupName
import server.persistence.BackupsDAL

class BackupService
@Inject
constructor(
    private val backupsDAL: BackupsDAL,
) {

  fun create(backup: Backup): Backup {
    return backupsDAL.create(backup.isValidOrThrow())
  }

  fun get(backupName: BackupName): Backup? {
    return backupsDAL.get(backupName)
  }

  fun list(): List<Backup> {
    return backupsDAL.list()
  }

  fun update(backup: Backup): Backup? {
    return backupsDAL.update(backup.isValidOrThrow())
  }

  fun delete(backupName: BackupName) {
    backupsDAL.delete(backupName)
  }

  /** Will throw a [BadRequestException] if the backup is not valid */
  private fun Backup.isValidOrThrow(): Backup {
    val errorBuilder = StringBuilder()
    if (!CronExpression.isValidExpression(cronSchedule)) {
      errorBuilder.appendLine(INVALID_CRON_SCHEDULE_MESSAGE)
    }

    val errorMessage = errorBuilder.toString()
    if (errorMessage.isNotBlank()) {
      throw BadRequestException(errorMessage)
    }

    return this
  }

  companion object {
    const val INVALID_CRON_SCHEDULE_MESSAGE = "cronSchedule is not a valid cron specification"
  }
}
