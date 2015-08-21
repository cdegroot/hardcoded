package com.evrl.hardcoded

import org.scalatest.{FlatSpec, ShouldMatchers}

/**
 * Hardcoded should be simple enough to get by with a single test class ;-)
 */
class TheTest extends FlatSpec with ShouldMatchers {

  object envs {
    val PROD = Environment("PROD")
    val LT   = Environment("LOAD_TEST")
    val STG  = Environment("STAGING")
  }

  import envs._

  val hardcodedValue = EnvLocal("TEST_VALUE", (PROD, LT) -> 50, STG -> 10, DEFAULT -> 10)

  "hardcoded" should "return the correct value" in {
    hardcodedValue.get(PROD) should be(50)

  }
}
