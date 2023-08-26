package server.persistence

import com.google.common.truth.Truth.assertThat
import kotlin.random.Random
import kotlin.test.assertFailsWith
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.Rule
import org.junit.jupiter.api.Test
import server.TestHarness
import server.injection.getInstance
import server.models.Backup
import server.next

class BackupsTest {
  @Rule @JvmField val harness = TestHarness()

  private val subject = getInstance<BackupsDAL>()

  init {
    initDatabaseConnection()
  }

  @Test
  fun canList() {
    val created =
        generateSequence {
              subject.create(
                  Backup(name = Random.next(), displayName = Random.next(), config = Random.next()))
            }
            .take(5)
            .toList()

    val res = subject.list()

    assertThat(res).containsAtLeastElementsIn(created)
  }

  @Test
  fun canPersist() {
    val expected = Backup(name = Random.next(), displayName = Random.next(), config = "")

    val createRes = subject.create(expected)
    assertThat(createRes).isEqualTo(expected)

    subject.get(createRes.name).let { assertThat(it).isEqualTo(expected) }

    subject.delete(createRes.name)
    subject.get(createRes.name).let { assertThat(it).isNull() }
  }

  @Test
  fun create_conflictingNames_Throws() {
    val expected = Backup(name = Random.next(), displayName = Random.next(), config = "")

    val createRes = subject.create(expected)
    assertThat(createRes).isEqualTo(expected)

    assertFailsWith<ExposedSQLException> {
      subject.create(expected)
      // get to trigger transaction caching flush
      subject.get(expected.name)
    }
  }
}
