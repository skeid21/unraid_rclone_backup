package server.persistence

import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import server.TestHarness
import server.TestHarnessExtension
import server.models.BackupStub

@ExtendWith(TestHarnessExtension::class)
class BackupsDalTest(harness: TestHarness) {

  private val subject = harness.getInstance<BackupsDAL>()

  @Test
  fun canList() {
    val created = generateSequence { subject.create(BackupStub.get()) }.take(5).toList()

    val res = subject.list()

    assertThat(res).containsExactlyElementsIn(created)
  }

  @Test
  fun canPersist() {
    val expected = BackupStub.get()

    val createRes = subject.create(expected)
    assertThat(createRes).isEqualTo(expected)

    subject.get(createRes.name).let { assertThat(it).isEqualTo(expected) }

    subject.delete(createRes.name)
    subject.get(createRes.name).let { assertThat(it).isNull() }
  }

  @Test
  fun create_conflictingNames_Throws() {
    val expected = BackupStub.get()

    val createRes = subject.create(expected)
    assertThat(createRes).isEqualTo(expected)

    assertFailsWith<ExposedSQLException> {
      subject.create(expected)
      // get to trigger transaction caching flush
      subject.get(expected.name)
    }
  }

  @Test
  fun canUpdate() {
    val expected = BackupStub.get()

    subject.create(expected).let { assertThat(it).isEqualTo(expected) }

    subject.get(expected.name).let { assertThat(it).isEqualTo(expected) }

    val expectedUpdate = BackupStub.get().copy(name = expected.name)
    subject.update(expectedUpdate).let { assertThat(it).isEqualTo(expectedUpdate) }
  }
}
