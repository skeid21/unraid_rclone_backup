package server.models

import kotlinx.datetime.Instant

@JvmInline
value class BackupResultName(val value: String) {
  init {
    require(value.isNotBlank())
  }

  fun id(): String {
    return value.substringAfter("/")
  }
}

fun String.asBackupResultName() = BackupResultName(this)

fun String.idToBackupResultName() = BackupResultName("backupResults/$this")

/** The result of running a backup */
data class BackupResult(
    /** The unique resource name for the BackupResult */
    val name: BackupResultName,
    /** The time tha the backup process started */
    val startTime: Instant,
    /** The time that the backup process ended */
    val endTime: Instant,
    /** The result of the backup process */
    val result: Result,
    /** The output of the backup process */
    val output: String
) {
  enum class Result {
    Unknown,
    Success,
    Failure,
  }
}
