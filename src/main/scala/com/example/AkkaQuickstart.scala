package com.example


import akka.actor.{Actor, ActorRef, ActorSystem, Props}


import scala.concurrent.ExecutionContextExecutor
import scala.util.Random

class IntActorProcess(stats: ActorRef) extends Actor {
  def receive: Receive = {
    case value: Int =>
      println("Process stage: I'm a IntActorProcess and I'm working on value (" + value.toString + "). Here are some math operations on it: "
        + 2*value + ", "
        + value / 2 + ", "
        + math.sqrt(value) + ", "
        + value*value + "."
      )
      stats ! 1
  }
}

class WordActorProcess extends Actor{
  def receive = {
    case (word: String, automata: Automata) =>
      val result:Boolean = automata.checkWord(word)
      println("Process stage: I'm a WordActor and I have got word (" + word + "). Here is a result of word check: " + result)
  }
}


class MainHandler(system: ActorSystem) extends Actor {
  var gramatics: Array[Grammar] = Array.empty[Grammar]
  var automats: Array[Automata] = Array.empty[Automata]

  val statActor: ActorRef = system.actorOf(Props(new StatsActor))

  def receive: Receive = {
    case value: Int =>
      system.actorOf(Props(new IntActorProcess(statActor))) ! value
    case grammar: Grammar =>
      gramatics :+= grammar
      automats :+= Automata(grammar)
    case (grammarID: Int, word: String) =>
      if (grammarID >= gramatics.length) sender() ! "That grammar ID doesn't exist\n"
      else {
        statActor ! word
        system.actorOf(Props(new WordActorProcess)) ! (word,automats(grammarID))
      }
  }

}

class GenerateRequestActor(mainHandler: ActorRef, id: Int) extends Actor {
  override def receive: Receive = {
    case "Start" =>
      println("Generate stage: Generator(" + id + ")  is starting to create traffic")
      val rand = scala.util.Random
      while(true){
        val probability = rand.nextInt(100)
        if (probability % 2 == 0){
          var word = (97 + rand.nextInt(3)).asInstanceOf[Char].toString //word now consists of letter "a" "b" or "c"
          word += (97 + rand.nextInt(3)).asInstanceOf[Char].toString //adding at the end "a" "b" or "c" so that we can have a possibility of a word that is accepted by grammar
          val sleepTime = rand.nextInt(3000)
          println("Generate stage: Generator(" + id + ") generated random word ("+ word + ") for grammar. Going to sleep for " + sleepTime + " seconds.")
          mainHandler ! (0,word)
          Thread.sleep(sleepTime)
        }else{
          val i = rand.nextInt(1000)
          val sleepTime = rand.nextInt(3000)
          println("Generate stage: Generator(" + id + ") generated random int (" + i + ") for processing. Going to sleep for " + sleepTime + " seconds.")
          mainHandler ! i
          Thread.sleep(sleepTime)
        }
      }
  }
}



object AkkaQuickstart extends App {

  val numberOfActors = 6

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  val handler = system.actorOf(Props(new MainHandler(system)),"Handler")
  val generator = system.actorOf(Props(new GenerateRequestActor(handler,1)),"Generator")
  val data = ReadFile("/Example.txt").getProperData()
  val grammar = Grammar(data._1, data._2, data._3)
  handler ! grammar

  Thread.sleep(1000) //sleep for 1s before starting generating traffic

  for( i <- 1 to numberOfActors){
    system.actorOf(Props(new GenerateRequestActor(handler, i))) ! "Start"
  }
}
