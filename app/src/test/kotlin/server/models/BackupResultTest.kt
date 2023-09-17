package server.models

import com.google.common.truth.Truth.assertThat
import java.util.stream.Stream
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class BackupResultTest {

	@ParameterizedTest
	@MethodSource("badNames")
	fun backupResultName_invalidInputs(name: String) {
		assertFailsWith<IllegalStateException> {
			BackupResultName(name)
		}
	}

	@ParameterizedTest
	@MethodSource("goodNames")
	fun backupResultName_validInputs(name: String, id: String, parent: BackupName) {
		val bm = BackupResultName(name)
		assertThat(bm.parent).isEqualTo(parent)
		assertThat(bm.id).isEqualTo(id)
		assertThat(bm.value).isEqualTo(name)
	}

	companion object {
		@JvmStatic
		fun badNames(): Stream<Arguments> =
			Stream.of(
				Arguments.of(
					//bad parent collection name
					"bakup/mybackup/backupResults/myresult"
				),

				Arguments.of(
					//bad collection name
					"backups/mybackup/backupRelts/myresult"
				),
				Arguments.of(
					// additional name parts
					"backups/mybackup/backupResults/myresult/extrapart"
				),
				Arguments.of(
					// ends with /
					"backups/mybackup/backupResults/myresult/"
				)
			)

		@JvmStatic
		fun goodNames(): Stream<Arguments> =
			Stream.of(
				Arguments.of(
					"backups/mybackup/backupResults/myresult",
					"myresult",
					BackupName("backups/mybackup")
				),
				Arguments.of(
					"backups/mybackup/backupResults/1myresult",
					"1myresult",
					BackupName("backups/mybackup")
				),
				Arguments.of(
					"backups/mybackup/backupResults/myresult2",
					"myresult2",
					BackupName("backups/mybackup")
				)
			)
	}
}