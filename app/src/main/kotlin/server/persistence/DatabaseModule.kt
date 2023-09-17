package server.persistence

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.nio.file.Files
import java.nio.file.Path
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.SQLiteConfig
import server.config
import server.databaseFilePath

class DatabaseModule : AbstractModule() {
  @Provides
  fun database(): Database {
    if (database == null) {
      initDatabaseConnection()
    }
    return database!!
  }
}

var database: Database? = null

fun initDatabaseConnection() {
  // for testing
  if (config.databaseFilePath != ":memory:") {
    // used for testing
    val dbPath = Path.of(config.databaseFilePath).parent
    if (!Files.exists(dbPath)) {
      Files.createDirectories(dbPath)
    }
  }

  val dataSource =
      HikariDataSource(
          HikariConfig().apply {
            driverClassName = "org.sqlite.JDBC"
            jdbcUrl = "jdbc:sqlite:${config.databaseFilePath}"
            maximumPoolSize = 2
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_SERIALIZABLE"
            dataSourceProperties = SQLiteConfig().apply { enforceForeignKeys(true) }.toProperties()
            validate()
          })

  database = Database.connect(dataSource)

  transaction(database) {
    SchemaUtils.create(Backups)
    SchemaUtils.createMissingTablesAndColumns(Backups)
    SchemaUtils.create(BackupResults)
    SchemaUtils.createMissingTablesAndColumns(BackupResults)
  }
}
