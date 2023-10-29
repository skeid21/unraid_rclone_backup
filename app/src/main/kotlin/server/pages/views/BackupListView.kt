package server.pages.views

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.html.ARTICLE
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.div
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
import server.pages.withBackupId
import server.services.BackupResultService
import server.services.BackupService

private fun UL.backupListItem(backup: Backup, backupResult: BackupResult?) {
  li(classes = "collection-item") {
    div(classes = "card small") {
      a {
        style = "text-decoration:none; color:inherit"
        href = BACKUP_DETAIL.withBackupId(backup.name)
        div(classes = "card-content") {
          div(classes = "card-title") { +backup.displayName }

          val lastRun =
              if (backupResult != null) {
                backupResult.endTime.toLocalDateTime(TimeZone.currentSystemDefault())
              } else {
                "Never"
              }
          p { +"Last Run: $lastRun " }
          p { +"Last Run Result: ${backupResult?.status ?: ""}" }
          p { +"Created: ${backup.createTime.toLocalDateTime(TimeZone.currentSystemDefault())}" }
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
  ul(classes = "collection") {
    backups.forEach { backupListItem(it, backupResultService.getMostRecentResult(it.name)) }
  }
}
