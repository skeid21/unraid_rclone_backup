package server.pages.views

import kotlinx.html.ARTICLE
import server.getInstance
import server.models.BackupName
import server.pages.BACKUP_EDIT
import server.pages.templates.backupForm
import server.pages.withBackupId
import server.services.BackupService

fun ARTICLE.backupEditView(backupName: BackupName) {
  val backup = getInstance<BackupService>().get(backupName)
  backupForm(backup, BACKUP_EDIT.withBackupId(backupName))
}
