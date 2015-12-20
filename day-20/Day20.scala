import scala.annotation.tailrec

/**
  * Created by paulius on 20/12/2015.
  */

object Day20 {

  val elves = 1000000

  def main(args: Array[String]): Unit = {
    val target = 36000000

    args.length == 1 match {
      case true if args(0) == "one" => println(partOne(target, 800500, 1005000))
      case true if args(0) == "two" => println(partTwo(target, 800500, 1005000))
      case _ => sys.exit(-1)
    }
  }

  @tailrec
  def partOne(target: Int, houseNumber: Int, max: Int): Int = {
    if (houseNumber >= max) {
      println("More than max")
      sys.exit(-1)
    }
    val numOfPresents = (1 to elves).foldLeft(0) { (presents, elfNumber) =>
      if (houseNumber % elfNumber == 0) presents + elfNumber * 10
      else presents
    }
    println(s"House $houseNumber; Presents: $numOfPresents")
    if (numOfPresents >= target) {
      houseNumber
    } else {
      partOne(target, houseNumber + 5, max)
    }
  }

  @tailrec
  def partTwo(target: Int, houseNumber: Int, max: Int): Int = {
    if (houseNumber >= max) {
      println("More than max")
      sys.exit(-1)
    }
    val numOfPresents = (1 to elves).foldLeft(0) { (presents, elfNumber) =>
      if (houseNumber % elfNumber == 0 && houseNumber / elfNumber < 50) {
        presents + elfNumber * 11
      } else presents
    }
    println(s"House $houseNumber; Presents: $numOfPresents")
    if (numOfPresents >= target) {
      houseNumber
    } else {
      partTwo(target, houseNumber + 5, max)
    }
  }

}


