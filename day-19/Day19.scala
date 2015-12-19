import scala.io.Source

object Day19 {
  def main(args: Array[String]) {
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
}