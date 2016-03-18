package examples

import go.libre.abc.kindof.di.diDefine
import go.libre.abc.kindof.di.inject

object Example1 extends App {
  /*
   * The UseCase Class contains a val that should be injected. The injected class is of type 'HelpfulClass'.
   * Try to initialize the UseCase before calling the diDefine for the service that should be injected in it (1)
   */
  try {
    val shouldFail = new UseCase
  } catch {
    case error: Throwable => println(error.getMessage)
  }

  /*
   * Define a construction method for the 'HelpfulClass'
   */
  def c() = new HelpfulClass()

  /*
   * Call the diDefine for the above
   * This could be also written as: diDefine(() => new ConfigurationService("/my/path"))
   */
  diDefine(c)

  /*
   * Now the diDefine has been called for the HelpfulClass, the creation of UseCase succeeds
   */
  val shouldSucceed = new UseCase

  /*
   * This prints 'I am doing something helpful' 
   */
  shouldSucceed.execute

}

class UseCase {
  // Inject a HelpfulClass instance
  val helpfulClass = inject[HelpfulClass]

  def execute(): Unit = {
    // Use the injected helpfulClass
    helpfulClass.doSomethingHelpful
  }
}

class HelpfulClass {
  def doSomethingHelpful = {
    println("I am doing something helpful")
  }
}