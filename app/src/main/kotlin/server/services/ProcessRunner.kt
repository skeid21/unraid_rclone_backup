package server.services

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class ProcessRunner() {
  data class ProcessResult(val output: String, val exitVal: Int, val process: Process)

  val shouldCancelAndExitProcess = AtomicBoolean(false)

  private fun getOutput(inputStream: InputStream): String {
    val w = StringWriter()
    val buffer = CharArray(1024)
    try {
      val reader = InputStreamReader(inputStream)
      var chars = reader.read(buffer)
      while (chars >= 0) {
        w.write(buffer, 0, chars)
        chars = reader.read(buffer)
      }
    } catch (e: IOException) {
      // noop
    }

    return w.toString()
  }

  suspend fun runProcess(pb: ProcessBuilder): ProcessResult = coroutineScope {
    val p = pb.redirectErrorStream(true).start()

    p.inputStream.use { stream ->
      val deferredOutput = async(Dispatchers.IO) { getOutput(stream) }
      var hasBeenDestroyed = false
      while (p.isAlive) {
        if (shouldCancelAndExitProcess.get() && !hasBeenDestroyed) {
          hasBeenDestroyed = true
          p.destroy()
        }
        delay(500)
      }

      return@coroutineScope ProcessResult(
          output = deferredOutput.await(), exitVal = p.exitValue(), process = p)
    }
  }
}
