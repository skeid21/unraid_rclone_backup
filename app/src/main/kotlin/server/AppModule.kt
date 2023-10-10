package server

import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import server.persistence.DatabaseModule

class AppModule : Module {
  override fun configure(binder: Binder) {
    binder.install(DatabaseModule())
  }
}

fun newInjector(): Injector = Guice.createInjector(AppModule())!!

val injector = newInjector()

/** Utilize getInstance in route lambda to resolve instances an fulfill business logic */
inline fun <reified T> getInstance(): T = injector.getInstance(T::class.java)

inline fun <reified T> withInstance(block: T.() -> Unit) =
    injector.getInstance(T::class.java).block()
