package server.pages.templates

import kotlinx.html.ARTICLE
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.article
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.nav
import kotlinx.html.script
import kotlinx.html.title
import kotlinx.html.ul

fun HTML.root(block: ARTICLE.() -> Unit) {
  head {
    title { +"UnClone" }
    link {
     href = "https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css"
      rel = "stylesheet"
    }
    link {
      href = "https://fonts.googleapis.com/icon?family=Material+Icons"
      rel = "stylesheet"
    }
    script {
      src = "https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"
    }
    script {
     +"""
       function deleteResource(resourceName) {
          fetch(`v1/${'$'}{resourceName}`, {method: 'DELETE'});
          location.reload();
       }
     """.trimIndent()
    }
  }
  body {
    nav {
      div(classes="nav-wrapper indigo") {
         a(classes="brand-logo") {
           href="#"
           +"UnClone" }
        ul(classes = "right hide-on-med-and-down") {
          id = "nav-mobile"
          li { a { href="/backups"
            +"Backups"
          } }
          li { a { href="/settings"
              +"Settings"
          } }
        }
      }
    }
    article(block = block)
  }
}
