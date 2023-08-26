package server.pages.views

import kotlinx.html.ARTICLE
import kotlinx.html.p

fun ARTICLE.backupEditView(createNew: Boolean) {
  if (createNew) {
    p { +"this is the edit view for a new item" }
  } else {
    p { +"this is the edit view for an existing item" }
  }
}
