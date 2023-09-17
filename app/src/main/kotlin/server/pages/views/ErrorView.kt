package server.pages.views

import kotlinx.html.ARTICLE
import kotlinx.html.div
import kotlinx.html.hr
import kotlinx.html.p

fun ARTICLE.errorView(exception: Throwable) {
  p { +"An Error occurred while performing an operation." }
  p { +"If this error was caused by invalid input, try browsing back and fixing those inputs." }
  hr()
  p { +"Error Type: ${exception.javaClass.name}" }
  hr()
  p { +"Error Message: ${exception.message}" }
  hr()
  p { +"Stack Trace: " }
  div()
  p { +exception.stackTraceToString() }
}
