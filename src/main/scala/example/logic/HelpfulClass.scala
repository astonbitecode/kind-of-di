package example.logic

import org.astonbitecode.kindof.di._
import example.service.ConfigurationService

class HelpfulClass {
  private val config = inject[ConfigurationService]

  def doSomethingUsingTheConfiguration = {
    println(config.getFilePath())
  }
}