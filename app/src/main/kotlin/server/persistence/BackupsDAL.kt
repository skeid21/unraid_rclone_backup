package server.persistence

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction
import server.models.Backup
import server.models.BackupName
import server.models.asBackupName
import server.persistence.Backups.uniqueIndex

/** Data definition for the Backups table* */
object Backups : IntIdTable() {
  val name: Column<String> = varchar("name", 256).uniqueIndex()
  val displayName: Column<String> = varchar("display_name", 256)
  val cronSchedule: Column<String> = varchar("cron_schedule", 256)
  val config: Column<String> = text("config")
}

/** DAO for interacting with the [Backups] table * */
class DAOBackup(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<DAOBackup>(Backups)

  var name by Backups.name.uniqueIndex()
  var displayName by Backups.displayName
  var cronSchedule by Backups.cronSchedule
  var config by Backups.config
}

/** A data access layer for persisting [Backup] core models */
class BackupsDAL {
  fun create(backup: Backup): Backup =
      transaction {
            DAOBackup.new {
              name = backup.name.value
              displayName = backup.displayName
              cronSchedule = backup.cronSchedule
              config = backup.config
            }
          }
          .toCoreModel()

  fun get(name: BackupName): Backup? = transaction { getByName(name) }?.toCoreModel()

  fun list(): List<Backup> = transaction { DAOBackup.all().map { it.toCoreModel() } }

  fun delete(name: BackupName) = transaction { getByName(name)?.delete() }

  fun update(backup: Backup): Backup? =
      transaction {
            getByName(backup.name)?.apply {
              displayName = backup.displayName
              cronSchedule = backup.cronSchedule
              config = backup.config
            }
          }
          ?.toCoreModel()

  private fun getByName(name: BackupName): DAOBackup? =
      DAOBackup.find { Backups.name eq name.value }.firstOrNull()
}

/** Converts a [DAOBackup] to a [Backup]* */
private fun DAOBackup.toCoreModel(): Backup =
    Backup(
        name = name.asBackupName(),
        displayName = displayName,
        cronSchedule = cronSchedule,
        config = config)