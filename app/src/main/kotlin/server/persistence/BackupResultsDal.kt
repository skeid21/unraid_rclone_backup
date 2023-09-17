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
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import server.models.BackupResult
import server.models.BackupResultName
import server.models.asBackupResultName
import server.persistence.Backups.uniqueIndex

/** Data definition for the BackupResults table */
object BackupResults : IntIdTable() {
  val name: Column<String> = varchar("name", 256).uniqueIndex()
  val backupId: Column<Int> = integer("backup_id")
  val startTime: Column<Instant> = timestamp("start_time")
  val endTime: Column<Instant> = timestamp("end_time")
  val result: Column<String> = varchar("result", 50)
  val output: Column<String> = text("output")

  init {
    foreignKey(
        backupId to Backups.id,
        onUpdate = ReferenceOption.RESTRICT,
        onDelete = ReferenceOption.CASCADE)
  }
}

/** DAO for interacting with the [BackupResults] table * */
class DAOBackupResult(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<DAOBackupResult>(BackupResults)

  var name by BackupResults.name
  var backupId by BackupResults.backupId
  var startTime by BackupResults.startTime
  var endTime by BackupResults.endTime
  var result by BackupResults.result
  var output by BackupResults.output
}

typealias BackupResultRef = EntityRef<BackupResult>

class BackupResultsDal @Inject constructor(private val db: Database) {
  fun create(backupResult: BackupResult, backupId: Int) =
      transaction(db) {
            DAOBackupResult.new {
              name = backupResult.name.value
              this.backupId = backupId
              startTime = backupResult.startTime
              endTime = backupResult.endTime
              result = backupResult.result.name
              output = backupResult.output
            }
          }
          .toRef()

  fun get(name: BackupResultName): BackupResultRef? = transaction(db) { getByName(name) }?.toRef()

  fun list(backupId: Int): List<BackupResultRef> =
      transaction(db) {
        DAOBackupResult.find { BackupResults.backupId eq backupId }.map { it.toRef() }
      }

  fun delete(name: BackupResultName) = transaction(db) { getByName(name)?.delete() }

  private fun getByName(name: BackupResultName): DAOBackupResult? =
      DAOBackupResult.find { BackupResults.name eq name.value }.firstOrNull()
}

fun DAOBackupResult.toRef() =
    BackupResultRef(
        BackupResult(
            name = name.asBackupResultName(),
            startTime = startTime,
            endTime = endTime,
            result = BackupResult.Result.valueOf(result),
            output = output),
        primaryId = id.value)
