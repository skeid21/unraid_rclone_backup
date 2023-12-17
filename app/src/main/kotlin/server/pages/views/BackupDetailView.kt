package server.pages.views

import kotlinx.html.ARTICLE
import kotlinx.html.a
import kotlinx.html.h3
import kotlinx.html.hr
import kotlinx.html.i
import kotlinx.html.style
import server.getInstance
import server.models.BackupName
import server.pages.BACKUP_EDIT
import server.pages.templates.backupForm
import server.pages.templates.backupResultsList
import server.pages.withBackupId
import server.services.BackupService

fun ARTICLE.backupDetailView(backupName: BackupName) {
  val backup = getInstance<BackupService>().get(backupName)

  backupForm(backup, null)
  h3 { +"Backup Results" }
  hr(classes = "indigo lighten-2") {
    style = "background-color: indigo; height: 4px; border: none;"
  }
  backupResultsList(backupName)

  a(classes = "btn-floating btn waves-effect waves-light indigo darken-2 ") {
    style = "position: fixed; right:20px; bottom:20px"
    href = BACKUP_EDIT.withBackupId(backupName)
    i(classes = "material-icons") { +"edit" }
  }
}
