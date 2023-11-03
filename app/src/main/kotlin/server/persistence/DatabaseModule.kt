package server.persistence

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import org.jetbrains.exposed.sql.Database

class DatabaseModule : AbstractModule() {
  @Provides
  @Singleton
  fun database(): Database {
    return DatabaseFactory.getDatabase()
  }
}
