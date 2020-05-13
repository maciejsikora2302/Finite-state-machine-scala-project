package com.example

import java.util

import scala.io.Source

object ReadFile {
  def apply(fileName: String): ReadFile = {
    val filePath = getClass.getResource(fileName)
    var data = Array.fill(3) {
      Array.empty[String]
    }
    var state = -1

    for (line <- Source.fromURL(filePath).getLines) {
      if (line.equals("Terminals:"))state = 0
      else if (line.equals("Non terminals:"))state = 1
      else if (line.equals("Productions:"))state = 2
      else data(state)++= line.replaceAll("\\s", "").split(",")
    }
    data(0).foreach(e => if (e.length != 1) println("Not proper terminal: " + e))
    data(1).foreach(e => if (e.length != 1) println("Not proper non-terminal: " + e))
    new ReadFile(data(0), data(1), data(2))
  }
}

class ReadFile(terminals: Array[String], nonTerminals: Array[String], expressions: Array[String]) {
  def getProperData() = {
    (terminals.map(e => e.charAt(0)), nonTerminals.map(e => e.charAt(0)), expressions)
  }
}
