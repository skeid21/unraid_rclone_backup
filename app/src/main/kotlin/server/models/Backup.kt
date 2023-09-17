package server.models

import kotlinx.datetime.Instant

@JvmInline
value class BackupName(val value: String) {
  init {
    require(value.isNotBlank())
  }

  fun id(): String {
    return value.substringAfter("/")
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
