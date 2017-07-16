//#full-example
//http://developer.lightbend.com/guides/akka-quickstart-scala/?_ga=2.44314850.759731059.1500229203-811310199.1487624284#what-hello-world-does
package com.lightbend.akka.sample

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import scala.io.StdIn

//#greeter-companion
//#greeter-messages
//1 It is a good practice to put an actor’s associated messages in its companion object.
//  This makes it easier to understand what type of messages the actor expects and handles.
//2 It is also a common pattern to use a props method in the companion object that describes how to construct the Actor.
//3 Messages should be immutable, since they are shared between different threads.
//4 Since messages are the Actor’s public API, it is a good practice to define messages 
//   with good names and rich semantic and domain specific meaning, 
//   even if they just wrap your data type. This will make it easier to use, 
//   understand and debug actor-based systems


object Greeter {
  
  //#greeter-messages
  /**
    * This method, will create the new Greeter Actor.
    * @param message
    * @param printerActor
    * @return
    */
  def props(
    message: String,
    printerActor: ActorRef
  ): Props = Props(new Greeter(message, printerActor))
  
  //#greeter-messages
  final case class WhoToGreet(who: String)
  
  case object Greet
  
}

//#greeter-messages
//#greeter-companion

//#greeter-actor
class Greeter(
  message: String,
  printerActor: ActorRef
) extends Actor {
  
  import Greeter._
  import Printer._
  
//  The greeting variable contains the Actor’s state and is set to "" by default.
  // the string value can be setting during the first  case WhoToGreet(who), and 
  // later it will send the message to printerActor.
  var greeting = ""
  
  def receive = {
    case WhoToGreet(who) => // This receive is to store message internally
      greeting = s"$message, $who"
    case Greet => // This will send message to printerActor
      //#greeter-send-message
      printerActor ! Greeting(greeting)
    //#greeter-send-message
  }
}

//#greeter-actor

//#printer-companion
//#printer-messages
object Printer {
  
  //#printer-messages
  //Props.apply --> will be a function that creates an instance of the supplied type using the default constructor.
  def props: Props = Props[Printer]
  
  //#printer-messages
  final case class Greeting(greeting: String)
  
}

//#printer-messages
//#printer-companion

//#printer-actor
// Name is Printer
class Printer extends Actor with ActorLogging {
  
  import Printer._
  
  def receive = {
    case Greeting(greeting) => // When Printer Actor get the Greeting message, it will log it .
      log.info(s"Greeting received (from ${sender() }): $greeting")
  }
}

//#printer-actor

//#main-class
object AkkaQuickstart extends App {
  
  import Greeter._
  
  // ActorSystem acts as a container for Actors and manages their life-cycles
  // Create the 'helloAkka' actor system
  val system: ActorSystem = ActorSystem("helloAkka")
  // Scala
  
  try {
    //#create-actors
    // Create 1 printer actor
    //actorOf, two paramters: props() + name
    //Pros--> will be a function that creates an instance of the supplied type using the default constructor.
    //You can not new a actor, you can only get it from Factory, and only get a reference.
    val printer: ActorRef = system.actorOf(Printer.props, "printerActor")
    
    // Create the 3 'greeter' actors
    //new Greeter(message, printerActor), the Greeter will have the printActor, it can be looked for later, when search for the Actor
    val howdyGreeter: ActorRef = system.actorOf(Greeter.props("Howdy", printer), "howdyGreeter")
    val helloGreeter: ActorRef = system.actorOf(Greeter.props("Hello", printer), "helloGrhowdyGreetereeter")
    val goodDayGreeter: ActorRef = system.actorOf(Greeter.props("Good day", printer), "goodDayGreeter")
    //#create-actors
    
    system.actorSelection("")
    //#main-send-messages
    // sends messages to the Greeter Actor instances, which store them internally.
    howdyGreeter ! WhoToGreet("Akka") 
    // Finally, instruction messages to the Greeter Actors trigger them to send messages to the Printer Actor
    howdyGreeter ! Greet
    
    howdyGreeter ! WhoToGreet("Lightbend")
    howdyGreeter ! Greet

    helloGreeter ! WhoToGreet("Scala")
    helloGreeter ! Greet

    goodDayGreeter ! WhoToGreet("Play")
    goodDayGreeter ! Greet
//    #main-send-messages
    
    println(">>> Press ENTER to exit <<<")
    StdIn.readLine()
  } finally {
    system.terminate()
  }
}

//#main-class  
//#full-example
