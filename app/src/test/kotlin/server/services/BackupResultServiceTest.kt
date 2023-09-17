package server.services

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import server.TestHarness
import server.TestHarnessExtension
import server.models.BackupName
import server.models.BackupResultStub
import server.models.BackupStub

@ExtendWith(TestHarnessExtension::class)
class BackupResultServiceTest(harness: TestHarness) {

  private val subject: BackupResultService = harness.getInstance()
  private val backupName: BackupName

  init {
    val backupService: BackupService = harness.getInstance()
    backupName = backupService.create(BackupStub.get()).name
  }

  @Test
  fun canManageResource() {
    val backupResult = BackupResultStub.get(backupName)
    assertThat(subject.get(backupResult.name)).isNull()
    assertThat(subject.list(backupName)).isEmpty()

    subject.create(backupResult)

    assertThat(subject.get(backupResult.name)).isEqualTo(backupResult)
    assertThat(subject.list(backupName)).containsExactly(backupResult)

    subject.delete(backupResult.name)

    assertThat(subject.get(backupResult.name)).isNull()
    assertThat(subject.list(backupName)).isEmpty()
  }
}
