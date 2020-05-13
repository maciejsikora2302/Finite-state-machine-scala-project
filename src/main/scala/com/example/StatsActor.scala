package com.example

import akka.actor.Actor

object StatsActor{

}

class StatsActor extends Actor{
  override def receive: Receive = {
    case s:String => println("Whata")
  }
}
