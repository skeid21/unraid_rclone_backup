package server.persistence

import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import kotlinx.datetime.Instant
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import server.TestHarness
import server.TestHarnessExtension
import server.models.BackupName
import server.models.BackupResultStub
import server.models.BackupStub
import server.next

@ExtendWith(TestHarnessExtension::class)
class BackupResultsDalTest(private val harness: TestHarness) {

  private val subject = harness.getInstance<BackupResultsDal>()

  private val backupName: BackupName

  init {
    val backupsDal = harness.getInstance<BackupsDal>()
    val backup = backupsDal.create(BackupStub.get())
    backupName = backup.name
  }

  @Test
  fun canPersist() {
    val expected = BackupResultStub.get(backupName)
    subject.create(expected).let { assertThat(it).isEqualTo(expected) }

    subject.get(expected.name).let { assertThat(it).isEqualTo(expected) }

    subject.delete(expected.name)
    subject.get(expected.name).let { assertThat(it).isNull() }
  }

  @Test
  fun canList() {
    var offsetFromEpoch: Long = 0
    val generated =
        generateSequence {
              val backupResult =
                  BackupResultStub.get(backupName)
                      .copy(
                          startTime = Instant.fromEpochSeconds(offsetFromEpoch++),
                          endTime = Instant.fromEpochSeconds(offsetFromEpoch++))
              subject.create(backupResult)
            }
            .take(5)
            .toList()

    val expected = generated.last()

    subject.getMostRecentResult(backupName).let { res -> assertThat(res).isEqualTo(expected) }
  }

  @Test
  fun canGetMostRecentResult() {
    val expected =
        generateSequence { subject.create(BackupResultStub.get(backupName)) }.take(5).toList()
  }

  @Test
  fun create_invalidBackupForeignKey_throws() {
    assertFailsWith<ExposedSQLException> {
      val backupNotFound = BackupName("backups/notFound")
      subject.create(BackupResultStub.get(backupNotFound))
    }
  }

  @Test
  fun deletingBackup_cascadesDelete() {
    val expected =
        generateSequence { subject.create(BackupResultStub.get(backupName)) }.take(5).toList()

    subject.list(backupName).let { res -> assertThat(res).isEqualTo(expected) }

    harness.getInstance<BackupsDal>().delete(backupName)

    subject.list(backupName).let { assertThat(it).isEmpty() }
  }
}
