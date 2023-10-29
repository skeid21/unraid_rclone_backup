package server.pages.templates

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.html.ARTICLE
import kotlinx.html.UL
import kotlinx.html.contentEditable
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.label
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.textArea
import kotlinx.html.ul
import server.getInstance
import server.models.BackupName
import server.models.BackupResult
import server.services.BackupResultService

private fun UL.backupResultsListItem(backupResult: BackupResult) {
  li(classes = "collection-item") {
    div(classes = "card small") {
      div(classes = "card-content") {
        div(classes = "card-title") { +"${backupResult.name.value}" }
        p {
          +"Start Time: ${backupResult.startTime.toLocalDateTime(TimeZone.currentSystemDefault())}"
        }
        p { +"End Time: ${backupResult.endTime.toLocalDateTime(TimeZone.currentSystemDefault())}" }
        p { +"Status: ${backupResult.status}" }
        if (backupResult.output.isNotBlank()) {
          label {
            htmlFor = "ouput"
            +"Output"
          }
          textArea(classes = "materialize-textarea") {
            id = "output"
            contentEditable = false
            text(backupResult.output)
          }
        }
      }
    }
  }
}

fun ARTICLE.backupResultsList(backupName: BackupName) {

  val backupResults =
      getInstance<BackupResultService>().list(backupName).sortedByDescending { it.endTime }
  ul(classes = "collection") { backupResults.forEach { backupResultsListItem(it) } }
}
