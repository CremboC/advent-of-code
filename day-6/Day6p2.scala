import scala.collection.immutable.Queue
import scala.io.Source

/**
  * Created by paulius on 26/12/2015.
  */

val f = Source.fromFile("input.data").getLines.toVector
val lines = Queue(f: _*)
val pr = """(?:\w+ )?(\w+) (\d+),(\d+) through (\d+),(\d+)""".r
var grid = Array.ofDim[Int](1000, 1000)

def loop(lines: Queue[String]): Array[Array[Int]] = {
  if (lines.isEmpty) grid
  else {
    val (line, next) = lines.dequeue
    line match {
      case pr(action, sx, sy, ex, ey) =>
        for (x <- sx.toInt to ex.toInt; y <- sy.toInt to ey.toInt) {
          action match {
            case "toggle" => grid(x)(y) += 2
            case "on" => grid(x)(y) += 1
            case "off" => grid(x)(y) = math.max(grid(x)(y) - 1, 0)
          }
        }
    }

    loop(next)
  }
}

println(loop(lines).flatten.sum)