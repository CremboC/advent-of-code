import scala.collection._
import scala.io.Source
import scala.util.Random

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
      chems(subject) map {
        "%s%s%s".format(before, _, after)
      }
    }
    println(molecules.flatten.toSet.size)
  }

  def part2(): Unit = {
    val lines = Source.fromFile("input.data").getLines.toList

    var chems = lines.takeWhile(!_.isEmpty).foldLeft(List.empty[(String, String)]) { (ls, current) =>
      val sp = current.split(" => ")
      (sp.last, sp.head) :: ls
    }

    var steps = 0
    var molecule = lines.last
    while (true) {
      chems = Random.shuffle(chems)
      chems.foreach {
        case (from, to) if molecule.contains(from) =>
          molecule = molecule.replaceFirst(from, to)
          steps += 1
        case _ =>
      }
      // often gets stuck on this so just restart, lol
      if (molecule == "CRnSiRnFYCaRnFArArFArAl") {
        molecule = lines.last
      }
      if (molecule == "e") {
        println(s"Steps: $steps; Run this couple of times to make sure this is the lowest")
        sys.exit()
      }
    }
  }

}