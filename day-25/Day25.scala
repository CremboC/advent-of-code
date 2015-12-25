import scala.annotation.tailrec

class Point(val y: Int, val x: Int) {
  override def equals(other: Any): Boolean = other match {
    case that: Point => y == that.y && x == that.x
    case _ => false
  }
}

val search = new Point(2981, 3075) // row, column; y, x
def nextCode(prev: Long): Long = prev * 252533L % 33554393L

@tailrec
def find(coord: Point, yBegin: Int, previousCode: Long): Long = {
  if (coord == search) previousCode
  else {
    if (coord.x == yBegin) find(new Point(yBegin + 1, 1), yBegin + 1, nextCode(previousCode))
    else find(new Point(coord.y - 1, coord.x + 1), yBegin, nextCode(previousCode))
  }
}

println(find(new Point(1, 1), 1, 20151125))