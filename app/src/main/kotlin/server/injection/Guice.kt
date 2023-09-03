package server.injection

import com.google.inject.Guice

val injector = Guice.createInjector()!!

/** Utilize getInstance in route lambda to resolve instances an fulfill business logic */
inline fun <reified T> getInstance(): T = injector.getInstance(T::class.java)

inline fun <reified T> withInstance(block: T.() -> Unit) = injector.getInstance(T::class.java).block()
