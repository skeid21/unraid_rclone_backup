package server.services

typealias SideEffect = () -> Unit

fun Any.effect(s: SideEffect) {
  s()
}
