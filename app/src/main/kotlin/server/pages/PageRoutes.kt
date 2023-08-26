package server.pages

import io.ktor.http.path
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import server.models.idToName
import server.pages.templates.root
import server.pages.views.backupDetailView
import server.pages.views.backupEditView
import server.pages.views.backupListView
import server.pages.views.backupNewView
import server.pages.views.settingsView

fun Routing.installIndexPageIngress() {
  // index.html
  get("/") { this.call.respondRedirect(permanent = false) { this.path("/backups") } }

  get("/backups") { this.call.respondHtml { root { backupListView() } } }
  get("/settings") { this.call.respondHtml { root { settingsView() } } }

  get("/backups/{backup_id}") {
    call.respondHtml { root { backupDetailView(call.parameters["backup_id"]!!.idToName()) } }
  }
  get("/backups/{backup_id}/edit") {
    call.respondHtml { root { backupEditView(call.parameters["backup_id"]!!.idToName()) } }
  }
  get("/backups//new") { call.respondHtml { root { backupNewView() } } }
}
