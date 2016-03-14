package example.app

import org.astonbitecode.kindof.di.initService
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
  val c = new ConfigurationService("/my/path")
  initService(c)
  val hc = new HelpfulClass
  initService(hc)

  // Run the application (2)
  new UseCase().execute

  // Try to re-initialize the service (3)
  try {
    initService(new ConfigurationService("/my/other/path"))
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