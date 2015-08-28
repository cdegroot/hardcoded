package com.evrl.hardcoded

import java.text.SimpleDateFormat
import scala.language.implicitConversions

import org.scalatest.{FlatSpec, ShouldMatchers}

import scala.util.control.NonFatal

/**
 * Hardcoded should be simple enough to get by with a single test class ;-)
 */
class TheTest extends FlatSpec with ShouldMatchers {

  object envs {
    val PROD = Environment("Production")
    val LT   = Environment("Load Test")
    val INT  = Environment("Integration")
    val STG  = Environment("Staging")
  }

  import envs._


  "hardcoded" should "return the correct value" in {
    val hardcodedValue = EnvLocal((PROD, LT) -> 50, INT -> 25, DEFAULT -> 10)

    // exercise simple, multiple, and default
    hardcodedValue.get(INT) should be(25)
    hardcodedValue.get(PROD) should be(50)
    hardcodedValue.get(STG) should be(10)
  }

  it should "respect the system environment" in {
    val hardcodedValue = EnvLocal("TEST_VALUE", (PROD, LT) -> 50, INT -> 25, DEFAULT -> 10)

    setenv("TEST_VALUE", "42")

    // regardless of environment, we now should get the value injected through
    // the process environment. Also exercises using apply() instead of get()
    hardcodedValue(INT) should be(42)
    hardcodedValue(PROD) should be(42)
    hardcodedValue(STG) should be(42)
  }

  it should "convert any environment variable that has a converter" in {
    val format = new SimpleDateFormat("yyyy-MM-dd")
    val date = format.parse("2015-08-25")
    setenv("FOO", format.format(date))
    implicit def parseDate(s: String): Option[java.util.Date] = try Some(format.parse(s)) catch { case NonFatal(_) => None }

    val v: EnvLocal[java.util.Date] = EnvLocal("FOO", DEFAULT -> null)

    v.get(PROD) should be(date)
  }

  // Hack hack with the system environment.

  def setenv(key: String, value: String): Unit = {
    val env = System.getenv()
    val fld = env.getClass.getDeclaredField("m")
    fld.setAccessible(true)
    fld.get(env).asInstanceOf[java.util.Map[String,String]].put(key, value)
  }
}
