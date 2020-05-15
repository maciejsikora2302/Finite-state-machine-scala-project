package com.example

import akka.actor.Actor

object StatsActor{

}

class StatsActor extends Actor{
  var processedInts = 0
  var processedStrings = 0
  override def receive: Receive = {
    case s => println("Statistics stage: IAMALIVEHAHAHAHHA")
  }
}
