package com.evrl

import scala.language.implicitConversions

/**
 * Hardcoded package object - this should be the only import you need
 */
package object hardcoded {

  case class Environment(name: String)

  /** There's always a default environment as a catch-all */
  val DEFAULT = Environment("DEFAULT")

  implicit def tuple2EnvToSingleEnv[T](t2: ((Environment, Environment), T)) : (Environment, T) = ???

  case class EnvLocal[T](envOverrideName: String, defs: (Environment, T)*) {
    def get(env: Environment): T = ???
  }
}
