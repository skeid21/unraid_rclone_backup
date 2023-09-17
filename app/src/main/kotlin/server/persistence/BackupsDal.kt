package server.persistence

import com.google.inject.Inject
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import server.models.Backup
import server.models.BackupName
import server.models.asBackupName

/** Data definition for the Backups table* */
object Backups : IntIdTable() {
  val name: Column<String> = varchar("name", 2083).uniqueIndex()
  val createTime: Column<Instant> = timestamp("create_time")
  val lastSuccessfulRunTime: Column<Instant?> = timestamp("last_successful_run_time").nullable()
  val displayName: Column<String> = varchar("display_name", 256)
  val cronSchedule: Column<String> = varchar("cron_schedule", 256)
  val sourceDir: Column<String> = varchar("source_dir", 4096)
  val destinationDir: Column<String> = varchar("destination_dir", 4096)
  val config: Column<String> = text("config")
}

/** DAO for interacting with the [Backups] table * */
class DAOBackup(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<DAOBackup>(Backups)

  var name by Backups.name
  var createTime by Backups.createTime
  var lastSuccessfulRunTime by Backups.lastSuccessfulRunTime
  var displayName by Backups.displayName
  var cronSchedule by Backups.cronSchedule
  var sourceDir by Backups.sourceDir
  var destinationDir by Backups.destinationDir
  var config by Backups.config
}

/** A data access layer for persisting [Backup] core models */
class BackupsDal @Inject constructor(private val db: Database) {
  fun create(backup: Backup): Backup =
      transaction(db) {
            DAOBackup.new {
              name = backup.name.value
              createTime = Clock.System.now()
              update(backup)
            }
          }
          .toBackup()

  fun get(name: BackupName): Backup? = transaction(db) { getByName(name) }?.toBackup()

  fun list(): List<Backup> = transaction(db) { DAOBackup.all().map { it.toBackup() } }

  fun update(backup: Backup): Backup? =
      transaction(db) { getByName(backup.name)?.apply { update(backup) } }?.toBackup()

  fun delete(name: BackupName) = transaction(db) { getByName(name)?.delete() }

  private fun getByName(name: BackupName): DAOBackup? =
      DAOBackup.find { Backups.name eq name.value }.firstOrNull()
}

/** Update the [DAOBackup] with fields from the specified [Backup] */
private fun DAOBackup.update(backup: Backup) {
  lastSuccessfulRunTime = backup.lastSuccessfulRunTime
  displayName = backup.displayName
  cronSchedule = backup.cronSchedule
  sourceDir = backup.sourceDir
  destinationDir = backup.destinationDir
  config = backup.config
}

/** Converts a [DAOBackup] to a [Backup]* */
private fun DAOBackup.toBackup(): Backup =
    Backup(
        name = name.asBackupName(),
        createTime = createTime,
        lastSuccessfulRunTime = lastSuccessfulRunTime,
        displayName = displayName,
        cronSchedule = cronSchedule,
        sourceDir = sourceDir,
        destinationDir = destinationDir,
        config = config)
