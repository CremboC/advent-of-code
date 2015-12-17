import scala.io.Source
import scala.util.matching.Regex

/**
  * Created by paulius on 17/12/2015.
  */

object Day2 {

  def surface(l: Int, w: Int, h: Int) = 2 * l * w + 2 * w * h + 2 * h * l
  def ribbon(l: Int, w: Int, h: Int) = {
    val s = List(l, w, h).sorted
    s.head * 2 + s(1) * 2 + (l * w * h)
  }

  def main(args: Array[String]) {
    val contents = Source.fromFile("input.data").getLines()
    val r: Regex = """(\d+)x(\d+)x(\d+)""".r
    val total: (Int, Int) = contents.foldLeft((0, 0))((carry, str) => {
      str match {
        case r(l, w, h) =>
          val li = l.toInt
          val wi = w.toInt
          val hi = h.toInt
          (carry._1 + surface(li, wi, hi) + List(li * wi, wi * hi, hi * li).min, carry._2 + ribbon(li, wi, hi))
      }
    })

    println(s"Star one: ${total._1}")
    println(s"Star two: ${total._2}")
  }
}