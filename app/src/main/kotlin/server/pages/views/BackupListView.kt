package server.pages.views

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.html.ARTICLE
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.hr
import kotlinx.html.i
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.style
import kotlinx.html.ul
import server.getInstance
import server.models.Backup
import server.models.BackupResult
import server.pages.BACKUP_DELETE
import server.pages.BACKUP_DETAIL
import server.pages.BACKUP_NEW
import server.pages.BACKUP_RUN
import server.pages.withBackupId
import server.services.BackupExecutorService
import server.services.BackupResultService
import server.services.BackupService

private fun UL.backupListItem(backup: Backup, backupResult: BackupResult?, isRunning: Boolean) {
  li(classes = "collection-item") {
    div(classes = "card small") {
      a {
        style = "text-decoration:none; color:inherit"
        href = BACKUP_DETAIL.withBackupId(backup.name)
        div(classes = "card-content") {
          div(classes = "card-title") { +backup.displayName }

          val lastRun =
              backupResult?.endTime?.toLocalDateTime(TimeZone.currentSystemDefault()) ?: "Never"

          p { +"Last Run: $lastRun " }
          if (backupResult != null) {
            p { +"Last Run Result: ${backupResult.status}" }
          }
          p { +"Created: ${backup.createTime.toLocalDateTime(TimeZone.currentSystemDefault())}" }
          hr {}
          val status = if (backup.schedulePaused) "Schedule Paused" else "Scheduled"
          p { +"Status: $status" }
        }
      }
      div(classes = "card-action") {
        a(classes = "right btn waves-effect indigo lighten-2") {
          href = BACKUP_DELETE.withBackupId(backup.name)
          style = "margin:10px"
          i(classes = "material-icons right") { +"delete" }
          +"Delete"
        }
      }
      if (isRunning) {
        div(classes = "card-action") {
          div(classes = "preloader-wrapper small active") {
            div(classes = "spinner-layer spinner-indigo-only") {
              div(classes = "circle-clipper left") { div(classes = "circle") }
              div(classes = "gap-patch") { div(classes = "circle") }
              div(classes = "circle-clipper right") { div(classes = "circle") }
            }
          }
          +"Running..."
        }
      } else {
        div(classes = "card-action") {
          val icon = "play_arrow"
          val colorTent = if (isRunning) "darken-2" else "lighten-2"
          a(classes = "left btn-floating waves-effect indigo $colorTent") {
            href = BACKUP_RUN.withBackupId(backup.name)
            i(classes = "material-icons") { +icon }
          }
        }
      }
    }
  }
}

fun ARTICLE.backupListView() {
  val backups = getInstance<BackupService>().list()
  a(classes = "btn-floating btn waves-effect waves-light indigo darken-2 ") {
    style = "position: fixed; right:20px; bottom:20px"
    href = BACKUP_NEW
    i(classes = "material-icons") { +"add" }
  }

  val backupResultService = getInstance<BackupResultService>()
  val runningJobs = getInstance<BackupExecutorService>().listExecutingJobs()
  ul(classes = "collection") {
    backups.forEach {
      backupListItem(
          it, backupResultService.getMostRecentResult(it.name), runningJobs.contains(it.name))
    }
  }
}
