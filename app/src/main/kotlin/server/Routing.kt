package server

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.Routing
import server.pages.installIndexPageIngress

fun Application.installRoutes() {
  install(Routing) { installIndexPageIngress() }
}
