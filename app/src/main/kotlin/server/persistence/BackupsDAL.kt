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


/**Data definition for the Backups table**/
object Backups : IntIdTable() {
	val name: Column<String> = varchar("name", 256).uniqueIndex()
	val displayName: Column<String> = varchar("display_name", 256)
}

/**DAO for interacting with the [Backups] table **/
class DAOBackup(id: EntityID<Int>): IntEntity(id) {
	companion object : IntEntityClass<DAOBackup>(Backups)
	var name by Backups.name.uniqueIndex()
	var displayName by Backups.displayName
}

/**
 * A data access layer class for interacting with [Backup] core models
 */
class BackupsDAL {
	fun list(): List<Backup>  = transaction { DAOBackup.all().map { it.toCoreModel()}}

	fun get(name: BackupName): Backup? = transaction {  getByName(name)}?.toCoreModel()

	fun delete(name: BackupName) = transaction { getByName(name)?.delete() }

	fun create(backup: Backup): Backup = transaction {
	  DAOBackup.new {
			name = backup.name.value
			displayName = backup.displayName
		}
	}.toCoreModel()

	private fun getByName(name: BackupName): DAOBackup? = DAOBackup.find { Backups.name eq name.value }.firstOrNull()
}

/** Converts a [DAOBackup] to a [Backup]**/
private fun DAOBackup.toCoreModel(): Backup =
  Backup(
		name = name.asBackupName(),
		displayName = displayName,
		//TODO store config on disk?
		config = ""
	)
