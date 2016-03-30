package com.github.astonbitecode.di

object DIScope extends Enumeration {
  type value = Value
  val SINGLETON_LAZY = Value("singleton-lazy")
  val SINGLETON_EAGER = Value("singleton-eager")
  val PROTOTYPE = Value("prototype")
}