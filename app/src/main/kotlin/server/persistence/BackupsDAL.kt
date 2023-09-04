package server.persistence

import com.google.inject.Inject
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.transactions.transaction
import server.models.Backup
import server.models.BackupName
import server.models.asBackupName
import server.persistence.Backups.destinationDir
import server.persistence.Backups.sourceDir
import server.persistence.Backups.uniqueIndex

/** Data definition for the Backups table* */
object Backups : IntIdTable() {
  val name: Column<String> = varchar("name", 256).uniqueIndex()
  val displayName: Column<String> = varchar("display_name", 256)
  val cronSchedule: Column<String> = varchar("cron_schedule", 256)
  val sourceDir: Column<String> = varchar("source_dir", 4096)
  val destinationDir: Column<String> = varchar("destination_dir", 4096)
  val config: Column<String> = text("config")
}

/** DAO for interacting with the [Backups] table * */
class DAOBackup(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<DAOBackup>(Backups)

  var name by Backups.name.uniqueIndex()
  var displayName by Backups.displayName
  var cronSchedule by Backups.cronSchedule
  var sourceDir by Backups.sourceDir
  var destinationDir by Backups.destinationDir
  var config by Backups.config
}

/** A data access layer for persisting [Backup] core models */
class BackupsDAL @Inject constructor(private val db: Database) {
  fun create(backup: Backup): Backup =
      transaction(db) {
            DAOBackup.new {
              name = backup.name.value
              update(backup)
            }
          }
          .toCoreModel()

  fun get(name: BackupName): Backup? = transaction(db) { getByName(name) }?.toCoreModel()

  fun list(): List<Backup> = transaction(db) { DAOBackup.all().map { it.toCoreModel() } }

  fun delete(name: BackupName) = transaction(db) { getByName(name)?.delete() }

  fun update(backup: Backup): Backup? =
      transaction(db) {
            getByName(backup.name)?.apply {
              update(backup)
            }
          }
          ?.toCoreModel()


  private fun getByName(name: BackupName): DAOBackup? =
      DAOBackup.find { Backups.name eq name.value }.firstOrNull()
}


/** Update the [DAOBackup] with fields from the specified [Backup] */
private fun DAOBackup.update(backup: Backup) {
  displayName = backup.displayName
  cronSchedule = backup.cronSchedule
  sourceDir = backup.sourceDir
  destinationDir = backup.destinationDir
  config = backup.config
}

/** Converts a [DAOBackup] to a [Backup]* */
private fun DAOBackup.toCoreModel(): Backup =
    Backup(
        name = name.asBackupName(),
        displayName = displayName,
        cronSchedule = cronSchedule,
        sourceDir = sourceDir,
        destinationDir = destinationDir,
        config = config)
