package com.example

import scala.annotation.tailrec
import scala.collection.mutable


object Automata {
  def apply(grammar: Grammar): Automata = {
    var result = new Automata()
    grammar.getProductions().keySet.foreach(key => result.states.+=((key, new State(key))))
    result.states.+=(('\0', new State('\0')))
    result.states.keySet.filter(k => k != '\0').foreach(
      key => {
        val products = grammar.getProductions()(key)
        products.foreach(elem => {
          val terminals = elem.getTo().filter(p => grammar.getTerminals().contains(p))
          val stateChar = if (elem.getTo().length == terminals.length) '\0'
          else elem.getTo().charAt(terminals.length)
          result.states(key).setNeigbour(terminals, result.states(stateChar))
        })
      })
    result
  }
}

class Automata() {
  var states: mutable.HashMap[Char, State] = mutable.HashMap.empty[Char, State]

  def checkWord(word: String): Boolean = {
    var letters = 0
    var pos = 0

    def nextStage(pos: Int = 0, state: State= states('S')): Boolean = {
//      println(state.getID())
      if (state.getID() == '\0' && pos == word.length) true
      else {
        val nonCompleteWordPart = word.slice(pos, word.length)
        var flag = false
        state.getNeighboursSet().foreach(terminals => {
          if (nonCompleteWordPart.contains(terminals))
            flag = if (nextStage(pos + terminals.length, state.getNeighbour(terminals))) true else false
        })
        flag
      }
    }
    nextStage()
  }
}


class State(id: Char,
            neighbours: mutable.HashMap[String, State] = mutable.HashMap.empty[String, State]) {

  def getID()= id

  def setNeigbour(terminal: String, state: State): Unit = {
    neighbours(terminal) = state
  }

  def getNeighbour(terminals: String)=neighbours(terminals)

  def getNeighboursSet() = {
    neighbours.keySet
  }
}