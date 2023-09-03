package server.persistence

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
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

class DatabaseModule : AbstractModule() {

  @Provides
  @Singleton
  fun database(): Database {
    return initDatabaseConnection()
  }
}

fun initDatabaseConnection(): Database {
  fun ensureDdFileDirectory() {
    val dbPath = Path.of(config.databaseFilePath).parent
    if (!Files.exists(dbPath)) {
      Files.createDirectories(dbPath)
    }
  }

  fun connectToDatabase(): Database {
    ensureDdFileDirectory()
    val db = Database.connect("jdbc:sqlite:${config.databaseFilePath}", "org.sqlite.JDBC")
    // For both: set SQLite compatible isolation level, seE
    // https://github.com/JetBrains/Exposed/wiki/FAQ
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    return db
  }

  return connectToDatabase().apply {
    transaction(this) {
      addLogger(StdOutSqlLogger)
      SchemaUtils.create(Backups)
      SchemaUtils.createMissingTablesAndColumns(Backups)
    }
  }
}
