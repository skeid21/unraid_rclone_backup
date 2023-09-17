package server

import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.api.extension.ParameterResolver
import server.injection.newInjector
import server.persistence.initDatabaseConnection

class TestHarness {
  val injector = newInjector().apply { getInstance(Database::class.java) }

  inline fun <reified T> getInstance(): T = injector.getInstance(T::class.java)
}

class TestHarnessExtension : ParameterResolver, AfterEachCallback {
  private var harness: TestHarness? = null

  override fun supportsParameter(
      paramContext: ParameterContext,
      extensionContext: ExtensionContext
  ): Boolean = paramContext.parameter.type == TestHarness::class.java

  override fun resolveParameter(
      paramContext: ParameterContext,
      extensionContext: ExtensionContext
  ): Any {
    if (paramContext.parameter.type == TestHarness::class.java) {
      harness = TestHarness()
      return harness!!
    }

    throw ParameterResolutionException("Unsupported type requested")
  }

  override fun afterEach(extensionContext: ExtensionContext?) {
    // re-initialize the db connection so the in-memory-db chagnes are not carried
    // forward to the next test.
    initDatabaseConnection()
  }
}
