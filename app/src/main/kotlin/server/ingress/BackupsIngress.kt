package server.ingress

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.contentType
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import server.injection.with
import server.models.idToName
import server.services.BackupService

fun Routing.installBackupIngress() {
  route("v1/backups") {
    contentType(ContentType.Application.Json) {

      // List
      get { with<BackupService> { call.respond(list()) } }

      get("/{backup_id}") {
        with<BackupService> {
          val id = call.parameters["backup_id"]
          if (id.isNullOrBlank()) {
            call.respondInvalidBackupId()
          } else {
            when (get(id.idToName())) {
              null -> call.response.status(HttpStatusCode.NotFound)
              else -> call.respond(it)
            }
          }
        }
      }
    }

    delete("/{backup_id}") {
      with<BackupService> {
        val id = call.parameters["backup_id"]
        if (id.isNullOrBlank()) {
          call.respondInvalidBackupId()
        } else {
          delete(id.idToName())
          call.response.status(HttpStatusCode.OK)
        }
      }
    }
  }
}

private suspend fun ApplicationCall.respondInvalidBackupId() {
  respond(HttpStatusCode.BadRequest, "Invalid path parameter backup_id")
}
