//http://blog.csdn.net/lovehuangjiaju/article/details/50437454
package blog.csdn.net

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorLogging, ActorPath, ActorRef, OneForOneStrategy}

import scala.language.postfixOps
// the following code is just from Actor trait.
//class ExampleActor extends Actor {
//  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
//    case _: ArithmeticException      => Resume
//    case _: NullPointerException     => Restart
//    case _: IllegalArgumentException => Stop
//    case _: Exception                => Escalate
//  }
//  
//  def receive = {
//    // directly calculated reply
//    case Request(r)               => sender() ! calculate(r)
//    
//    // just to demonstrate how to stop yourself
//    case Shutdown                 => context.stop(self)
//    
//    // error kernel with child replying directly to 'sender()'
//    case Dangerous(r)             => context.actorOf(Props[ReplyToOriginWorker]).tell(PerformWork(r), sender())
//    
//    // error kernel with reply going through us
//    case OtherJob(r)              => context.actorOf(Props[ReplyToMeWorker]) ! JobRequest(r, sender())
//    case JobReply(result, orig_s) => orig_s ! result
//  }
//}

/*
 *Actor API: Hook方法
 */
object Example_05 extends App{
  import akka.actor.Actor
  import akka.actor.ActorSystem
  import akka.actor.Props
  
  
  class FirstActor extends Actor with ActorLogging{
    //通过context.actorOf方法创建Actor. 定义变量不初始化.
    var child:ActorRef = _
    
    //Hook方法，preStart()，Actor启动之前调用，用于完成初始化工作
    override def preStart(): Unit ={
      log.info("preStart() in FirstActor")
      //通过context上下文创建Actor
      child = context.actorOf(Props[SecondActor], name = "myChild") //这里初始化
    }
    def receive = {
      //向MyActor发送消息
      case x => child ! x;log.info("received "+x)
    }
    
    //Hook方法，postStop()，Actor停止之后调用
    override def postStop(): Unit = {
      log.info("postStop() in FirstActor")
    }
  }
  
  
  class SecondActor extends Actor with ActorLogging{
    //Hook方法，preStart()，Actor启动之前调用，用于完成初始化工作
    override def preStart(): Unit ={
      log.info("preStart() in MyActor")
    }
    def receive = {
      case "test" => log.info("received test")
      case _      => log.info("received unknown message")
    }
    
    //Hook方法，postStop()，Actor停止之后调用
    override def postStop(): Unit = {
      log.info("postStop() in MyActor")
    }
  }
  
  val system = ActorSystem("MyActorSystem")
  val systemLog=system.log
  
  //创建FirstActor对象
  val myactor = system.actorOf(Props[FirstActor], name = "firstActor")
  
  systemLog.info("准备向myactor发送消息")
  //向myactor发送消息
  myactor!"test"
  myactor! 123
  Thread.sleep(5000)
  //关闭ActorSystem，停止程序的运行
  system.terminate()
}



/*
 *Actor API:成员变量self及sender()方法的使用
 */
object Example_06 extends App{
  import akka.actor.Actor
  import akka.actor.ActorSystem
  import akka.actor.Props
  
  
  class FirstActor extends Actor with ActorLogging{
    //通过context.actorOf方法创建Actor
    var child:ActorRef = _
    
    override def preStart(): Unit ={
      log.info("preStart() in FirstActor")
      //通过context上下文创建Actor
      child = context.actorOf(Props[SecondActor], name = "secondActor")
    }
    def receive = {
      //向MyActor发送消息
      case x => child ! x;log.info("received "+x)
    }
    
    
  }
  
  
  class SecondActor extends Actor with ActorLogging{
    self!"message from self reference"
    def receive = {
      case "test" => log.info("received test");sender()!"message from MyActor"
      case "message from self reference"=>log.info("message from self refrence")
      case _      => log.info("received unknown message");
    }
    
  }
  
  val system = ActorSystem("MyActorSystem")
  val systemLog=system.log
  
  //创建FirstActor对象
  val firstActor = system.actorOf(Props[FirstActor], name = "firstActor")
  
  systemLog.info("准备向myactor发送消息")
  //向myactor发送消息
  firstActor!"test"
  firstActor! 123
  Thread.sleep(5000)
  //关闭ActorSystem，停止程序的运行
  system.terminate()
}

