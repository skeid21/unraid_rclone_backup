package server.persistence

import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
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
    backupName = backup.entity.name
  }

  @Test
  fun canPersist() {
    val expected = BackupResultStub.get(backupName)
    subject.create(expected).let { assertThat(it.entity).isEqualTo(expected) }

    subject.get(expected.name).let { assertThat(it?.entity).isEqualTo(expected) }

    subject.delete(expected.name)
    subject.get(expected.name).let { assertThat(it).isNull() }
  }

  @Test
  fun canList() {
    val expected =
        generateSequence { subject.create(BackupResultStub.get(backupName)) }
            .take(5)
            .map { it.entity }
            .toList()

    subject.list(backupName).let { res -> assertThat(res.map { it.entity }).isEqualTo(expected) }
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
        generateSequence { subject.create(BackupResultStub.get(backupName)) }
            .take(5)
            .map { it.entity }
            .toList()

    subject.list(backupName).let { res -> assertThat(res.map { it.entity }).isEqualTo(expected) }

    harness.getInstance<BackupsDal>().delete(backupName)

    subject.list(backupName).let { assertThat(it).isEmpty() }
  }
}
