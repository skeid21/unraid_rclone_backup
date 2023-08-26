package server.pages.templates

import kotlinx.html.FORM
import kotlinx.html.InputType
import kotlinx.html.input
import server.models.Backup

fun FORM.backupForm(backup: Backup?) {
  input(type = InputType.text) { value = backup?.displayName ?: "" }
}
