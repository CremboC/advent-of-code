import scala.io.Source

/**
  * Created by paulius on 18/12/2015.
  */

object Day18 {

  def checkNeighbors(state: Array[Array[Char]], y: Int, x: Int): Int = {
    val s = new StringBuilder()
    for (ii <- (y - 1) to (y + 1); jj <- (x - 1) to (x + 1); if !(ii == y) || !(jj == x)) {
      s.append(state(ii)(jj))
    }
    s.count(_ == '#')
  }

  def nextState(state: Array[Array[Char]]): Array[Array[Char]] = {
    val newState = state.map(_.clone)
    for ((row, y) <- state.view.zipWithIndex; (light, x) <- row.view.zipWithIndex; if light == '#' || light == '.') {
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
    val grid = Array.ofDim[Char](contents.length, contents.length + 1)

    for ((l, i) <- contents.view.zipWithIndex) {
      grid.update(i, l.toCharArray)
    }

    val lightsOn: Int = (1 to 100).foldLeft(grid)((carry, index) => {
      nextState(carry)
    }).flatten.count(_ == '#')
    println(s"Lights On: $lightsOn")
  }
}
