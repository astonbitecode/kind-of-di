package example.logic

import org.astonbitecode.kindof.di._

class AnotherHalpfulClass {
  private val helpfulClass = inject[HelpfulClass]

  def doSomethingUsingTheHelpfulClass = {
    print("AnotherHelpfulClass uses the injected HelpfulClass: ")
    helpfulClass.doSomethingUsingTheConfiguration
  }
}