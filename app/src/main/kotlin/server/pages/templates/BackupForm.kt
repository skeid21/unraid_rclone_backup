package server.pages.templates

import kotlinx.html.ARTICLE
import kotlinx.html.FormEncType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.style
import kotlinx.html.textArea
import server.models.Backup

fun ARTICLE.backupForm(backup: Backup?, formActionUrl: String) {
  form(
      action = formActionUrl,
      method = FormMethod.post,
      encType = FormEncType.applicationXWwwFormUrlEncoded) {
        style = "margin:15px;"

        // only display for an edit view of a backup that already exists
        if (backup != null) {
          div(classes = "input-field") {
            input(type = InputType.text) {
              id = "name"
              name = id
              disabled = true
              value = backup.name.value
            }
            label {
              htmlFor = "name"
              +"Resource Name"
            }
          }
        }
        div(classes = "input-field") {
          input(type = InputType.text) {
            id = "displayName"
            name = id
            value = backup?.displayName ?: ""
          }
          label {
            htmlFor = "displayName"
            +"Display Name"
          }
        }
        div(classes = "input-field") {
          input(type = InputType.text) {
            id = "cronSchedule"
            name = id
            value = backup?.cronSchedule ?: ""
          }
          label {
            htmlFor = "cronSchedule"
            +"Cron Schedule"
          }
        }
        div(classes = "input-field") {
          input(type = InputType.text) {
            id = "sourceDir"
            name = id
            value = backup?.cronSchedule ?: ""
          }
          label {
            htmlFor = "sourceDir"
            +"Source Dir"
          }
        }

        div(classes = "input-field") {
          input(type = InputType.text) {
            id = "destinationDir"
            name = id
            value = backup?.cronSchedule ?: ""
          }
          label {
            htmlFor = "destinationDir"
            +"Destination Dir"
          }
        }
        val testAreaContent = backup?.config ?: ""
        div(classes = "input-field") {
          textArea(classes = "materialize-textarea") {
            id = "config"
            name = id
            +testAreaContent
          }
          label {
            htmlFor = "config"
            +"Config"
          }
        }
        input(classes = "right btn waves-effect indigo lighten-2", type = InputType.submit) {
          value = "Submit"
        }
      }
}
