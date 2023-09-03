package server

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.api.extension.ParameterResolver
import server.injection.newInjector

class TestHarness {
  val injector = newInjector().apply { getInstance(Database::class.java) }

  inline fun <reified T> getInstance(): T = injector.getInstance(T::class.java)
}

class TestHarnessExtension : ParameterResolver, BeforeEachCallback, AfterEachCallback {
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

  override fun beforeEach(extensionContext: ExtensionContext?) {
    val manager = TransactionManager.managerFor(harness?.getInstance())
    manager?.currentOrNull()?.rollback()
    manager?.newTransaction()
  }

  override fun afterEach(extensionContext: ExtensionContext?) {
    TransactionManager.managerFor(harness?.getInstance())?.currentOrNull()?.rollback()
  }
}
