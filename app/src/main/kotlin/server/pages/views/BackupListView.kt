package server.pages.views

import kotlinx.html.ARTICLE
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.li
import kotlinx.html.onClick
import kotlinx.html.p
import kotlinx.html.style
import kotlinx.html.ul
import server.injection.getInstance
import server.models.Backup
import server.services.BackupService

private fun UL.backupListItem(backup: Backup) {
  li(classes = "collection-item") {
    div(classes = "card small") {
      a {
        style = "text-decoration:none; color:inherit"
        href = "/${backup.name.value}"
        div(classes = "card-content") {
          div(classes = "card-title") { +backup.displayName }
          p { +"Last run: Today" }
          p { +"Created: Jun 12th, 2023" }
        }
      }
      div(classes = "card-action") {
        button(classes = "right btn waves-effect indigo lighten-2") {
          style = "margin:10px"
          onClick = """deleteResource('${backup.name.value}')"""
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
    href = "backups//new"
    i(classes = "material-icons") { +"add" }
  }

  ul(classes = "collection") { backups.forEach { backupListItem(it) } }
}
