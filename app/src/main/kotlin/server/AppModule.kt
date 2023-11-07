package server

import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import server.persistence.DatabaseModule
import server.services.ServicesModule

class AppModule : Module {
  override fun configure(binder: Binder) {
    binder.install(DatabaseModule())
    binder.install(ServicesModule())
  }
}

fun newInjector(): Injector = Guice.createInjector(AppModule())!!

val injector = newInjector()

/** Utilize getInstance in route lambda to resolve instances an fulfill business logic */
inline fun <reified T> getInstance(): T = injector.getInstance(T::class.java)

inline fun <reified T> withInstance(block: T.() -> Unit) =
    injector.getInstance(T::class.java).block()
