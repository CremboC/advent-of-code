import scala.io.Source
import scala.util.matching.Regex

object Day19 {
  def main(args: Array[String]) =
    if (args.length == 1)
      args(0) match {
        case "one" => part1()
        case "two" => part2()
        case _ => println("one/two")
      }

  def part1(): Unit = {
    val lines = Source.fromFile("input.data").getLines.toList
    val combination = lines.last
    val chems = lines.takeWhile(!_.isEmpty).foldLeft(Map.empty[String, List[String]]) { (map, current) =>
      val sp = current.split(" => ")
      map.contains(sp.head) match {
        case true => map + (sp.head -> (sp.last :: map(sp.head)))
        case false => map + (sp.head -> List(sp.last))
      }
    }

    val r = """([A-Ze]{1}[almihnrg]*)""".r
    val all = r.findAllMatchIn(combination).map(_.toString).toList
    val molecules = for ((subject, index) <- all.zipWithIndex; if chems.contains(subject)) yield {
      val (before, after) = (all.slice(0, index).mkString, all.slice(index + 1, all.length).mkString)
      chems(subject) map { el =>
        "%s%s%s".format(before, el, after)
      }
    }
    println(molecules.flatten.toSet.size)
  }

  def part2(): Unit = {
    val lines = Source.fromFile("input.data").getLines.toList
    val start = lines.last
    val target = "e"
    val e = List("HF", "NAl", "OMg")
    var m = Map.empty[String, Boolean]

    val reverseChems = lines.takeWhile(!_.isEmpty).foldLeft(Map.empty[String, String]) { (map, current) =>
      val sp = current.split(" => ")
      map + (sp.last -> sp.head)
    }

    println(reverseChems)

    val r = s"""(${reverseChems.keys.toSet.mkString("|")})""".r
    var l = Set.empty[String]

    magic(start, 1)

    def mkAll(r: Regex, str: String): List[String] = r.findAllMatchIn(str).map(_.toString).toList

    def magic(string: String, steps: Int): Unit = {
      val all = mkAll(r, string)
      if (all.length == 1) {
        l += all.head
        return
      }
      for ((subject, index) <- all.zipWithIndex) yield {
        val (before, after) = (all.slice(0, index).mkString, all.slice(index + 1, all.length).mkString)
        val strings: List[String] = subject match {
          case "e" => for (ev <- e) yield "%s%s%s".format(before, ev, after)
          case _ => List("%s%s%s".format(before, reverseChems(subject), after))
        }

        for (str <- strings; if !m.contains(str)) yield {
          m += (str -> true)
          magic(str, steps + 1)
        }
      }
    }
  }

}