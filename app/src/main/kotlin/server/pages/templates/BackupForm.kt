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

fun ARTICLE.backupForm(backup: Backup?, formActionUrl: String?) {
  val disableEditing = formActionUrl == null
  form(
      action = formActionUrl ?: "",
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
            disabled = disableEditing
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
            disabled = disableEditing
            value = backup?.cronSchedule ?: ""
            placeholder = "0 0 2 ? * * (Quartz CRON, Every day at 2AM)"
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
            disabled = disableEditing
            value = backup?.sourceDir ?: ""
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
            disabled = disableEditing
            value = backup?.destinationDir ?: ""
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
            disabled = disableEditing
            +testAreaContent
          }
          label {
            htmlFor = "config"
            +"Config"
          }
        }
        if (!disableEditing) {
          input(classes = "right btn waves-effect indigo lighten-2", type = InputType.submit) {
            value = "Submit"
          }
        }
      }
}
