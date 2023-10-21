package server.pages

import io.ktor.http.Parameters
import io.ktor.http.path
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import server.models.Backup
import server.models.BackupName
import server.models.BackupResultStatus
import server.models.idToName
import server.pages.templates.root
import server.pages.views.backupDetailView
import server.pages.views.backupEditView
import server.pages.views.backupListView
import server.pages.views.backupNewView
import server.pages.views.settingsView
import server.services.BackupExecutorService
import server.services.BackupService
import server.withInstance

const val BACKUPS = "/backups"
const val SETTINGS = "/settings"
const val BACKUP_DETAIL = "$BACKUPS/{backup_id}"
const val BACKUP_DRYRUN = "$BACKUPS/{backup_id}/dryrun"
const val BACKUP_EDIT = "$BACKUPS/{backup_id}/edit"
const val BACKUP_DELETE = "$BACKUPS/{backup_id}/delete"
const val BACKUP_NEW = "$BACKUPS//new"

fun String.withBackupId(backupName: BackupName): String = replace("{backup_id}", backupName.id)

fun Routing.installIndexPageIngress() {
  // index
  get("/") { this.call.respondRedirect(permanent = false) { this.path("/backups") } }

  //
  // -- Settings --
  //
  get(SETTINGS) { this.call.respondHtml { root { settingsView() } } }

  //
  // -- Backups ---
  //

  //
  // List
  //
  get(BACKUPS) { this.call.respondHtml { root { backupListView() } } }

  get(BACKUP_DETAIL) {
    call.respondHtml { root { backupDetailView(call.parameters["backup_id"]!!.idToName()) } }
  }

  //
  // Edit
  //
  get(BACKUP_EDIT) {
    call.respondHtml { root { backupEditView(call.parameters["backup_id"]!!.idToName()) } }
  }
  post(BACKUP_EDIT) {
    withInstance<BackupService> {
      val backupName = call.parameters["backup_id"]!!.idToName()
      val currentBackup = get(backupName)
      val backup = call.receiveParameters().toBackup(currentBackup)

      update(backup)

      call.redirectToHome()
    }
  }

  //
  // New
  //
  get(BACKUP_NEW) { call.respondHtml { root { backupNewView() } } }

  post(BACKUP_NEW) {
    withInstance<BackupService> {
      val backup = call.receiveParameters().toBackup(null)
      create(backup)
    }

    withInstance<BackupExecutorService> { ensureBackupJobsExist() }
    call.redirectToHome()
  }

  //
  // Delete
  //
  get(BACKUP_DELETE) {
    withInstance<BackupService> { delete(call.parameters["backup_id"]!!.idToName()) }
  }
}

fun Parameters.toBackup(backup: Backup?): Backup {

  val displayName = this["displayName"].toString()
  val name = backup?.name ?: displayName.idToName()

  return Backup(
      name = name,
      // create time is ignored on update and create
      createTime = Clock.System.now(),
      // lastSuccessfulRunTime is ignored on update and create from user
      lastRunTime = backup?.lastRunTime,
      // lastRunResult  is ignored on update and create from user
      lastRunResult = backup?.lastRunResult ?: BackupResultStatus.Unknown,
      displayName = displayName,
      cronSchedule = this["cronSchedule"].toString(),
      sourceDir = this["sourceDir"].toString(),
      destinationDir = this["destinationDir"].toString(),
      config = this["config"].toString())
}

suspend fun ApplicationCall.redirectToHome() {
  respondRedirect(BACKUPS, false)
}
