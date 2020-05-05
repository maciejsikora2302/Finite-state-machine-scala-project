package com.example


import akka.actor.{Actor, ActorRef, ActorSystem, Props}


import scala.concurrent.ExecutionContextExecutor
import scala.util.Random

object EvaluateActor {
  case class EvaluateStr(s:String,destination: ActorRef)
  case class EvaluateInt(value:Int,destination: ActorRef)
}

class EvaluateActor extends Actor{
  import EvaluateActor._
  def receive ={
    case EvaluateStr(s,destination) => destination ! "What is it: "+s
    case EvaluateInt(value,destination) => destination ! value*2
  }

}


class MainHandler(system: ActorSystem) extends Actor {
  import com.example.EvaluateActor._
  def receive: Receive = {
    case value: Int =>
      system.actorOf( Props(new EvaluateActor)) ! EvaluateInt(value,sender())
    case s: String =>
      system.actorOf(Props(new EvaluateActor())) ! EvaluateStr(s,sender())
  }

}

class GenerateRequestActor(mainHandler: ActorRef,id: Int) extends Actor {
  override def receive: Receive = {
    case s:String =>
      println("ID "+id+"  "+s)
      Thread.sleep(2000)
      mainHandler !  Random.nextInt(100)
    case value:Int =>
      println("ID "+id+"  "+value)
      println(value)
      Thread.sleep(2000)
      mainHandler ! Random.alphanumeric.take(value%10).mkString
  }

}

object AkkaQuickstart extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  val handler = system.actorOf(Props(new MainHandler(system)),"Handler")
  val generator = system.actorOf(Props(new GenerateRequestActor(handler,1)),"Generator")
  val generator2 = system.actorOf(Props(new GenerateRequestActor(handler,2)),"Generator2")
  generator ! "Test"
  Thread.sleep(1000)
  generator2 ! "Test2"


}
