/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package server

import kotlin.test.Test
import com.google.common.truth.Truth.assertThat

class ServerTest {
  @Test
  fun appHasAGreeting() {
    val classUnderTest = Server()
    assertThat(classUnderTest.greeting).isNotNull()
  }
}
