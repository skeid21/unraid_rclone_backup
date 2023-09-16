package server.services

import com.google.common.truth.Truth.assertThat
import io.ktor.server.plugins.BadRequestException
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import server.TestHarness
import server.TestHarnessExtension
import server.models.Backup
import server.models.BackupStub
import server.models.toCompare
import server.next

@ExtendWith(TestHarnessExtension::class)
class BackupServiceTest(harness: TestHarness) {
  private val subject = harness.getInstance<BackupService>()

  @Test
  fun canManageResource() {
    val backup = BackupStub.get()
    assertThat(subject.get(backup.name)).isNull()
    assertThat(subject.list()).isEmpty()

    subject.create(backup)

    assertThat(subject.get(backup.name)?.toCompare()).isEqualTo(backup.toCompare())
    assertThat(subject.list().toCompare()).containsExactly(backup.toCompare())

    subject.delete(backup.name)

    assertThat(subject.get(backup.name)).isNull()
    assertThat(subject.list()).isEmpty()
  }

  @ParameterizedTest
  @MethodSource("badInputs")
  fun create_badInputs_throwsBadRequestException(backup: Backup, errorMessage: String) {
    assertFailsWith<BadRequestException>(errorMessage) { subject.create(backup) }
  }

  @ParameterizedTest
  @MethodSource("badInputs")
  fun update_badInputs_throwsBadRequestException(backup: Backup, errorMessage: String) {
    val name = subject.create(BackupStub.get()).name
    assertThat(subject.get(name)).isNotNull()

    assertFailsWith<BadRequestException>(errorMessage) { subject.update(backup.copy(name = name)) }
  }

  companion object {
    @JvmStatic
    fun badInputs(): Stream<Arguments> =
        Stream.of(
            Arguments.of(
                // empty cron
                BackupStub.get().copy(cronSchedule = ""),
                BackupService.INVALID_CRON_SCHEDULE_MESSAGE),
            Arguments.of(
                // Invalid cron
                BackupStub.get().copy(cronSchedule = "Jkk21"),
                BackupService.INVALID_CRON_SCHEDULE_MESSAGE),
            Arguments.of(
            // Invalid cron - not enough specificaitons 5 required
            BackupStub.get().copy(cronSchedule = "0 * * *"),
            BackupService.INVALID_CRON_SCHEDULE_MESSAGE),
          Arguments.of(
            // Invalid cron - too many specifications
            BackupStub.get().copy(cronSchedule = "0 * * * * ?"),
            BackupService.INVALID_CRON_SCHEDULE_MESSAGE),
            Arguments.of(
                // empty sourceDir
                BackupStub.get().copy(sourceDir = ""),
                BackupService.SOURCE_DIR_CANNOT_BE_EMPTY),
            Arguments.of(
                // source dir doesn't exist
                BackupStub.get().copy(sourceDir = Random.next()),
                BackupService.SOURCE_DIR_DOES_NOT_EXIST),
            Arguments.of(
                // destination dir cannot be empty
                BackupStub.get().copy(destinationDir = ""),
                BackupService.DESTINATION_DIR_CANNOT_BE_EMPTY))
  }
}
