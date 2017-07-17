//#full-example
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.lightbend.akka.sample.tutorial_2_2.Device

//#test-classes
class Tutorial_2_2Test(_system: ActorSystem)
  extends TestKit(_system) //only this is from Akka
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterAll {
  //#test-classes
  
  def this() = this(ActorSystem("tutorial"))
  
  override def afterAll: Unit = {
    shutdown(system)
  }
  
  it should "reply with empty reading if no temperature is known" in {
    val probe = TestProbe()
    val deviceActor = system.actorOf(Device.props("group", "device"))
  
    deviceActor.tell(Device.ReadTemperature(requestId = 42), probe.ref)
    val response = probe.expectMsgType[Device.RespondTemperature]
    response.requestId should ===(42)
    response.value should ===(None)
  }
}
