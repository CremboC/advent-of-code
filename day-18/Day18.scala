import scala.io.Source

/**
  * Created by paulius on 18/12/2015.
  */

object Day18 {

  val size = 100

  def checkNeighbors(state: Array[Array[Char]], y: Int, x: Int): Int = {
    val s = new StringBuilder()
    for (ii <- (y - 1) to (y + 1); jj <- (x - 1) to (x + 1); if !(ii == y) || !(jj == x)) {
      s.append(state(ii)(jj))
    }
    s.count(_ == '#')
  }

  def edge(x: Int, y: Int): Boolean =
    if (x == 1 && y == 1) true
    else if (x == 1 && y == size) true
    else if (x == size && y == 1) true
    else if (x == size && y == size) true
    else false

  def nextState(state: Array[Array[Char]]): Array[Array[Char]] = {
    val newState = state.map(_.clone)
    for ((row, y) <- state.zipWithIndex;
         (light, x) <- row.zipWithIndex
         if light == '#' || light == '.'
         if !edge(x, y)
    ) {
      val lightsOn = checkNeighbors(state, y, x)
      light match {
        case '#' if lightsOn < 2 || lightsOn > 3 => newState(y)(x) = '.'
        case '.' if lightsOn == 3 => newState(y)(x) = '#'
        case _ => // do nothing
      }
    }
    newState
  }

  def printState(state: Array[Array[Char]]) = {
    for (y <- state) {
      for (x <- y) {
        print(x)
      }
      println()
    }
  }

  def main(args: Array[String]) {
    val contents: Array[String] = Source.fromFile("input.data").getLines.toArray
    val grid = Array.ofDim[Char](contents.length, contents.length)

    for ((l, i) <- contents.zipWithIndex) {
      grid.update(i, l.toCharArray)
    }

    grid(1)(1) = '#'
    grid(1)(size) = '#'
    grid(size)(1) = '#'
    grid(size)(size) = '#'

    val lightsOn: Int = (1 to 100).foldLeft(grid)((carry, index) => {
      nextState(carry)
    }).flatten.count(_ == '#')
    println(s"Lights On: $lightsOn")
  }
}
