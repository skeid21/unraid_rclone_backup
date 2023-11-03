package server.persistence

import com.google.inject.AbstractModule
import com.google.inject.Provides
import org.jetbrains.exposed.sql.Database

class DatabaseModuleTest : AbstractModule() {
  @Provides fun database(): Database = database

  companion object {
    lateinit var database: Database

    fun initDatabaseForTesting() {
      val ds = DatabaseFactory.getDataSource()
      DatabaseFactory.applyMigrations(ds)
      database = DatabaseFactory.getDatabase(ds)
    }
  }
}
