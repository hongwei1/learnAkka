//http://doc.akka.io/docs/akka/2.5.3/scala/guide/tutorial_1.html
package com.lightbend.akka.sample

/**
  * Created by zhanghongwei on 10/07/2017.
  */

import akka.actor.{Actor, ActorSystem, Props}

//1 http://doc.akka.io/docs/akka/2.5.3/scala/guide/tutorial_1.html#structure-of-an-actorref-and-paths-of-actors
object tutorial_1_1 extends App {
  val system: ActorSystem = ActorSystem()

    class PrintMyActorRefActor extends Actor {

      override def receive: Receive = {
        case "printit" =>
          val secondRef = context.actorOf(Props.empty, "second-actor")
          println(s"Second: $secondRef")
      }
    }
    //Since all actor references are valid URLs
    //akka://default/user/first-actor#1716707940
    val firstRef = system.actorOf(Props[PrintMyActorRefActor], "first-actor")

    println(s"First : $firstRef")
    firstRef ! "printit"
}

//2  http://doc.akka.io/docs/akka/2.5.3/scala/guide/tutorial_1.html#hierarchy-and-lifecycle-of-actors
object tutorial_1_2 extends App {
  val system: ActorSystem = ActorSystem()
  class StartStopActor1 extends Actor {
    
    override def preStart(): Unit = {
      println("first started")
      context.actorOf(StartStopActor2.props, "second")
    }
    override def postStop(): Unit = println("first stopped")
    
    override def receive: Receive = {
      case "stop" => context.stop(self)
    }
  }
  
  object StartStopActor1{
    def props: Props = Props[StartStopActor1]
  }
  
  class StartStopActor2 extends Actor {
    
    override def preStart(): Unit = println("second started")
    override def postStop(): Unit = println("second stopped")
    
    // Actor.emptyBehavior is a useful placeholder when we don't
    // want to handle any messages in the actor.
    override def receive: Receive = Actor.emptyBehavior
  }
  
  object StartStopActor2{
    def props: Props = Props[StartStopActor2]
  }
  
  val first = system.actorOf(StartStopActor1.props, "first")
  first ! "stop"
  
}



// 3 http://doc.akka.io/docs/akka/2.5.3/scala/guide/tutorial_1.html#hierarchy-and-failure-handling-supervision-
object tutorial_1_3 extends App {
  
  val system: ActorSystem = ActorSystem()
  
  class SupervisingActor extends Actor {
    val child = context.actorOf(Props[SupervisedActor], "supervised-actor")
    
    override def receive: Receive = {
      case "failChild" => 
        child ! "fail"
    }
  }
  
  class SupervisedActor extends Actor {
    override def preStart(): Unit = println("supervised actor started")
    override def postStop(): Unit = println("supervised actor stopped")
    
    override def receive: Receive = {
      case "fail" =>
        println("supervised actor fails now")
        throw new Exception("I failed!")
    }
  }
  
  val supervisingActor = system.actorOf(Props[SupervisingActor], "supervising-actor")
  supervisingActor ! "failChild"
  
}

//4 http://doc.akka.io/docs/akka/2.5.3/scala/guide/tutorial_1.html#the-first-actor
import akka.actor.{ Actor, ActorLogging, Props }

object IotSupervisor {
  def props(): Props = Props(new IotSupervisor)
}

class IotSupervisor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("IoT Application started")
  override def postStop(): Unit = log.info("IoT Application stopped")
  
  // No need to handle any messages
  override def receive = Actor.emptyBehavior
}

import akka.actor.ActorSystem
import scala.io.StdIn

object IotApp {
  
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("iot-system")
    
    try {
      // Create top level supervisor
      val supervisor = system.actorOf(IotSupervisor.props(), "iot-supervisor")
      // Exit the system after ENTER is pressed
      StdIn.readLine()
    } finally {
      system.terminate()
    }
  }
  
}