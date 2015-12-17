import java.security.MessageDigest

/**
  * Created by paulius on 17/12/2015.
  */

object Day4 {
  val m = MessageDigest.getInstance("MD5")

  def md5(s: String) = {
    m.update(s.getBytes)
    m.digest().map("%02x".format(_)).mkString
  }

  def main(args: Array[String]) {
    val original = "iwrupvqb"
    Stream.from(1).foreach(i => {
      val input = original + i
      if (md5(input).startsWith("000000")) {
        println(i)
        return
      }
    })
  }
}

