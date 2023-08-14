/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package server

import io.grpc.ServerBuilder

fun buildServer(port: Int): io.grpc.Server =
    ServerBuilder.forPort(port).addService(BackupService()).build()

fun main() {
  val port = System.getenv("PORT")?.toInt() ?: 50051
  val server = buildServer(port)
  server.start()
  server.awaitTermination()
}