/*
*Actor API:unhandled方法的使用
*/
object Example_051 extends App{
  import akka.actor.Actor
  import akka.actor.ActorSystem
  import akka.actor.Props
  
  
  class FirstActor extends Actor with ActorLogging{
    def receive = {
      //向MyActor发送消息
      case "test" => log.info("received test")
    }
    
    //重写unhandled方法
    override def unhandled(message: Any): Unit = {
      log.info("unhandled message is {}",message)
    }
  }
  
  
  
  val system = ActorSystem("MyActorSystem")
  val systemLog=system.log
  
  //创建FirstActor对象
  val myactor = system.actorOf(Props[FirstActor], name = "firstActor")
  
  systemLog.info("准备向myactor发送消息")
  //向myactor发送消息
  myactor!"test"
  myactor! 123
  Thread.sleep(5000)
  //关闭ActorSystem，停止程序的运行
  system.terminate()
}

/*
*Actor API:成员变量self及sender()方法的使用
*/
object Example_07 extends App{
  import akka.actor.Actor
  import akka.actor.ActorSystem
  import akka.actor.Props
  
  class FirstActor extends Actor with ActorLogging{
    //通过context.actorOf方法创建Actor
    var child:ActorRef = _
    override def preStart(): Unit ={
      log.info("preStart() in FirstActor")
      //通过context上下文创建Actor
      child = context.actorOf(Props[MyActor], name = "myActor")
    }
    def receive = {
      //向MyActor发送消息
      case x => child ! x;log.info("received "+x)
    }
  }
  
  class MyActor extends Actor with ActorLogging{
    var parentActorRef:ActorRef=_
    override def preStart(): Unit ={
      //通过context.parent获取其父Actor的ActorRef
      parentActorRef=context.parent
    }
    def receive = {
      case "test" => log.info("received test");parentActorRef!"message from ParentActorRef"
      case _      => log.info("received unknown message");
    }
    
  }
  val system = ActorSystem("MyActorSystem")
  val systemLog=system.log
  
  //创建FirstActor对象
  val myactor = system.actorOf(Props[FirstActor], name = "firstActor")
  //获取ActorPath
  val myActorPath: ActorPath =system.child("firstActor")
  //通过system.actorSelection方法获取ActorRef
  val myActor1=system.actorSelection(myActorPath)
  systemLog.info("准备向myactor发送消息")
  //向myActor1发送消息
  myActor1!"test"
  myActor1! 123
  Thread.sleep(5000)
  //关闭ActorSystem，停止程序的运行
  system.terminate()
}

/*
*ActorPath
*/
object Example_08 extends App{
  import akka.actor.Actor
  import akka.actor.ActorSystem
  import akka.actor.Props
  
  class FirstActor extends Actor with ActorLogging{
    //通过context.actorOf方法创建Actor
    var child:ActorRef = _
    override def preStart(): Unit ={
      log.info("preStart() in FirstActor")
      //通过context上下文创建Actor
      child = context.actorOf(Props[MyActor], name = "myActor")
    }
    def receive = {
      //向MyActor发送消息
      case x => child ! x;log.info("received "+x)
    }
  }
  
  class MyActor extends Actor with ActorLogging{
    def receive = {
      case "test" => log.info("received test");
      case _      => log.info("received unknown message");
    }
    
  }
  val system = ActorSystem("MyActorSystem")
  val systemLog=system.log
  
  //创建FirstActor对象
  val firstactor = system.actorOf(Props[FirstActor], name = "firstActor")
  
  //获取ActorPath
  val firstActorPath=system.child("firstActor")
  systemLog.info("firstActorPath--->{}",firstActorPath)
  
  
  //通过system.actorSelection方法获取ActorRef
  val myActor1=system.actorSelection(firstActorPath)
  
  //直接指定其路径
  val myActor2=system.actorSelection("/user/firstActor")
  //使用相对路径
  val myActor3=system.actorSelection("../firstActor")
  
  
  systemLog.info("准备向myactor发送消息")
  //向myActor1发送消息
  myActor2!"test"
  myActor2! 123
  Thread.sleep(5000)
  //关闭ActorSystem，停止程序的运行
  system.terminate()
}