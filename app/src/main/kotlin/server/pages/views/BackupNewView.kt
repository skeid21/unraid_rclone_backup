package server.pages.views

import kotlinx.html.ARTICLE
import server.pages.BACKUP_NEW
import server.pages.templates.backupForm

fun ARTICLE.backupNewView() {
  backupForm(null, BACKUP_NEW)
}
