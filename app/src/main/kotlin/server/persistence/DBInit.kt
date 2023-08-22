package server.persistence

import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

fun initDatabaseConnection() {
	ensureDdFileDirectory()
	connectToDatabase()
	initTables()
}

private const val databaseFilePath = "/tmp/data.db"
private fun ensureDdFileDirectory() {
	if (!Files.exists(Path.of(databaseFilePath).parent))	 {
		Files.createDirectories(Path.of(databaseFilePath).parent)
	}
}

private fun initTables() {
	transaction {
		addLogger(StdOutSqlLogger)
		SchemaUtils.create(Backups)
	}
}

private fun connectToDatabase() {
	// In file
	Database.connect("jdbc:sqlite:$databaseFilePath", "org.sqlite.JDBC")
	// In memory
//	Database.connect("jdbc:sqlite:file:test?mode=memorycache=shared", "org.sqlite.JDBC")

	// For both: set SQLite compatible isolation level, seE
// https://github.com/JetBrains/Exposed/wiki/FAQ
	TransactionManager.manager.defaultIsolationLevel =
		Connection.TRANSACTION_SERIALIZABLE
}

