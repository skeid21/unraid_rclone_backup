package server.services

import com.google.common.truth.Truth.assertThat
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlinx.datetime.Clock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import server.TestHarness
import server.TestHarnessExtension
import server.models.Backup
import server.models.BackupResult
import server.models.BackupStub
import server.models.asBackupResultName

@ExtendWith(TestHarnessExtension::class)
class BackupExecutorServiceTest(private val harness: TestHarness) {
  private val subject = harness.getInstance<BackupExecutorService>()
  private val backups: List<Backup>

  init {
    val everyMinuteCron = "* 1 * ? * *"
    val backupService = harness.getInstance<BackupService>()
    backups =
        generateSequence { backupService.create(BackupStub.get(everyMinuteCron)) }.take(5).toList()
  }

  @Test
  fun canManageJobs() {
    subject.ensureJobsExist(backups)

    subject.listJobs().let { jobs ->
      assertThat(jobs)
          .containsExactlyElementsIn(
              backups.map { BackupExecutorService.JobDescription(it.name, it.cronSchedule) })
    }

    // Removed backup should remove job
    val backupsWithItemRemoved = backups.toMutableList()
    backupsWithItemRemoved.removeAt(0)

    subject.ensureJobsExist(backupsWithItemRemoved)

    subject.listJobs().let { jobs ->
      assertThat(jobs)
          .containsExactlyElementsIn(
              backupsWithItemRemoved.map {
                BackupExecutorService.JobDescription(it.name, it.cronSchedule)
              })
    }
  }

  @Test
  fun jobsAreNotDoubledScheduled() {
    subject.ensureJobsExist(backups)

    subject.listJobs().let { jobs ->
      assertThat(jobs)
          .containsExactlyElementsIn(
              backups.map { BackupExecutorService.JobDescription(it.name, it.cronSchedule) })
    }

    subject.ensureJobsExist(backups)

    subject.listJobs().let { jobs ->
      assertThat(jobs)
          .containsExactlyElementsIn(
              backups.map { BackupExecutorService.JobDescription(it.name, it.cronSchedule) })
    }
  }

  @Test
  fun jobsCronScheduleIsUpdated() {
    var backup = backups.first()
    subject.ensureJobsExist(listOf(backup))

    subject.listJobs().let {
      assertThat(it)
          .containsExactly(BackupExecutorService.JobDescription(backup.name, backup.cronSchedule))
    }

    backup = backup.copy(cronSchedule = "1 * * ? * *")

    subject.ensureJobsExist(listOf(backup))

    subject.listJobs().let {
      assertThat(it)
          .containsExactly(BackupExecutorService.JobDescription(backup.name, backup.cronSchedule))
    }
  }

  @Test
  //  	@Timeout(15)
  fun backupJobSuccessfullyRuns() {
    val fileToFile = harness.setupLocalFileToFileConfigTest()
    val backupService: BackupService = harness.getInstance()
    val backupResultService: BackupResultService = harness.getInstance()
    val backupTest =
        backupService.create(
            BackupStub.get(
                // evey 5 seconds
                cronSchedule = "0/5 * * ? * *",
                sourceDir = fileToFile.sourceDir,
                destinationDir = fileToFile.destinationDir,
                config = fileToFile.config))

    backupResultService.create(
        BackupResult(
            name =
                "${backupTest.name.value}/backupResults/${UUID.randomUUID()}".asBackupResultName(),
            startTime = Clock.System.now(),
            endTime = Clock.System.now(),
            output = "",
            result = BackupResult.Result.Success))

    val backup =
        backupService.create(
            BackupStub.get(
                // evey 5 seconds
                cronSchedule = "0/5 * * ? * *",
                sourceDir = fileToFile.sourceDir,
                destinationDir = fileToFile.destinationDir,
                config = fileToFile.config))

    subject.ensureJobsExist(listOf(backup))
    var backupResults = backupResultService.list(backup.name)
    while (backupResults.isEmpty()) {
      backupResults = backupResultService.list(backup.name)
    }

    val result = backupResults.first()
    assertThat(result.result).isEqualTo(BackupResult.Result.Success)

    val copiedFiles =
        Path.of(fileToFile.destinationDir)
            .listDirectoryEntries()
            .filter { it.isRegularFile() }
            .map { it.fileName.toString() }

    val sourceFiles = assertThat(copiedFiles).containsExactlyElementsIn(fileToFile.fileNames)
  }
}
