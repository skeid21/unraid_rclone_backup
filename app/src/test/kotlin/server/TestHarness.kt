package server

import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.rules.ExternalResource
import server.persistence.initDatabaseConnection

class TestHarness : ExternalResource() {
  init {
    initDatabaseConnection()
  }

  private val testTransaction = TransactionManager.manager.newTransaction()

  override fun after() {
    testTransaction.rollback()
  }
}
