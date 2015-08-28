package com.evrl

import scala.language.implicitConversions
import scala.reflect.runtime.universe._
import scala.util.control.NonFatal

/**
 * Hardcoded package object - this should be the only import you need
 */
package object hardcoded {

  /** An environment. Define as many as you want */
  case class Environment(name: String)

  /** There's always a default environment as a catch-all */
  val DEFAULT = Environment("DEFAULT")

  // An EnvLocal consists of these environment-specific values. Usually you don't need it
  // directly, because of the implicit conversions which let you specify values with
  // tuples.
  trait EnvironmentValue[T] {
    private[hardcoded] def getIfFor(targetEnv: Environment): Option[T]
  }

  /**
   * If your EnvLocal is not one of the primitive types, you can make a converted
   * implicitly available to parse environment overrides into the correct type.
   * @param s The string to parse
   * @tparam T The type to return
   * @return An option, Some[T] if parsing is successful, None otherwise.
   */
  def defaultReader[T](s: String): Option[T] = None

  /**
   * A value that is different per environment.
   * @param envOverrideName the name of an environment variable that can override this value (or null,
   *                        see the second ctor). Note that overrides are only supported for
   *                        primitive values.
   * @param defs a list of EnvironmentValue definitions, usually given as tuples.
   * @tparam T the type that this instance encapsulates
   */
  case class EnvLocal[T: TypeTag](envOverrideName: String, defs: EnvironmentValue[T]*)
                                 (implicit reader: String => Option[T] = defaultReader _) {

    /**
     * Return the value for the indicated environment.
     * @param env the environment
     * @return the value for that environment. There is no error handling - configuration is
     *         completely in the hands of the developer, so this will just throw ugly exceptions
     *         if nothing could be found.
     */
    def get(env: Environment): T = getOverride.getOrElse(defs.flatMap(_.getIfFor(env)).head)

    /**
     * Alternative for get. Use what you like. Style matters, style varies.
     * @see get
     */
    def apply(env: Environment): T = get(env)

    private def getOverride: Option[T] = envOverrideName match {
      case null => None
      case s: String => Option(System.getenv(s)).flatMap(v => convert(v))
    }

    private def convert(value: String): Option[T] = {
      def tryConvert[X](f: => X) = try Some(f) catch { case NonFatal(_) => None }
      val generic = typeOf[T] match {
        case v if v =:= typeOf[String] => Some(value)
        case v if v =:= typeOf[Byte] => tryConvert(value.toByte)
        case v if v =:= typeOf[Short] => tryConvert(value.toShort)
        case v if v =:= typeOf[Int] => tryConvert(value.toInt)
        case v if v =:= typeOf[Long] => tryConvert(value.toLong)
        case v if v =:= typeOf[Boolean] => tryConvert(value.toBoolean)
        case v if v =:= typeOf[Float] => tryConvert(value.toFloat)
        case v if v =:= typeOf[Double] => tryConvert(value.toDouble)
        case _ => reader(value)
      }
      generic.asInstanceOf[Option[T]]
    }
  }

  object EnvLocal {
    /** Alternate constructor for if you don't need an environment override */
    def apply[T](defs: EnvironmentValue[T]*)(implicit evidence: TypeTag[T]): EnvLocal[T] =
      EnvLocal(null: String, defs:_*)(evidence, defaultReader)
  }


  case class SingleEnvironmentValue[T](env: Environment, value: T) extends EnvironmentValue[T] {
    override def getIfFor(targetEnv: Environment): Option[T] =
      if (targetEnv == env || env == DEFAULT) Some(value) else None
  }

  case class MultiEnvironmentValue[T](envs: Seq[Environment], value: T) extends EnvironmentValue[T] {
    override def getIfFor(targetEnv: Environment): Option[T] =
      envs.find(e => targetEnv == e).map(_ => value)
  }

  implicit def singleEnvToValue[T](t: (Environment, T)) : EnvironmentValue[T] =
    SingleEnvironmentValue(t._1, t._2)
  implicit def tuple2EnvToValue[T](t: ((Environment, Environment), T)) : EnvironmentValue[T] =
    MultiEnvironmentValue(Seq(t._1._1, t._1._2), t._2)
  implicit def tuple3EnvToValue[T](t: ((Environment, Environment, Environment), T)) : EnvironmentValue[T] =
    MultiEnvironmentValue(Seq(t._1._1, t._1._2, t._1._3), t._2)
  implicit def tuple4EnvToValue[T](t: ((Environment, Environment, Environment, Environment), T)) : EnvironmentValue[T] =
    MultiEnvironmentValue(Seq(t._1._1, t._1._2, t._1._3, t._1._4), t._2)
  // If you have need tuple5 and further, submit a PR. I'd also like to know about your
  // setup ;-)
}
