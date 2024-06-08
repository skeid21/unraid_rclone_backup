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
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import server.models.Backup
import server.models.BackupName
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
const val BACKUP_NEW = "$BACKUPS:new"
const val BACKUP = "$BACKUPS/{backup_id}"
const val BACKUP_EDIT = "$BACKUPS/{backup_id}:edit"
const val BACKUP_DELETE = "$BACKUPS/{backup_id}:delete"
const val BACKUP_RUN = "$BACKUPS/{backup_id}:run"
const val BACKUP_STOP = "$BACKUPS/{backup_id}:stop"

fun Routing.installIndexPageIngress() {
  // index
  get("/") { call.respondRedirect(permanent = false) { this.path("/backups") } }

  //
  // -- Settings --
  //
  get(SETTINGS) { call.respondHtml { root { settingsView() } } }

  //
  // -- Backups ---
  //

  //
  // List
  //
  get(BACKUPS) { call.respondHtml { root { backupListView() } } }

  //
  // Get
  //
  get(BACKUP) {
    call.respondHtml { root { backupDetailView(call.parameters["backup_id"]!!.idToName()) } }
  }

  //
  // Edit
  //
  get(BACKUP_EDIT) {
    call.respondHtml { root { backupEditView(call.parameters["backup_id"]!!.idToName()) } }
  }
  post(BACKUP_EDIT) {
    mutation {
      val backupName = call.parameters["backup_id"]!!.idToName()
      val currentBackup = get(backupName)
      val backup = call.receiveParameters().toBackup(currentBackup)

      update(backup)
    }
    call.redirectToHome()
  }

  //
  // New
  //
  get(BACKUP_NEW) { call.respondHtml { root { backupNewView() } } }

  post(BACKUP_NEW) {
    mutation { create(call.receiveParameters().toBackup(null)) }

    call.redirectToHome()
  }

  //
  // Delete
  //
  get(BACKUP_DELETE) {
    mutation { delete(call.parameters["backup_id"]!!.idToName()) }
    call.redirectToHome()
  }

  //
  // Run
  //
  get(BACKUP_RUN) {
    withInstance<BackupExecutorService> {
      this.runBackupJobNow(call.parameters["backup_id"]!!.idToName())
    }
    call.redirectToHome()
  }

  //
  // Stop
  //
  get(BACKUP_STOP) {
    withInstance<BackupExecutorService> {
      val backupName = call.parameters["backup_id"]!!.idToName()
      interruptBackupJob(backupName)
      while (listExecutingJobs().contains(backupName)) {
        delay(500)
      }
    }
    call.redirectToHome()
  }
}

fun Parameters.toBackup(backup: Backup?): Backup {

  val displayName = this["displayName"].toString()
  val name = backup?.name ?: displayName.idToName()

  return Backup(
      name = name,
      // create time is ignored on update and create
      createTime = Clock.System.now(),
      displayName = displayName,
      cronSchedule = this["cronSchedule"].toString(),
      schedulePaused = this["schedulePaused"] != null,
      sourceDir = this["sourceDir"].toString(),
      destinationDir = this["destinationDir"].toString(),
      config = this["config"].toString())
}

inline fun mutation(block: BackupService.() -> Unit) {
  withInstance<BackupService>(block).also {
    withInstance<BackupExecutorService> { ensureBackupJobsExist() }
  }
}

suspend fun ApplicationCall.redirectToHome() {
  respondRedirect(BACKUPS, false)
}

fun String.withBackupId(backupName: BackupName): String = replace("{backup_id}", backupName.id)
