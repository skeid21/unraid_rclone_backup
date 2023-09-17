package server.models

import kotlinx.datetime.Instant

data class BackupName(val value: String) {
  val id: String
  init {
    val matchResult = nameRegex.matchEntire(value) ?: throw IllegalStateException("$value is not a valid BackupName")
    id = matchResult.groupValues[1]
  }

  companion object {
    private val nameRegex = Regex("backups/([^/]+)$")
  }
}

fun String.asBackupName() = BackupName(this)

fun String.idToName() = BackupName("backups/$this")

data class Backup(
    /** The unique resource name for the backup */
    val name: BackupName,
    /** The creation time for this backup */
    val createTime: Instant,
    /** The last successful time that this backup ran */
    val lastSuccessfulRunTime: Instant?,
    /** The display name for the backup */
    val displayName: String,
    /** A cron schedule string used to schedule the backup */
    val cronSchedule: String,
    /** The source directory that the backup should read from */
    val sourceDir: String,
    /** The destination directory that the backup should write to */
    val destinationDir: String,
    /** The rclone configuration to use* */
    val config: String
)
