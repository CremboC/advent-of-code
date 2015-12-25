import scala.io.Source

val presents = Source.fromFile("input.data").getLines.map(_.toLong).toVector
val compartments = 4
val targetWeight = presents.sum / compartments

val min: Long = (1 to 10).map { i =>
  val qe = presents.combinations(i).filter(_.sum == targetWeight)
  if (qe.nonEmpty) qe.minBy(_.product).product
  else Long.MaxValue
}.min

println(min)