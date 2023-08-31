package server.pages.views

import kotlinx.html.ARTICLE
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.style
import kotlinx.html.ul
import server.injection.getInstance
import server.models.Backup
import server.pages.BACKUP_DELETE
import server.pages.BACKUP_EDIT
import server.pages.BACKUP_NEW
import server.pages.withBackupId
import server.services.BackupService

private fun UL.backupListItem(backup: Backup) {
  li(classes = "collection-item") {
    div(classes = "card small") {
      a {
        style = "text-decoration:none; color:inherit"
        href = BACKUP_EDIT.withBackupId(backup.name)
        div(classes = "card-content") {
          div(classes = "card-title") { +backup.displayName }
          p { +"Last run: Today" }
          p { +"Created: Jun 12th, 2023" }
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
    style = "position: fixed; right:10px; bottom:10px"
    href = BACKUP_NEW
    i(classes = "material-icons") { +"add" }
  }

  ul(classes = "collection") { backups.forEach { backupListItem(it) } }
}
