package server.services

import com.google.common.truth.Truth.assertThat
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.Scheduler
import org.quartz.spi.JobFactory
import org.quartz.spi.TriggerFiredBundle
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
  fun jobsWillNotExecuteIfPreviousRunIsInProgress() {

    var shouldSleep = true
    class FakeJob() : Job {
      override fun execute(context: JobExecutionContext?) {
        while (shouldSleep) {
          Thread.sleep(10)
        }
      }
    }
    class FakeJobFactory() : JobFactory {
      override fun newJob(bundle: TriggerFiredBundle?, scheduler: Scheduler?): Job {
        println("+++++++ Got 1")
        return FakeJob()
      }
    }

    backupService.create(BackupStub.get("0/1 * * ? * *"))
    val sut = BackupExecutorService(backupService, FakeJobFactory())
    sut.ensureBackupJobsExist()

    Thread.sleep(3000)

    sut.listExecutingJobs().let { executingJobs -> assertThat(executingJobs).hasSize(1) }
  }

  @Test
  fun jobsWillNotScheduleIfScheduleIsPaused() {
    val backup = backupService.create(BackupStub.get("0/1 * * ? * *").copy(schedulePaused = true))
    subject.ensureBackupJobsExist()

    val jobs = subject.listJobs()
    assertThat(jobs).hasSize(0)
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

  @Test
  fun backupJob_paused_canBeManuallyTriggered() {
    val fileToFile = harness.setupLocalFileToFileConfigTest()
    val backupService: BackupService = harness.getInstance()
    val backupResultService: BackupResultService = harness.getInstance()

    val backup =
        backupService.create(
            BackupStub.get(
                // evey 5 seconds
                cronSchedule = "0/5 * * ? * *",
                schedulePaused = true,
                sourceDir = fileToFile.sourceDir,
                destinationDir = fileToFile.destinationDir,
                config = fileToFile.config,
            ))

    subject.ensureBackupJobsExist()
    subject.runBackupJobNow(backup.name)

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

  @Test
  fun backupJob_canBeManuallyTriggered() {
    val fileToFile = harness.setupLocalFileToFileConfigTest()
    val backupService: BackupService = harness.getInstance()
    val backupResultService: BackupResultService = harness.getInstance()

    val backup =
        backupService.create(
            BackupStub.get(
                // 10th minute of 1st hour
                // Low chances of test running where the cron would trigger the job
                cronSchedule = "* 10 1 ? * *",
                schedulePaused = false,
                sourceDir = fileToFile.sourceDir,
                destinationDir = fileToFile.destinationDir,
                config = fileToFile.config,
            ))

    subject.ensureBackupJobsExist()
    subject.runBackupJobNow(backup.name)

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
