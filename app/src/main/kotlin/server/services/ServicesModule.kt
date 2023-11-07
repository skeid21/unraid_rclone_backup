package server.services

import com.google.inject.AbstractModule
import org.quartz.spi.JobFactory

class ServicesModule : AbstractModule() {
  override fun configure() {
    bind(JobFactory::class.java).to(BackupJobFactory::class.java)
  }
}
