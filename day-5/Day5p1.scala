import scala.io.Source

/**
  * Created by paulius on 26/12/2015.
  */

val words = Source.fromFile("input.data").getLines
val vr = """[aeiou]""".r
val dr = """([a-z])\1""".r
val ir = """ab|cd|pq|xy""".r

def threeVowels(word: String): Boolean = vr.findAllMatchIn(word).length >= 3
def doubleLetter(word: String): Boolean = dr.findFirstIn(word).isDefined
def illegal(word: String): Boolean = ir.findFirstIn(word).isDefined

println(words.filter(threeVowels).filter(doubleLetter).filterNot(illegal).size)