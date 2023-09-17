package server.models

import com.google.common.truth.Truth.assertThat
import java.util.stream.Stream
import kotlin.test.assertFailsWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class BackupTest {
  @ParameterizedTest
  @MethodSource("badNames")
  fun backupName_invalidInputs(name: String) {
    assertFailsWith<IllegalStateException> { BackupName(name) }
  }

  @ParameterizedTest
  @MethodSource("goodNames")
  fun backupName_validInputs(name: String, id: String) {
    val bm = BackupName(name)
    assertThat(bm.id).isEqualTo(id)
    assertThat(bm.value).isEqualTo(name)
  }

  companion object {
    @JvmStatic
    fun badNames(): Stream<Arguments> =
        Stream.of(
            Arguments.of(
                // bad collection name
                "bakup/mybackup"),
            Arguments.of(
                // additional name parts
                "backup/mybackup/extrapart"),
            Arguments.of(
                // ends with /
                "backup/mybackup/"))

    @JvmStatic
    fun goodNames(): Stream<Arguments> =
        Stream.of(
            Arguments.of("backups/mybackup", "mybackup"),
            Arguments.of("backups/1backup", "1backup"),
            Arguments.of("backups/backup2", "backup2"))
  }
}
