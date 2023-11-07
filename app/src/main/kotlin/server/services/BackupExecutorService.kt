package server.services

import jakarta.inject.Inject
import java.io.File
import java.util.UUID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.quartz.CronScheduleBuilder.cronSchedule
import org.quartz.CronTrigger
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobBuilder.newJob
import org.quartz.JobDetail
import org.quartz.JobExecutionContext
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.Trigger
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.GroupMatcher
import org.quartz.spi.JobFactory
import org.quartz.spi.TriggerFiredBundle
import server.models.Backup
import server.models.BackupName
import server.models.BackupResult
import server.models.BackupResultName

class BackupExecutorService
@Inject
constructor(private val backupService: BackupService, jobFactory: JobFactory) {
  init {
    scheduler.setJobFactory(jobFactory)
  }

  data class JobDescription(val backupName: BackupName, val cronSchedule: String)

  fun ensureBackupJobsExist() {
    val backups = backupService.list()
    fun trigger(triggerName: String, jobKey: JobKey, backup: Backup): Trigger =
        newTrigger()
            .withIdentity(triggerName, GROUP)
            .withSchedule(
                cronSchedule(backup.cronSchedule).withMisfireHandlingInstructionDoNothing())
            .forJob(jobKey)
            .build()

    val currentJobs = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(GROUP))
    for (backup in backups) {
      val jobKey = JobKey("${backup.name.value}_job", GROUP)
      val triggerName = "${backup.name.value}_trigger"
      val trigger = trigger(triggerName, jobKey, backup)

      if (!currentJobs.contains(jobKey)) {
        val job =
            newJob(BackupJob::class.java)
                .withIdentity(jobKey)
                .usingJobData(BACKUP_NAME_JOB_DATA_KEY, backup.name.value)
                .build()

        scheduler.scheduleJob(job, trigger)
      } else {
        currentJobs.remove(jobKey)
        scheduler.rescheduleJob(trigger.key, trigger)
      }
    }

    // Delete any jobs not referenced in the input backups list
    scheduler.deleteJobs(currentJobs.toList())
  }

  fun listJobs(): List<JobDescription> =
      scheduler
          .getJobKeys(GroupMatcher.jobGroupEquals(GROUP))
          .map { key ->
            val jobDetail = scheduler.getJobDetail(key)
            val triggers = scheduler.getTriggersOfJob(key)
            require(triggers.count() == 1)

            val cronTrigger = triggers.first() as CronTrigger

            JobDescription(jobDetail.backupName, cronTrigger.cronExpression)
          }
          .toList()

  fun listExecutingJobs(): List<BackupName> =
      scheduler.currentlyExecutingJobs.map { it.jobDetail.backupName }

  companion object {
    const val GROUP = "backup_jobs"
    const val BACKUP_NAME_JOB_DATA_KEY = "backup_name_job_data"

    private val scheduler: Scheduler
      get() = StdSchedulerFactory.getDefaultScheduler()

    init {
      setup()
    }

    fun setup() {
      scheduler.start()
    }

    fun teardown() {
      scheduler.shutdown()
    }
  }
}

private val JobDetail.backupName: BackupName
  get() = BackupName(this.jobDataMap.getString(BackupExecutorService.BACKUP_NAME_JOB_DATA_KEY))

// If a backup job is already running do not start that job again.
// Ensures if a job is running long, another trigger of that job will not start processing.
@DisallowConcurrentExecution
class BackupJob
@Inject
constructor(
    private val backupService: BackupService,
    private val backupResultService: BackupResultService
) : Job {
  private fun deleteExcessResults(backupName: BackupName) {
    val results = backupResultService.list(backupName)
    if (results.count() > 10) {
      results
          .sortedBy { it.endTime }
          .map { it.name }
          .take(results.count() - 10)
          .forEach { backupResultService.delete(it) }
    }
  }

  override fun execute(context: JobExecutionContext) {

    val backupName = context.jobDetail.backupName
    deleteExcessResults(backupName)

    fun backupResultName() =
        BackupResultName("${backupName.value}/backupResults/${UUID.randomUUID()}")
    val startTime = Clock.System.now()

    fun createBackupResults(endTime: Instant, status: BackupResult.Status, output: String) {
      backupResultService.create(
          BackupResult(
              name = backupResultName(),
              startTime = startTime,
              endTime = endTime,
              output = output,
              status = status))
    }

    val configFile = File.createTempFile("rclone-config", "tmp")
    try {
      val backup = backupService.get(backupName)!!

      // write config to temp location
      configFile.writeText(backup.config)
      // TODO - remote expected the be the remote name
      val p =
          ProcessBuilder(
                  "rclone",
                  "--log-level=INFO",
                  "--config",
                  configFile.absolutePath,
                  "sync",
                  backup.sourceDir,
                  "remote:${backup.destinationDir}")
              .redirectErrorStream(true)
              .start()

      p.waitFor()

      val output =
          p.inputReader().let {
            val lines = it.readLines()
            return@let if (lines.isNotEmpty()) {
              lines.reduce { acc, s -> acc + "\n" + s }
            } else {
              ""
            }
          }

      val status =
          if (p.exitValue() != 0) {
            BackupResult.Status.Failure
          } else {
            BackupResult.Status.Success
          }

      createBackupResults(Clock.System.now(), status, output)
    } catch (t: Throwable) {
      createBackupResults(
          Clock.System.now(), BackupResult.Status.Failure, t.message ?: t.javaClass.name)
    } finally {
      configFile.delete()
    }
  }
}

class BackupJobFactory
@Inject
constructor(
    private val backupService: BackupService,
    private val backupResultService: BackupResultService
) : JobFactory {
  override fun newJob(bundle: TriggerFiredBundle, scheduler: Scheduler): Job {
    require(bundle.jobDetail.jobClass == BackupJob::class.java)
    return BackupJob(backupService, backupResultService)
  }
}
