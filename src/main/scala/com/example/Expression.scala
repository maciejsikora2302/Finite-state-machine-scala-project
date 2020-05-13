package com.example


object Expression {
  def apply(line: String): Expression = {
    if (line.length < 3 || !line.contains("->")) null
    else {
      new Expression(getFrom(line), getTo(line))
    }
  }

  def getFrom(line: String): Char = {
    var pos = line.indexOf("->")
    var from = line.slice(0, pos)
    from = from.replace(' ', '\0')
    if (from.length == 1) from.charAt(0) else throw new Exception
  }

  def getTo(line: String): String = {
    var pos = line.indexOf("->")
    var to = line.slice(pos + 2, line.length)
    to = to.replace(' ', '\0')
    to
  }


}

class Expression(from: Char, to: String, position: Int = 0) {

  def getFrom(): Char = this.from

  def getTo(): String = this.to

  override def toString: String = this.from + "->" + this.to
}
