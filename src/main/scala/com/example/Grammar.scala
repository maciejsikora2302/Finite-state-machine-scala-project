package com.example

import java.util

import scala.collection.mutable

object Grammar {
  def apply(terminals: Array[Char], nonTerminals: Array[Char], expressions: Array[String]): Grammar = {
    var array = expressions.map({
      exp => Expression(exp)
    })

    array.foreach({
      exp => {
        if (!nonTerminals.contains(exp.getFrom()))
          println("This non terminal isn't declared: " + exp.getFrom())
        exp.getTo().foreach({
          term =>
            if (!terminals.contains(term) && !nonTerminals.contains(term))
              println("This term is not included in this gramathich") else None
        })
      }
    })

    val map = getHashMap(array)
    new Grammar(terminals, map)
  }


  def getHashMap(arrayExpression: Array[Expression])
  : mutable.HashMap[Char, Array[Expression]] = {
    var map = new mutable.HashMap[Char, Array[Expression]]
    arrayExpression.foreach(
      exp => {
        if (map.contains(exp.getFrom())) {
          var set = map(exp.getFrom())
          set :+= exp
          map.update(exp.getFrom(), set)
        }
        else map.+=((exp.getFrom(), Array(exp)))
      }
    )
    map
  }

}

class Grammar(terminals: Array[Char], productions: mutable.HashMap[Char, Array[Expression]]) {

  override def toString: String = {
    "Terminals: " + terminals.mkString(",") + "\nProductions: \n" +
      productions.values.map(arr => arr.map(e => e.toString).mkString("\n")).mkString("\n")
  }

  def getProductions() = { productions}
  def getTerminals() = { terminals}

}
