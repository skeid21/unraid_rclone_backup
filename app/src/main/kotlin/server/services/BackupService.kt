package server.services

import com.google.inject.Inject
import io.ktor.server.plugins.BadRequestException
import java.nio.file.Paths
import kotlin.io.path.exists
import org.quartz.CronExpression
import server.models.Backup
import server.models.BackupName
import server.persistence.BackupsDal

class BackupService
@Inject
constructor(
    private val backupsDal: BackupsDal,
) {

  fun create(backup: Backup): Backup {
    return backupsDal.create(backup.isValidOrThrow())
  }

  fun get(backupName: BackupName): Backup? {
    return backupsDal.get(backupName)
  }

  fun list(): List<Backup> {
    return backupsDal.list()
  }

  fun update(backup: Backup): Backup? {
    return backupsDal.update(backup.isValidOrThrow())
  }

  fun delete(backupName: BackupName) {
    backupsDal.delete(backupName)
  }

  /** Will throw a [BadRequestException] if the backup is not valid */
  private fun Backup.isValidOrThrow(): Backup {
    fun isValidCron(cronSchedule: String): Boolean {
      val parts = cronSchedule.split(" ")
      // Only support 6 parts cron schedule
      if (parts.count() != 6) {
        return false
      }

      if (!CronExpression.isValidExpression("$cronSchedule")) {
        return false
      }

      return true
    }

    val errorBuilder = StringBuilder()

    if (!isValidCron(cronSchedule)) {
      errorBuilder.appendLine(INVALID_CRON_SCHEDULE_MESSAGE)
    }

    if (sourceDir.isBlank()) {
      errorBuilder.appendLine(SOURCE_DIR_CANNOT_BE_EMPTY)
    }

    if (!Paths.get(sourceDir).exists()) {
      errorBuilder.appendLine(SOURCE_DIR_DOES_NOT_EXIST)
    }

    if (destinationDir.isBlank()) {
      errorBuilder.appendLine(DESTINATION_DIR_CANNOT_BE_EMPTY)
    }

    val errorMessage = errorBuilder.toString()
    if (errorMessage.isNotBlank()) {
      throw BadRequestException(errorMessage)
    }

    return this
  }

  companion object {
    const val INVALID_CRON_SCHEDULE_MESSAGE = "cronSchedule is not a valid cron specification"
    const val SOURCE_DIR_CANNOT_BE_EMPTY = "sourceDir cannot be empty"
    const val SOURCE_DIR_DOES_NOT_EXIST = "sourceDir does not exist"
    const val DESTINATION_DIR_CANNOT_BE_EMPTY = "destinationDir cannot be empty"
  }
}
