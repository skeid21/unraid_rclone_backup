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
import server.config
import server.databaseFilePath
import server.injection.getInstance
import server.models.Backup
import server.models.asBackupName

fun initDatabaseConnection() {
  ensureDdFileDirectory()
  connectToDatabase()
  initTables()
}

private fun ensureDdFileDirectory() {
  val dbPath = Path.of(config.databaseFilePath).parent
  if (!Files.exists(dbPath)) {
    Files.createDirectories(dbPath)
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
  Database.connect("jdbc:sqlite:${config.databaseFilePath}", "org.sqlite.JDBC")
  // In memory
  //	Database.connect("jdbc:sqlite:file:test?mode=memorycache=shared", "org.sqlite.JDBC")

  // For both: set SQLite compatible isolation level, seE
  // https://github.com/JetBrains/Exposed/wiki/FAQ
  TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

  val backupsDAL = getInstance<BackupsDAL>()
  if (backupsDAL.get("backups/test1".asBackupName()) == null) {
    backupsDAL.create(Backup("backups/test1".asBackupName(), "test1", "config"))
  }

  if (backupsDAL.get("backups/test2".asBackupName()) == null) {
    backupsDAL.create(Backup("backups/test2".asBackupName(), "test2", "config"))
  }
}
