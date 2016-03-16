package org.astonbitecode.kindof.di

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import org.junit.runner.RunWith
import org.specs2.mutable
import org.specs2.runner.JUnitRunner
import scala.concurrent.Await
import org.specs2.specification.BeforeEach

@RunWith(classOf[JUnitRunner])
class InjectionSpec extends mutable.Specification with BeforeEach {
  val timeout = FiniteDuration(1000, TimeUnit.MILLISECONDS)

  override def before() {
    TestUtil.clean
  }

  sequential

  "A spec for the Injections".txt

  "Singleton Injections" >> {
    "should be the default" >> {
      val f = diDefine { () => MyInjectableClass("I am a Singleton") }
      Await.result(f, timeout)

      class MyClassWithSingleton {
        val mic = inject[MyInjectableClass]
      }

      val m1 = new MyClassWithSingleton
      m1.mic.id === "I am a Singleton"
      val m2 = new MyClassWithSingleton
      m2.mic.id === "I am a Singleton"
      val m3 = new MyClassWithSingleton
      m3.mic.id === "I am a Singleton"
    }

    "should happen when the user code defines a SINGLETON_EAGER scope" >> {
      val f = diDefine(() => MyInjectableClass("I am a Singleton"), DIScope.SINGLETON_EAGER)
      Await.result(f, timeout)

      class MyClassWithSingleton {
        val mic = inject[MyInjectableClass]
      }

      val m1 = new MyClassWithSingleton
      m1.mic.id === "I am a Singleton"
      val m2 = new MyClassWithSingleton
      m2.mic.id === "I am a Singleton"
      val m3 = new MyClassWithSingleton
      m3.mic.id === "I am a Singleton"
    }

    "should happen when the user code defines a SINGLETON_LAZY scope" >> {
      val f = diDefine(() => MyInjectableClass("I am a Singleton"), DIScope.SINGLETON_LAZY)
      Await.result(f, timeout)

      class MyClassWithSingleton {
        val mic = inject[MyInjectableClass]
      }

      val m1 = new MyClassWithSingleton
      m1.mic.id === "I am a Singleton"
      val m2 = new MyClassWithSingleton
      m2.mic.id === "I am a Singleton"
      val m3 = new MyClassWithSingleton
      m3.mic.id === "I am a Singleton"
    }
  }

  "Prototype Injections" >> {
    "should happen when the user code defines it so" >> {
      val f = diDefine(() => MyInjectableClass("I am a Prototype " + System.nanoTime), DIScope.PROTOTYPE)
      Await.result(f, timeout)

      class MyClassWithPrototype {
        val mic = inject[MyInjectableClass]
      }

      var prevId = "Empty"
      val m1 = new MyClassWithPrototype
      prevId = m1.mic.id
      prevId !== "Empty"
      val m2 = new MyClassWithPrototype
      prevId !== m2.mic.id
      prevId = m2.mic.id
      val m3 = new MyClassWithPrototype
      prevId !== m3.mic.id
    }
  }

  case class MyInjectableClass(id: String)

}
