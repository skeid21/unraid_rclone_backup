package server

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Stage
import com.google.inject.util.Modules
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.random.Random
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.api.extension.ParameterResolver
import server.persistence.DatabaseModuleTest
import server.services.BackupExecutorService

class TestHarness() {
  val injector: Injector =
      Guice.createInjector(
          Stage.DEVELOPMENT, Modules.override(AppModule()).with(DatabaseModuleTest()))

  inline fun <reified T> getInstance(): T = injector.getInstance(T::class.java)

  data class FileToFileTestSetup(
      val config: String,
      val sourceDir: String,
      val destinationDir: String,
      val fileNames: List<String>
  )

  fun setupLocalFileToFileConfigTest(): FileToFileTestSetup {
    val sourceDir = Paths.get(TESTING_TEMP_DIR, Random.next())
    sourceDir.createDirectories()

    val destinationDir = Paths.get(TESTING_TEMP_DIR, Random.next())
    destinationDir.createDirectories()

    val filesCreated = mutableListOf<String>()
    for (n in 1..5) {
      val fileCreated = Files.createFile(sourceDir.resolve(Random.next<String>()))
      filesCreated.add(fileCreated.fileName.toString())
    }

    return FileToFileTestSetup(
        config = TESTING_RCLONE_CONFIG,
        sourceDir = sourceDir.toString(),
        destinationDir = destinationDir.toString(),
        filesCreated,
    )
  }

  companion object {
    val TESTING_TEMP_DIR: String = System.getProperty("application.testing.temp_dir")
    private val TESTING_RCLONE_CONFIG =
        """
      [remote]
      type = local
      skip_links = true
    """
            .trimIndent()
  }
}

class TestHarnessExtension :
    ParameterResolver, BeforeAllCallback, AfterEachCallback, BeforeEachCallback {
  override fun supportsParameter(
      paramContext: ParameterContext,
      extensionContext: ExtensionContext
  ): Boolean = paramContext.parameter.type == TestHarness::class.java

  override fun resolveParameter(
      paramContext: ParameterContext,
      extensionContext: ExtensionContext
  ): Any {
    if (paramContext.parameter.type == TestHarness::class.java) {
      return TestHarness()
    }

    throw ParameterResolutionException("Unsupported type requested")
  }

  override fun beforeEach(extensionContext: ExtensionContext?) {
    BackupExecutorService.setup()
  }

  override fun afterEach(extensionContext: ExtensionContext?) {
    BackupExecutorService.teardown()
    DatabaseModuleTest.initDatabaseForTesting()
  }

  override fun beforeAll(extensionContext: ExtensionContext?) {
    DatabaseModuleTest.initDatabaseForTesting()
  }
}
