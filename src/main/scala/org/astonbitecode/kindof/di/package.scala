package org.astonbitecode.kindof

import scala.reflect.ClassTag
import scala.collection.concurrent.{ Map, TrieMap }

package object di {
  private var readyToInject: Map[Class[_], Any] = TrieMap.empty
  private def key[T](implicit classTag: ClassTag[T]): Class[_] = classTag.runtimeClass

  def initService[T: ClassTag](c: T) {
    val k = key[T]
    readyToInject.get(k) match {
      case Some(initialized) => throw new RuntimeException("The service already initialized")
      case None => readyToInject = readyToInject += (k -> c)
    }
  }

  def inject[T: ClassTag]: T = {
    readyToInject.get(key[T]).getOrElse(throw new RuntimeException("The service is not yet initialized")).asInstanceOf[T]
  }
}