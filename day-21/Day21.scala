object Day21 {
  val boss = new Boss(9, 2, 103)

  val weapons = List(
    (8, 4, 0),
    (10, 5, 0),
    (25, 6, 0),
    (40, 7, 0),
    (74, 8, 0)
  )

  val armor = List(
    (0, 0, 0),
    (13, 0, 1),
    (31, 0, 2),
    (53, 0, 3),
    (75, 0, 4),
    (102, 0, 5)
  )

  val rings = List(
    (25, 1, 0),
    (50, 2, 0),
    (100, 3, 0),
    (20, 0, 1),
    (40, 0, 2),
    (80, 0, 3)
  )

  def isWinner(p: Player): Boolean = simulate(p) match {
    case _: Player => true
    case _: Boss => false
  }

  def mkPlayer(item: (Int, Int, Int), p: Player, isRing: Boolean = false): Player = {
    val rings = isRing match {
      case true => p.rings + item
      case _ => p.rings
    }
    new Player(p.dmg + item._2, p.defence + item._3, p.cost + item._1, rings)
  }

  def main(args: Array[String]) {
    val players = weapons.map { w =>
      new Player(w._2, 0, w._1)
    }

    val armoredPlayers = for (p <- players; a <- armor) yield mkPlayer(a, p)
    val oneRinged = for (p <- players; r <- rings) yield mkPlayer(r, p, isRing = true)
    val twoRinged = for (p <- oneRinged; r <- rings; if !p.rings.contains(r)) yield mkPlayer(r, p, isRing = true)
    val oneRingedAndArmored = for (p <- armoredPlayers; r <- rings) yield mkPlayer(r, p, isRing = true)
    val twoRingedAndArmored = for (p <- oneRingedAndArmored; r <- rings; if !p.rings.contains(r)) yield mkPlayer(r, p, isRing = true)

    if (args.length == 1) {
      args(0) match {
        case "one" =>
          val weaponOnlyWinner = players.filter(isWinner)
          val armoredWinners = armoredPlayers.filter(isWinner)
          val oneRingedWinners = oneRinged.filter(isWinner)
          val twoRingedWinners = twoRinged.filter(isWinner)
          val oneRingedAndArmoredWinners = oneRingedAndArmored.filter(isWinner)

          val combined = weaponOnlyWinner ++ armoredWinners ++ oneRingedWinners ++ twoRingedWinners ++ oneRingedAndArmoredWinners
          println(s"Winner for part one is: ${combined.minBy(_.cost)}")

        case "two" =>
          val twoRingedAndArmoredLosers = twoRingedAndArmored.filterNot(isWinner)

          println(s"Loser for part two is: ${twoRingedAndArmoredLosers.maxBy(_.cost)}")
      }
    }
  }

  // turn 0 = player; turn = 1
  def simulate(p: Player): Char = {
    def go(p: Player, b: Boss, playerNow: Boolean): Char = {
      if (p.hp <= 0) b
      else if (b.hp <= 0) p
      else {
        playerNow match {
          case true => go(p, new Boss(b.dmg, b.defence, b.hp - math.max(p.dmg - b.defence, 1)), playerNow = false)
          case false => go(new Player(p.dmg, p.defence, p.cost, p.rings, p.hp - math.max(b.dmg - p.defence, 1)), b, playerNow = true)
        }
      }
    }
    go(p, boss, playerNow = true)
  }

  trait Char

  class Player(val dmg: Int,
               val defence: Int,
               val cost: Int,
               val rings: Set[(Int, Int, Int)] = Set.empty,
               val hp: Int = 100) extends Char {
    override def toString = s"Player(dmg=$dmg, defence=$defence, cost=$cost, hp=$hp)"
  }

  class Boss(val dmg: Int, val defence: Int, val hp: Int) extends Char {
    override def toString = s"Boss(dmg=$dmg, defence=$defence, hp=$hp)"
  }

}
