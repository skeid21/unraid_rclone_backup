package server.persistence

import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import server.TestHarness
import server.TestHarnessExtension
import server.models.BackupStub
import server.models.toCompare

@ExtendWith(TestHarnessExtension::class)
class BackupsDalTest(harness: TestHarness) {

  private val subject = harness.getInstance<BackupsDal>()

  @Test
  fun canPersist() {
    val expected = BackupStub.get()

    val createRes = subject.create(expected)
    assertThat(createRes.toCompare()).isEqualTo(expected.toCompare())

    subject.get(createRes.name).let { assertThat(it?.toCompare()).isEqualTo(expected.toCompare()) }

    subject.delete(createRes.name)
    subject.get(createRes.name).let { assertThat(it).isNull() }
  }

  @Test
  fun canUpdate() {
    val expected = BackupStub.get()

    subject.create(expected).let { assertThat(it.toCompare()).isEqualTo(expected.toCompare()) }

    subject.get(expected.name).let { assertThat(it?.toCompare()).isEqualTo(expected.toCompare()) }

    val expectedUpdate = BackupStub.get().copy(name = expected.name)
    subject.update(expectedUpdate).let {
      assertThat(it?.toCompare()).isEqualTo(expectedUpdate.toCompare())
    }
  }

  @Test
  fun canList() {
    val created = generateSequence { subject.create(BackupStub.get()) }.take(5).toList()

    val res = subject.list()

    assertThat(res.toCompare()).containsExactlyElementsIn(created.toCompare())
  }

  @Test
  fun create_conflictingNames_Throws() {
    val expected = BackupStub.get()

    val createRes = subject.create(expected)
    assertThat(createRes.toCompare()).isEqualTo(expected.toCompare())

    assertFailsWith<ExposedSQLException> {
      subject.create(expected)
      // get to trigger transaction caching flush
      subject.get(expected.name)
    }
  }
}
