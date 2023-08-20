package server.pages.views

import kotlinx.html.ARTICLE
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.br
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.header
import kotlinx.html.i
import kotlinx.html.li
import kotlinx.html.onClick
import kotlinx.html.p
import kotlinx.html.ul
import server.injection.getInstance
import server.models.Backup
import server.services.BackupService

private fun UL.backupListItem(backup: Backup) {
  li(classes = "collection-item") {
    div(classes = "card small") {
      div(classes = "card-content") {
        div(classes = "card-title") {
          +backup.displayName
        }
        p { +"Last run: Today" }
        p { +"Created: Jun 12th, 2023" }
      }
      div(classes = "card-action") {
          a(classes = "btn waves-effect indigo lighten-2") {
            href = backup.name
            i(classes = "material-icons right") { +"edit" }
            +"Edit"
          }

          button(classes = "btn waves-effect indigo lighten-2") {
            onClick="""deleteResource('${backup.name}')"""
            i(classes = "material-icons right") { +"delete" }
            +"Delete"
          }
      }
    }
  }
}

fun ARTICLE.backupListView() {
  val backups = getInstance<BackupService>().list()
    a(classes = "right btn-floating btn waves-effect waves-light indigo darken-2") {
      href = "backups//new"
      i(classes = "material-icons") {
        +"add"
      }
    }
  br {  }
  ul(classes = "collection") {
    backups.forEach { backupListItem(it) }
  }
}
