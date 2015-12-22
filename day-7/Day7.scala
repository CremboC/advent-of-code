import scala.io.Source
import scala.util.matching.Regex

/**
  * Created by paulius on 20/12/2015.
  */

object Day7 {
  type op = (Option[Left], Option[String], Option[String], String)
  def main(args: Array[String]) {
    val lines: List[String] = Source.fromFile("test.data").getLines.toList
    val regex: Regex = """.*(AND|OR|NOT|LSHIFT|RSHIFT).*""".r

    lines.map { l =>
      val op = l match {
        case regex(operator) => operator
        case _ => ""
      }

      val split: Array[String] = l.split(" ")
      op match {
        case "AND" => (Some(split.head), op, Some(split(2)), split.last)
        case "OR" =>
        case "NOT" =>
        case "" =>
      }
    } foreach println
  }
}

trait Left extends Any
case class LString(v: String) extends Left
case class LInt(v: Int) extends Left

  //class Op(val out: String)
  //trait LR {
  //  def left:
  //
  //}
  //class And(val left) extends Op with LR


