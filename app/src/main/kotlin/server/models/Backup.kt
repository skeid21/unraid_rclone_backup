package server.models

@JvmInline
value class BackupName(val value: String) {
	init {
		require(value.isNotBlank())
	}
}

fun String.asBackupName() = BackupName(this)

data class Backup(val name: BackupName, val displayName: String, val config: String)
