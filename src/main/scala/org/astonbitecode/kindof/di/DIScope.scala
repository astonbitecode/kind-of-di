package org.astonbitecode.kindof.di

object DIScope extends Enumeration {
  type value = Value
  val SINGLETON = Value("singleton")
  val PROTOTYPE = Value("prototype")
}