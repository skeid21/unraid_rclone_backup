package server.pages

import io.ktor.http.path
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import server.pages.templates.root
import server.pages.views.backupDetailView
import server.pages.views.backupEditView
import server.pages.views.backupListView

fun Routing.installIndexPageIngress() {
  // index.html
  get("/") { this.call.respondRedirect(permanent = false) { this.path("/backups") } }

  get("/backups") { this.call.respondHtml { root { backupListView() } } }

  get("/backups/{backup_id}") { call.respondHtml { root { backupDetailView() } } }
  get("/backups/{backup_id}/edit") { call.respondHtml { root { backupEditView(false) } } }
  get("/backups//new") { call.respondHtml { root { backupEditView(true) } } }
}
