package server.models

import kotlinx.datetime.Instant

data class BackupResultName(val value: String) {
  val parent: BackupName
  val id: String

  init {
    val matchResult =
        nameRegex.matchEntire(value)
            ?: throw IllegalStateException("$value is not a valid BackupResultName")
    parent = BackupName("backups/${matchResult.groupValues[1]}")
    id = matchResult.groupValues[2]
  }

  companion object {
    private val nameRegex = Regex("backups/([^/]+)/backupResults/([^/]+)$")
  }
}

fun String.asBackupResultName() = BackupResultName(this)

/** The result of running a backup */
data class BackupResult(
    /** The unique resource name for the BackupResult */
    val name: BackupResultName,
    /** The time tha the backup process started */
    val startTime: Instant,
    /** The time that the backup process ended */
    val endTime: Instant,
    /** The status of the backup process */
    val status: Status,
    /** The output of the backup process */
    val output: String
) {

  enum class Status {
    Unknown,
    Success,
    Failure,
  }
}
