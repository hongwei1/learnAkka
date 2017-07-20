//http://blog.csdn.net/lovehuangjiaju/article/details/50437454
package blog.csdn.net

import akka.actor.{ActorLogging, ActorRef}

object Example_01 extends App{
  import akka.actor.Actor
  import akka.event.Logging
  import akka.actor.ActorSystem
  import akka.actor.Props
  
  class MyActor extends Actor {
    val log = Logging(context.system, this)
    
    def receive = {
      case "test" => log.info("received test")
      case _      => log.info("received unknown message")
    }
  }
  //创建ActorSystem对象
  val system = ActorSystem("MyActorSystem")
  //返回ActorSystem的LoggingAdpater
  val systemLog=system.log
  //创建MyActor,指定actor名称为myactor
  val myactor: ActorRef = system.actorOf(Props[MyActor], name = "myactor")
  
  systemLog.info("准备向myactor发送消息")
  //向myactor发送消息
  myactor!"test"
  myactor! 123
  
  //关闭ActorSystem，停止程序的运行
  system.terminate()
}

/*
 *定义Actor时混入ActorLogging
 */
object Example_02 extends App{
  import akka.actor.Actor
  import akka.actor.ActorSystem
  import akka.actor.Props
  
  class MyActor extends Actor with ActorLogging{
    def receive = {
      case "test" => log.info("received test")
      case _      => log.info("received unknown message")
    }
  }
  
  //创建ActorSystem对象
  val system = ActorSystem("MyActorSystem")
  //返回ActorSystem的LoggingAdpater
  val systemLog=system.log
  //创建MyActor，指定actor名称为myactor
  val myactor = system.actorOf(Props[MyActor], name = "myactor")
  
  systemLog.info("准备向myactor发送消息")
  //向myactor发送消息
  myactor!"test"
  myactor! 123
  
  //关闭ActorSystem，停止程序的运行
  system.terminate()
}

/*
 *创建Actor
 */
object Example_03 extends App{
  import akka.actor.Actor
  import akka.actor.ActorSystem
  import akka.actor.Props
  
  class MyActor extends Actor with ActorLogging{
    def receive = {
      case "test" => log.info("received test")
      case _      => log.info("received unknown message")
    }
  }
  
  val system = ActorSystem("MyActorSystem")
  val systemLog=system.log
  
  //下列两行代码编译可以通过，但运行时出抛出异常
  val  myActor=new MyActor //Actor can not use new !!!
  val myactor = system.actorOf(Props(myActor), name = "myactor")
  
  systemLog.info("准备向myactor发送消息")
  //向myactor发送消息
  myactor!"test"
  myactor! 123
  
  //关闭ActorSystem，停止程序的运行
  system.terminate()
}

/*
 *创建Actor,调用context.actorOf方法
 */
object Example_04 extends App{
  import akka.actor.Actor
  import akka.actor.ActorSystem
  import akka.actor.Props
  
  class FirstActor extends Actor with ActorLogging{
    //通过context.actorOf方法创建Actor.
    val secondActor = context.actorOf(Props[SecondActor], name = "SecondActor")
    def receive = {
      case x => secondActor ! x;log.info("received "+x)
    }
    
  }
  
  class SecondActor extends Actor with ActorLogging{
    val grandSon = context.actorOf(Props[ThirdActor], name = "ThirdActor")
    def receive = {
      case x  => grandSon ! x; log.info("received "+x)
    }
  }
  
  class ThirdActor extends Actor with ActorLogging{
    def receive = {
      case "test" => log.info("received test")
      case _      => log.info("received unknown message")
    }
  }
  // start a Actor system. 
  val system = ActorSystem("MyActorSystem")
  val systemLog=system.log
  
  //创建FirstActor对象
  val firstActor = system.actorOf(Props[FirstActor], name = "firstActor")
  
  systemLog.info("准备向myactor发送消息")
  //向myactor发送消息
  firstActor!"test"
  firstActor! 123
  //关闭ActorSystem，停止程序的运行
  system.terminate()
}