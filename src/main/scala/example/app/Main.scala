package example.app

import org.astonbitecode.kindof.di.defineConstructor
import org.astonbitecode.kindof.di.inject
import example.logic.{ AnotherHalpfulClass, HelpfulClass }
import example.service.ConfigurationService

object MyMain extends App {
  // Try to use the service before it gets initialized (1)
  try {
    val helpfulClassShouldFail = new HelpfulClass
  } catch {
    case error: Throwable => println(error.getMessage)
  }

  // Initialize services and classes (like the DI framework would do)
  def c() = new ConfigurationService("/my/path")
  defineConstructor(c)
  def hc() = new HelpfulClass
  defineConstructor(hc)

  // Run the application (2)
  new UseCase().execute

  // Try to re-initialize the service (3)
  try {
    defineConstructor(() => new ConfigurationService("/my/other/path"))
  } catch {
    case error: Exception => println(error.getMessage)
  }
}

class UseCase {
  // Inject a HelpfulClass instance
  val helpfulClass = inject[HelpfulClass]

  def execute(): Unit = {
    // Use the injected helpfulClass
    helpfulClass.doSomethingUsingTheConfiguration

    // Create an instance of some other class that uses internally the HelpfulClass instance as well 
    val anotherHelpfulClass = new AnotherHalpfulClass
    anotherHelpfulClass.doSomethingUsingTheHelpfulClass
  }
}