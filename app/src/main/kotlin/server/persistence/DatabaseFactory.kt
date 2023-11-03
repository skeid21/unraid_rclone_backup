package server.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.nio.file.Files
import java.nio.file.Path
import javax.sql.DataSource
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import org.jetbrains.exposed.sql.Database
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteOpenMode
import server.config
import server.databaseFilePath
import server.databaseNumConnections

object DatabaseFactory {
  fun getDataSource(): DataSource {
    // for testing
    if (config.databaseFilePath != ":memory:") {
      // used for testing
      val dbPath = Path.of(config.databaseFilePath).parent
      if (!Files.exists(dbPath)) {
        Files.createDirectories(dbPath)
      }
    }

    return HikariDataSource(
        HikariConfig().apply {
          driverClassName = "org.sqlite.JDBC"
          jdbcUrl = "jdbc:sqlite:${config.databaseFilePath}"
          maximumPoolSize = config.databaseNumConnections
          isAutoCommit = false
          transactionIsolation = "TRANSACTION_SERIALIZABLE"
          dataSourceProperties =
              SQLiteConfig()
                  .apply {
                    enforceForeignKeys(true)
                    setOpenMode(SQLiteOpenMode.FULLMUTEX)
                  }
                  .toProperties()
          validate()
        })
  }

  fun getDatabase(ds: DataSource = getDataSource()): Database = Database.connect(ds)

  fun applyMigrations(ds: DataSource = getDataSource()): MigrateResult =
      Flyway.configure().dataSource(ds).load().migrate()
}
