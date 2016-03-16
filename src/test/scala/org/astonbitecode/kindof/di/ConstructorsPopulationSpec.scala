package org.astonbitecode.kindof.di

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
import org.junit.runner.RunWith
import org.specs2.mutable
import org.specs2.runner.JUnitRunner
import org.specs2.specification.BeforeEach

@RunWith(classOf[JUnitRunner])
class ConstructorsPopulationSpec extends mutable.Specification with BeforeEach {
  val timeout = FiniteDuration(1000, TimeUnit.MILLISECONDS)

  override def before() {
    TestUtil.clean
  }

  "A spec for the Constructors population in the DI".txt

  "A constructor should be populated in the DI" >> {
    cache must haveSize(0)
    val f = defineConstructor { () => MyInjectableClass("One") }
    Await.result(f, timeout)
    cache must haveSize(1)
  }

  "A constructor should be replaced in the DI if it already exists" >> {
    val f1 = defineConstructor { () => MyInjectableClass("One") }
    Await.result(f1, timeout)
    val f2 = defineConstructor { () => MyInjectableClass("Two") }
    Await.result(f2, timeout)
    cache must haveSize(1)
    cache.head._2.constructor.apply().asInstanceOf[MyInjectableClass].id === "Two"
  }

  case class MyInjectableClass(id: String)

}
