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
  private[di] val constructors = new HashMap[Class[_], Constructor]
  private[di] val singletons = new HashMap[Class[_], Any]
  private[di] val as = ActorSystem()
  private[di] val syncActor = as.actorOf(SyncActor.props(constructors, singletons))

  private def key[T](implicit classTag: ClassTag[T]): Class[_] = classTag.runtimeClass

  /**
   * Defines a constructor for a Class. Every time that the DI wants to create a new instance, it will use the constructor defined here
   * @param constructor A Function that takes no arguments and returns an instance of a Class
   */
  def defineConstructor[T: ClassTag](constructor: () => T): Future[Unit] = {
    val p = Promise[Unit]
    syncActor ! Add(key[T], constructor, p)
    p.future
  }

  def inject[T: ClassTag](): T = {
    val k = key[T]
    singletons.get(k) match {
      case Some(inst) => inst.asInstanceOf[T]
      case None => createSingleton(k)
    }
  }

  def inject[T: ClassTag](scope: DIScope.value): T = {
    val k = key[T]
    scope match {
      case DIScope.SINGLETON => {
        singletons.get(k) match {
          case Some(inst) => inst.asInstanceOf[T]
          case None => createSingleton(k)
        }
      }
      case DIScope.PROTOTYPE => createPrototype(k)
    }
  }

  private[di] def createSingleton[T: ClassTag](k: Class[_]): T = {
    val constructor = constructors.get(k).getOrElse(throw new RuntimeException("The service is not yet initialized"))
    val inst = constructor.apply()
    syncActor ! AddSingleton(k, inst)
    inst.asInstanceOf[T]
  }

  private[di] def createPrototype[T: ClassTag](k: Class[_]): T = {
    val constructor = constructors.get(k).getOrElse(throw new RuntimeException("The service is not yet initialized"))
    val inst = constructor.apply()
    inst.asInstanceOf[T]
  }

  private[di] class SyncActor(constructors: HashMap[Class[_], Constructor], singletons: HashMap[Class[_], Any]) extends Actor {
    override def receive: Receive = {
      case Add(k, v, p) => {
        constructors.put(k, v)
        p.success()
      }
      case AddSingleton(k, v) => singletons.put(k, v)
    }
  }

  private[di] object SyncActor {
    def props(c: HashMap[Class[_], Constructor], s: HashMap[Class[_], Any]): Props = Props(new SyncActor(c, s))
  }

  private[di]type Constructor = () => Any

  /**
   * Internal API: Add
   */
  private[di] case class Add(c: Class[_], constructor: Constructor, promise: Promise[Unit])
  private[di] case class AddSingleton(c: Class[_], instance: Any)
}
