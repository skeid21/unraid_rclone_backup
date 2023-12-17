package server.services

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class ProcessRunnerTest {
  private val subject = ProcessRunner()

  @Test
  fun canRunProcess() = runTest {
    val pb = ProcessBuilder("sh", "-c", "echo hello")

    subject.runProcess(pb).let { res ->
      assertThat(res.exitVal).isEqualTo(0)
      assertThat(res.output).isEqualTo("hello\n")
    }
  }

  @Test
  fun canCancelProcess() = runTest {
    val pb = ProcessBuilder("sh", "-c", "while true; do echo hello; done")

    val deferredResult = async { subject.runProcess(pb) }

    subject.shouldCancelAndExitProcess.set(true)

    deferredResult.await().let { res -> assertThat(res.exitVal).isNotEqualTo(0) }
  }
}
