package server.models

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
    /** The unique resource name for the backujp */
    val name: BackupName,
    /** The display name for the */
    val displayName: String,
    /** A cron schedule string used to schedule the backup */
    val cronSchedule: String,
    /** The rclone configuration to use* */
    val config: String
)
