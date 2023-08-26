package server.pages.views

import kotlinx.html.ARTICLE
import kotlinx.html.form
import kotlinx.html.p
import server.injection.getInstance
import server.models.BackupName
import server.pages.templates.backupForm
import server.services.BackupService

fun ARTICLE.backupEditView(backupName: BackupName) {
  val backup = getInstance<BackupService>().get(backupName)
  p { +"This is the edit view " }
  form { backupForm(backup) }
}
