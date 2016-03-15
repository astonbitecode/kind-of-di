package org.astonbitecode.kindof.di

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
import org.junit.runner.RunWith
import org.specs2.mutable
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ConstructorsPopulationSpec extends mutable.Specification {
  val timeout = FiniteDuration(1000, TimeUnit.MILLISECONDS)

  sequential

  "A spec for the Constructors population in the DI".txt

  "A constructor" >> {
    "should be populated in the DI" >> {
      constructors must haveSize(0)
      val f = defineConstructor { () => MyInjectableClass("One") }
      Await.result(f, timeout)
      constructors must haveSize(1)
    }

    "should be replaced in the DI if it already exists" >> {
      val f = defineConstructor { () => MyInjectableClass("Two") }
      Await.result(f, timeout)
      constructors must haveSize(1)
      constructors.head._2.apply().asInstanceOf[MyInjectableClass].id === "Two"
    }
  }

  case class MyInjectableClass(id: String)

}
