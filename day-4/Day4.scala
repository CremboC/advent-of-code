import java.security.MessageDigest

import scala.annotation.tailrec

/**
  * Created by paulius on 17/12/2015.
  */

val m = MessageDigest.getInstance("MD5")
def md5(s: String) = {
  m.update(s.getBytes)
  m.digest().map("%02x".format(_)).mkString
}

val original = "iwrupvqb"

@tailrec
def calc(input: String, number: Int): Int = {
  if (md5(input).startsWith("00000")) number - 1
  else {
    calc(original + number, number + 1)
  }
}

println(calc(original, 1))

