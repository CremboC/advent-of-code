import scala.annotation.tailrec
import scala.io.Source

val lines = Source.fromFile("input.data").getLines.toVector
val registers = Map[String, Int](
  "a" -> 0,
  "b" -> 0
)

recur(registers, lines, 0)

@tailrec
def recur(rs: Map[String, Int], lines: IndexedSeq[String], nextInstr: Int): Map[String, Int] = {
  if (nextInstr == -1) rs
  else if (nextInstr > lines.length - 1) rs
  else {
    val instruction = lines(nextInstr)
    val split = instruction.split(" ").toVector
    val register = split(1)

    println(instruction)

    val r = split.head match {
      case "hlf" => (nextInstr + 1, Some(register -> rs(register) / 2))
      case "tpl" => (nextInstr + 1, Some(register -> rs(register) * 3))
      case "inc" => (nextInstr + 1, Some(register -> (rs(register) + 1)))
      case "jmp" => (nextInstr + split(1).toInt, None)
      case "jie" =>
        val actual = register.split(",")(0)
        val offset = split(2).toInt

        if (rs(actual) % 2 == 0) (nextInstr + offset, None)
        else (nextInstr + 1, None)
      case "jio" =>
        val actual = register.split(",")(0)
        val offset = split(2).toInt

        if (rs(actual) == 1) (nextInstr + offset, None)
        else (nextInstr + 1, None)
      case _ => (-1, None)
    }

    val newRs = r._2 match {
      case Some(pair) => rs + pair
      case _ => rs
    }

    println(s"Next instruction is '${r._1}', $newRs")

    recur(newRs, lines, r._1)
  }
}