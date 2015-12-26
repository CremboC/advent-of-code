import scala.io.Source

/**
  * Created by paulius on 26/12/2015.
  */

val words = Source.fromFile("input.data").getLines
val dr = """([a-z]{2}).*\1""".r
val inbtr = """([a-z]{1})([a-z]{1})\1""".r

def doubleLetter(word: String): Boolean = dr.findAllMatchIn(word).nonEmpty
def inbetween(word: String): Boolean = inbtr.findAllMatchIn(word).nonEmpty

println(words.filter(doubleLetter).count(inbetween))