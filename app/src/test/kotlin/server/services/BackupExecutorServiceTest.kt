package server.services

import com.google.common.truth.Truth.assertThat
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import server.TestHarness
import server.TestHarnessExtension
import server.models.Backup
import server.models.BackupResult
import server.models.BackupStub

@ExtendWith(TestHarnessExtension::class)
class BackupExecutorServiceTest(private val harness: TestHarness) {
  private val subject = harness.getInstance<BackupExecutorService>()
  private val backupService = harness.getInstance<BackupService>()

  private fun createBackupsForTest(): List<Backup> =
      generateSequence { backupService.create(BackupStub.get("* 1 * ? * *")) }.take(5).toList()

  @Test
  fun canManageJobs() {
    var backups = createBackupsForTest()
    subject.ensureBackupJobsExist()

    subject.listJobs().let { jobs ->
      assertThat(jobs)
          .containsExactlyElementsIn(
              backups.map { BackupExecutorService.JobDescription(it.name, it.cronSchedule) })
    }

    // Removed backup should remove job
    backupService.delete(backups[0].name)
    backups = backupService.list()

    subject.ensureBackupJobsExist()

    subject.listJobs().let { jobs ->
      assertThat(jobs)
          .containsExactlyElementsIn(
              backups.map { BackupExecutorService.JobDescription(it.name, it.cronSchedule) })
    }
  }

  @Test
  fun jobsAreNotDoubledScheduled() {
    val backups = createBackupsForTest()
    subject.ensureBackupJobsExist()

    subject.listJobs().let { jobs ->
      assertThat(jobs)
          .containsExactlyElementsIn(
              backups.map { BackupExecutorService.JobDescription(it.name, it.cronSchedule) })
    }

    subject.ensureBackupJobsExist()

    subject.listJobs().let { jobs ->
      assertThat(jobs)
          .containsExactlyElementsIn(
              backups.map { BackupExecutorService.JobDescription(it.name, it.cronSchedule) })
    }
  }

  @Test
  fun jobsCronScheduleIsUpdated() {
    val backups = createBackupsForTest()
    subject.ensureBackupJobsExist()

    subject.listJobs().let { jobs ->
      assertThat(jobs)
          .containsExactlyElementsIn(
              backups.map { BackupExecutorService.JobDescription(it.name, it.cronSchedule) })
    }

    val updatedBackup = backups[0].copy(cronSchedule = "1 * * ? * *")
    backupService.update(updatedBackup)

    subject.ensureBackupJobsExist()

    subject.listJobs().let { jobs ->
      val targetJob = jobs.first { it.backupName == updatedBackup.name }
      assertThat(targetJob.cronSchedule).isEqualTo(updatedBackup.cronSchedule)
    }
  }

  @OptIn(ExperimentalPathApi::class)
  @Test
  fun backupJobReportsErrorOnMissingSourceDir() {
    val fileToFile = harness.setupLocalFileToFileConfigTest()
    val backupService: BackupService = harness.getInstance()
    val backupResultService: BackupResultService = harness.getInstance()

    val backup =
        backupService.create(
            BackupStub.get(
                // evey 5 seconds
                cronSchedule = "0/5 * * ? * *",
                sourceDir = fileToFile.sourceDir,
                destinationDir = fileToFile.destinationDir,
                config = fileToFile.config))

    // Delete source dir to elicit a failure
    Path.of(fileToFile.sourceDir).deleteRecursively()

    subject.ensureBackupJobsExist()
    var backupResults = backupResultService.list(backup.name)
    while (backupResults.isEmpty()) {
      backupResults = backupResultService.list(backup.name)
    }

    val result = backupResults.first()
    assertThat(result.status).isEqualTo(BackupResult.Status.Failure)
  }

  @Test
  fun backupJobSuccessfullyRuns() {
    val fileToFile = harness.setupLocalFileToFileConfigTest()
    val backupService: BackupService = harness.getInstance()
    val backupResultService: BackupResultService = harness.getInstance()

    val backup =
        backupService.create(
            BackupStub.get(
                // evey 5 seconds
                cronSchedule = "0/5 * * ? * *",
                sourceDir = fileToFile.sourceDir,
                destinationDir = fileToFile.destinationDir,
                config = fileToFile.config))

    subject.ensureBackupJobsExist()
    var backupResults = backupResultService.list(backup.name)
    while (backupResults.isEmpty()) {
      backupResults = backupResultService.list(backup.name)
    }

    val result = backupResults.first()
    assertThat(result.status).isEqualTo(BackupResult.Status.Success)

    val copiedFiles =
        Path.of(fileToFile.destinationDir)
            .listDirectoryEntries()
            .filter { it.isRegularFile() }
            .map { it.fileName.toString() }

    assertThat(copiedFiles).containsExactlyElementsIn(fileToFile.fileNames)
  }
}
