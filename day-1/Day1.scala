import scala.io.Source

object Day1 {
  def main(args: Array[String]) {
    if (args.length == 0) {
      println("Arg 1 is [one|two]")
      System.exit(1)
    }

    val str: String = Source.fromFile("input.data").mkString
    if (args(0) == "one") {
      val up: Int = str.count(s => s == '(')
      val down: Int = str.count(s => s == ')')

      println(up - down)
    } else {
      var floor = 1
      str.zipWithIndex.foreach {
        case (s, i) =>
          s match {
            case '(' => floor += 1
            case ')' => floor -= 1
            case _ =>
          }
          if (floor == -1) {
            println(i)
            return
          }
      }
    }
  }
}