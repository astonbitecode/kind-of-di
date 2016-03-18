package go.libre.abc.kindof.di

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

  sequential

  "A spec for the Constructors population in the DI ".txt

  "A constructor should be populated in the DI" >> {
    val f = diDefine { () => MyInjectableClass("One") }
    Await.result(f, timeout)
    cache must haveSize(1)
  }

  "A constructor should be replaced in the DI if it already exists" >> {
    val f1 = diDefine { () => MyInjectableClass("One") }
    Await.result(f1, timeout)
    val f2 = diDefine { () => MyInjectableClass("Two") }
    Await.result(f2, timeout)
    cache must haveSize(1)
    cache.head._2.constructor.apply().asInstanceOf[MyInjectableClass].id === "Two"
  }

  "A constructor with scope SINGLETON_EAGER should create the instance upon the call" >> {
    val f = diDefine(() => MyInjectableClass("One"), DIScope.SINGLETON_EAGER)
    Await.result(f, timeout)
    cache must haveSize(1)
    cache.head._2.cachedInstance.isDefined === true
  }

  "A constructor with scope SINGLETON_LAZY should not create the instance upon the call" >> {
    val f = diDefine(() => MyInjectableClass("One"), DIScope.SINGLETON_LAZY)
    Await.result(f, timeout)
    cache must haveSize(1)
    cache.head._2.cachedInstance.isDefined === false
  }

  case class MyInjectableClass(id: String)

}
