package com.lightbend.akka.sample

/**
  * Created by zhanghongwei on 10/07/2017.
  */
import akka.actor.{Actor, Props}
import akka.pattern.ask

import scala.concurrent.Await

//class ExampleActor extends Actor {
//  val other = context.actorOf(Props[OtherActor], "childName") // will be destroyed and re-created upon restart by default
//  
//  def receive {
//    case Request1(msg) => other ! refine(msg)     // uses this actor as sender reference, reply goes to us
//    case Request2(msg) => other.tell(msg, sender()) // forward sender reference, enabling direct reply
//    case Request3(msg) =>
//    implicit val timeout = Timeout(5.seconds)
//    (other ? msg) pipeTo sender()
//    // the ask call will get a future from other's reply
//    // when the future is complete, send its value to the original sender
//  }
//}
