package com.evrl.hardcoded

import org.scalatest.{FlatSpec, ShouldMatchers}

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

  val hardcodedValue = EnvLocal("TEST_VALUE", (PROD, LT) -> 50, INT -> 25, DEFAULT -> 10)

  "hardcoded" should "return the correct value" in {
    // exercise simple, multiple, and default
    hardcodedValue.get(INT) should be(25)
    hardcodedValue.get(PROD) should be(50)
    hardcodedValue.get(STG) should be(10)
  }

  it should "respect the system environment" in {
    val env = System.getenv()
    val fld = env.getClass.getDeclaredField("m")
    fld.setAccessible(true)
    fld.get(env).asInstanceOf[java.util.Map[String,String]].put("TEST_VALUE", "42")

    // regardless of environment, we now should
    hardcodedValue.get(INT) should be(42)
    hardcodedValue.get(PROD) should be(42)
    hardcodedValue.get(STG) should be(42)
  }
}
