package server.persistence

import com.google.inject.Inject
import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import server.models.BackupName
import server.models.BackupResult
import server.models.BackupResultName
import server.models.asBackupResultName

/** Data definition for the BackupResults table */
object BackupResults : IntIdTable() {
  val name: Column<String> = varchar("name", 2083).uniqueIndex()
  val parentName: Column<String> = varchar("parent_name", 2083)
  val startTime: Column<Instant> = timestamp("start_time")
  val endTime: Column<Instant> = timestamp("end_time")
  val status: Column<String> = varchar("result", 50)
  val output: Column<String> = text("output")

  init {
    foreignKey(
        parentName to Backups.name,
        onUpdate = ReferenceOption.RESTRICT,
        onDelete = ReferenceOption.CASCADE)
  }
}

/** DAO for interacting with the [BackupResults] table * */
class DAOBackupResult(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<DAOBackupResult>(BackupResults)

  var name by BackupResults.name
  var parentName by BackupResults.parentName
  var startTime by BackupResults.startTime
  var endTime by BackupResults.endTime
  var status by BackupResults.status
  var output by BackupResults.output
}

class BackupResultsDal @Inject constructor(private val db: Database) {
  fun create(backupResult: BackupResult) =
      transaction(db) {
            DAOBackupResult.new {
              name = backupResult.name.value
              parentName = backupResult.name.parent.value
              startTime = backupResult.startTime
              endTime = backupResult.endTime
              status = backupResult.status.name
              output = backupResult.output
            }
          }
          .toBackupResult()

  fun get(name: BackupResultName): BackupResult? =
      transaction(db) { getByName(name) }?.toBackupResult()

  fun getMostRecentResult(parentName: BackupName): BackupResult? =
      transaction(db) {
        DAOBackupResult.find { BackupResults.parentName eq parentName.value }
            .orderBy(BackupResults.endTime to SortOrder.ASC)
            .lastOrNull()
            ?.toBackupResult()
      }

  fun list(parentName: BackupName): List<BackupResult> =
      transaction(db) {
        DAOBackupResult.find { BackupResults.parentName eq parentName.value }
            .map { it.toBackupResult() }
      }

  fun delete(name: BackupResultName) = transaction(db) { getByName(name)?.delete() }

  private fun getByName(name: BackupResultName): DAOBackupResult? =
      DAOBackupResult.find { BackupResults.name eq name.value }.firstOrNull()
}

fun DAOBackupResult.toBackupResult() =
    BackupResult(
        name = name.asBackupResultName(),
        startTime = startTime,
        endTime = endTime,
        status = BackupResult.Status.valueOf(status),
        output = output)
