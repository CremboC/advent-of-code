import scala.io.Source

/**
  * Created by paulius on 17/12/2015.
  */

object Day3 {

  def move(x: Int, y: Int, pos: (Int, Int)): (Int, Int)  = (pos._1 + x, pos._2 + y)

  def main(args: Array[String]) {
    val contents = Source.fromFile("input.data").mkString
    val grid = Array.ofDim[Boolean](10000, 10000)
    var pos = (10000 / 2, 10000 / 2)

    grid(pos._1)(pos._1) = true

    contents.foreach {
      case 'v' =>
        pos = move(0, -1, pos)
        grid(pos._1)(pos._2) = true
      case '>' =>
        pos = move(1, 0, pos)
        grid(pos._1)(pos._2) = true
      case '<' =>
        pos = move(-1, 0, pos)
        grid(pos._1)(pos._2) = true
      case '^' =>
        pos = move(0, 1, pos)
        grid(pos._1)(pos._2) = true
    }

    var visited: Int = 0
    for (x <- grid; y <- x; if y) {
      visited += 1
    }

    println(s"Star one: $visited")

  }
}
