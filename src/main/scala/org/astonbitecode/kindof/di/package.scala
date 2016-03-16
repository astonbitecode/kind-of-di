package org.astonbitecode.kindof

import scala.reflect.ClassTag
import scala.collection.concurrent.{ Map, TrieMap }
import scala.collection.mutable.HashMap
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import scala.concurrent.Promise
import scala.concurrent.Future

package object di {
  private[di] val cache = new HashMap[Class[_], DiElement]
  private val as = ActorSystem()
  private val syncActor = as.actorOf(SyncActor.props(cache))

  private def key[T](implicit classTag: ClassTag[T]): Class[_] = classTag.runtimeClass

  /**
   * Defines a constructor for a Class. Every time that the DI wants to create a new instance, it will use the constructor defined here
   * @param constructor A Function that takes no arguments and returns an instance of a Class
   * @param scope The scope that the Objects of the Class will be created for
   */
  def diDefine[T: ClassTag](constructor: () => T, scope: DIScope.value = DIScope.SINGLETON_EAGER): Future[Unit] = {
    val p = Promise[Unit]
    syncActor ! Add(key[T], DiElement(constructor, scope), p)
    p.future
  }

  def inject[T: ClassTag](): T = {
    val k = key[T]
    cache.get(k) match {
      case Some(diElement) => getOrCreateInstance(k)
      case None => throw new RuntimeException(s"No constructor found for $k")
    }
  }

  private[di] def getOrCreateInstance[T: ClassTag](k: Class[_]): T = {
    val diElement = cache.get(k).getOrElse(throw new RuntimeException(s"No constructor found for $k"))
    val inst = diElement.scope match {
      case DIScope.PROTOTYPE => diElement.constructor.apply()
      case _: DIScope.value => {
        diElement.cachedInstance match {
          case Some(i) => i
          case None => {
            val i = diElement.constructor.apply()
            syncActor ! AddSingleton(k, i)
            i
          }
        }
      }
    }

    inst.asInstanceOf[T]
  }

  private class SyncActor(c: HashMap[Class[_], DiElement]) extends Actor {
    override def receive: Receive = {
      case Add(k, v, p) => {
        val diElement = v.scope match {
          case DIScope.SINGLETON_EAGER => v.copy(cachedInstance = Some(v.constructor.apply()))
          case _ => v
        }
        c.put(k, diElement)
        p.success()
      }
      case AddSingleton(k, v) => {
        val diElement = c.getOrElse(k, throw new RuntimeException(s"No constructor found for $k"))
        val newDiElement = diElement.copy(cachedInstance = Some(v))
        c.put(k, newDiElement)
      }
    }
  }

  private object SyncActor {
    def props(de: HashMap[Class[_], DiElement]): Props = Props(new SyncActor(de))
  }

  private type Constructor = () => Any

  private[di] case class DiElement(constructor: Constructor, scope: DIScope.value, cachedInstance: Option[Any] = None)

  /**
   * Internal API: Add
   */
  private case class Add(c: Class[_], element: DiElement, promise: Promise[Unit])
  private case class AddSingleton(c: Class[_], instance: Any)

}
