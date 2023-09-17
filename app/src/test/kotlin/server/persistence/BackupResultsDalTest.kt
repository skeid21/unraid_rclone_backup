package server.persistence

import com.google.common.truth.Truth.assertThat
import kotlin.random.Random
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

  private val backupId: Int
  private val backupName: BackupName

  init {
    val backupsDal = harness.getInstance<BackupsDal>()
    val backup = backupsDal.create(BackupStub.get())
    backupId = backup.primaryId
    backupName = backup.entity.name
  }

  @Test
  fun canPersist() {
    val expected = BackupResultStub.get(backupName)
    subject.create(expected, backupId).let { assertThat(it.entity).isEqualTo(expected) }

    subject.get(expected.name).let { assertThat(it?.entity).isEqualTo(expected) }

    subject.delete(expected.name)
    subject.get(expected.name).let { assertThat(it).isNull() }
  }

  @Test
  fun canList() {
    val expected =
        generateSequence { subject.create(BackupResultStub.get(backupName), backupId) }
            .take(5)
            .map { it.entity }
            .toList()

    subject.list(backupId).let { res -> assertThat(res.map { it.entity }).isEqualTo(expected) }
  }

  @Test
  fun create_invalidBackupForeignKey_throws() {
    assertFailsWith<ExposedSQLException> {
      val invalid: Int = Random.next()
      subject.create(BackupResultStub.get(backupName), invalid)
    }
  }

  @Test
  fun deletingBackup_cascadesDelete() {
    val expected =
        generateSequence { subject.create(BackupResultStub.get(backupName), backupId) }
            .take(5)
            .map { it.entity }
            .toList()

    subject.list(backupId).let { res -> assertThat(res.map { it.entity }).isEqualTo(expected) }

    harness.getInstance<BackupsDal>().delete(backupName)

    subject.list(backupId).let { assertThat(it).isEmpty() }
  }
}
