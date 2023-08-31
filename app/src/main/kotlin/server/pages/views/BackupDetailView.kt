package server.pages.views

import kotlinx.html.ARTICLE
import kotlinx.html.a
import kotlinx.html.i
import kotlinx.html.p
import kotlinx.html.style
import server.injection.getInstance
import server.models.BackupName
import server.pages.BACKUP_EDIT
import server.pages.withBackupId
import server.services.BackupService

fun ARTICLE.backupDetailView(backupName: BackupName) {
  val backup = getInstance<BackupService>().get(backupName)
  p { +"this is the detail view" }

  a(classes = "btn-floating btn waves-effect waves-light indigo darken-2 ") {
    style = "position: fixed; right:10px; bottom:10px"
    href = BACKUP_EDIT.withBackupId(backupName)
    i(classes = "material-icons") { +"edit" }
  }
}
