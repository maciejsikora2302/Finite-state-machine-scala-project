package com.example


import akka.actor.{Actor, ActorRef, ActorSystem, Props}


import scala.concurrent.ExecutionContextExecutor
import scala.util.Random
import com.example.EvaluateActor._

object EvaluateActor {

  case class EvaluateStr(s: String, destination: ActorRef)

  case class EvaluateInt(value: Int, destination: ActorRef)

  case class CheckWordInGrammar(word: String, automata: Automata)

}

class EvaluateActor extends Actor {
  def receive = {
    case EvaluateStr(s, destination) => destination ! "What is it: " + s
    case EvaluateInt(value, destination) => destination ! value * 2
    case CheckWordInGrammar(word, automata) =>
      val result:Boolean = automata.checkWord(word)
      println(result)
  }
}


class MainHandler(system: ActorSystem) extends Actor {

  var gramatics: Array[Grammar] = Array.empty[Grammar]
  var automats: Array[Automata] = Array.empty[Automata]

  val statActor: ActorRef = system.actorOf(Props(new StatsActor))

  def receive: Receive = {
    case value: Int =>
      system.actorOf(Props(new EvaluateActor)) ! EvaluateInt(value, sender())
    case s: String =>
      system.actorOf(Props(new EvaluateActor())) ! EvaluateStr(s, sender())
    case grammar: Grammar =>
      gramatics :+= grammar
      automats :+= Automata(grammar)
    case (grammarID: Int, word: String) =>
      if (grammarID >= gramatics.length) sender() ! "That grammar ID doesn't exist\n"
      else {
        system.actorOf(Props(new EvaluateActor)) ! CheckWordInGrammar(word,automats(grammarID))
      }
  }

}

class GenerateRequestActor(mainHandler: ActorRef, id: Int) extends Actor {
  override def receive: Receive = {
    case s: String =>
      println("ID " + id + "  " + s)
      Thread.sleep(2000)
      mainHandler ! Random.nextInt(100)
    case value: Int =>
      println("ID " + id + "  " + value)
      println(value)
      Thread.sleep(2000)
      mainHandler ! Random.alphanumeric.take(value % 10).mkString
  }

}

object AkkaQuickstart extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
    val handler = system.actorOf(Props(new MainHandler(system)),"Handler")
    val generator = system.actorOf(Props(new GenerateRequestActor(handler,1)),"Generator")
    val generator2 = system.actorOf(Props(new GenerateRequestActor(handler,2)),"Generator2")
  //  generator ! "Test"
  //  Thread.sleep(1000)
  //  generator2 ! "Test2"
  val data = ReadFile("/Example.txt").getProperData()
  val grammar = Grammar(data._1, data._2, data._3)
  handler ! grammar
  handler ! (0,"ac")
  handler ! (0,"ab")
}
