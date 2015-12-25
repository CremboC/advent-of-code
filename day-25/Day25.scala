import scala.annotation.tailrec

case class Point(y: Int, x: Int)

val search = Point(2981, 3075) // row, column; y, x
def nextCode(prev: Long): Long = prev * 252533L % 33554393L

@tailrec
def find(coord: Point, yBegin: Int, previousCode: Long): Long = {
  if (coord == search) previousCode
  else if (coord.x == yBegin) find(Point(yBegin + 1, 1), yBegin + 1, nextCode(previousCode))
  else find(Point(coord.y - 1, coord.x + 1), yBegin, nextCode(previousCode))
}

println(find(Point(1, 1), 1, 20151125))