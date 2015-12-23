
import scala.annotation.tailrec
import scala.collection.immutable.Queue
import scala.util.Random

object Day22 {

  val bossDamage = 10
  val bossStartHp = 71

//  val bossDamage = 8
//  val bossStartHp = 13

  val availableSpells = Vector(
    new MagicMissile(),
    new Drain(),
    new Shield(),
    new Poison(),
    new Recharge()
  )

  def main(args: Array[String]) {
    var finished = false
    var combos = 0
    while (!finished) {
      val won = try {
        playerWon(new Player(0, generateSpellSet()))
      } catch {
        case _: Throwable => (false, -2)
      }
      combos += 1
      println(combos)
      if (won._1) {
        println(won)
        finished = true
      }
    }
  }

  def lastThreeAreCleanFrom(s: Spell, spells: Vector[Spell]): Boolean = {
    spells.size match {
      case 0 => true
      case 1 => !spells.last.equals(s)
      case 2 => !spells.last.equals(s) && !spells(spells.length - 2).equals(s)
      case _ => !spells.last.equals(s) && !spells(spells.length - 2).equals(s) && !spells(spells.length - 3).equals(s)
    }
  }

  val spells = (10 to 20).toVector

  def randomSpellAmount: Int = Random.shuffle(spells).head

  def generateSpellSet(): Queue[Spell] = {
    def nextSpell(): Spell = Random.shuffle(availableSpells).head match {
      case _: MagicMissile => new MagicMissile
      case _: Drain => new Drain
      case _: Shield => new Shield
      case _: Poison => new Poison
      case _: Recharge => new Recharge
    }

    @tailrec
    def go(spells: Vector[Spell], cost: Int): Vector[Spell] = {
      if (spells.size >= randomSpellAmount) spells
      else {
        var next: Spell = null
        var finished = false
        while (!finished) {
          next = nextSpell()
          finished = next match {
            case s: Shield if !lastThreeAreCleanFrom(s, spells) => false
            case r: Recharge if !lastThreeAreCleanFrom(r, spells) => false
            case p: Poison if !lastThreeAreCleanFrom(p, spells) => false
            case _ => true
          }
        }
        go(spells :+ next, cost + next.cost)
      }
    }

    val set = go(Vector(), 0)
    println(set)
    Queue(set: _*)
  }

  def playerWon(p: Player, b: Boss = new Boss): (Boolean, Int) = simulate(p, b) match {
    case (player: Player, c: Int) => (true, c)
    case (boss: Boss, _) => (false, -1)
  }

  def simulate(p: Player, b: Boss = new Boss): (Char, Int) = {
    @tailrec
    def go(p: Player, b: Boss, s: State, turns: Int, cost: Int): (Char, Int) = {
      if (b.hp <= 0) (p, cost)
      else if (p.hp <= 0) (b, cost)
      else if (p.mana < 0) (b, cost)
      else {
//        println(s"${s.currentAttacker.getClass}'s turn; Turn $turns")
//        println("––––––––––––––––––––––")
//        println(s"Player has ${p.hp} hp, ${p.mana} mana, ${p.armor} armor")
//        println(s"Boss has ${b.hp} hp")

        // for spells that have ended, do their end-effect
        val (aPlayer, aBoss) = s.activeSpells.filter(_._2 == 0).foldLeft((p, b)) { (carry, spell) =>
//          println(s"Spell ${spell._1} has ended")
          spell._1.asInstanceOf[Spell with Turns].atEnd(carry._1, carry._2)
        }

        // reduce numbers of turns left for active spells
        val newActiveSpells = s.activeSpells.foldLeft(Map.empty[Spell, Int]) { (carry, ac) =>
          ac match {
            case (spell, turnsLeft) if turnsLeft == 0 => carry
            case (spell, turnsLeft) => carry + (spell -> (turnsLeft - 1))
          }
        }

        // calculate player and boss after applying effects of turn-based spells
        val (newPlayer, newBoss) = newActiveSpells.isEmpty match {
          case true => (aPlayer, aBoss)
          case false =>
            // only apply spells that have a "each turn" effect
            newActiveSpells.foldLeft((aPlayer, aBoss)) { (carry, activeSpell) =>
              activeSpell._1 match {
                case spell: Spell with EachTurn =>
//                  println(s"Spell $spell ticks with ${activeSpell._2} turns left")
                  spell.eachTurn(carry._1, carry._2)
                case _ => carry
              }
            }
        }

        if (newBoss.hp <= 0) return (newPlayer, cost)

        // get the final player, boss and the state after applying the spell for this turn
        val (finalPlayer, finalBoss, newState, newCost) = s.currentAttacker match {
          // if this is the player's turn
          case _: Player =>
            val (nextSpell, spellsLeft) = newPlayer.spells.dequeue

            val newState = nextSpell match {
              case spell: Spell with Turns => new State(b, newActiveSpells + (spell -> spell.turns))
              case _ => new State(b, newActiveSpells)
            }

//            println(s"Player casts $nextSpell")
            val (afterSpellPlayer, finalBoss) = nextSpell.fire(newPlayer, newBoss)
            val finalPlayer = new Player(afterSpellPlayer.armor, spellsLeft, afterSpellPlayer.mana, afterSpellPlayer.hp)

            (finalPlayer, finalBoss, newState, cost + nextSpell.cost)

          // else if this is the boss's turn
          case _: Boss =>
            val damage = math.max(newBoss.damage - newPlayer.armor, 1)
            val hp = newPlayer.hp - damage
            val finalPlayer = new Player(newPlayer.armor, newPlayer.spells, newPlayer.mana, hp)
            val finalState = new State(finalPlayer, newActiveSpells)

//            println(s"Boss does $damage damage")

            (finalPlayer, newBoss, finalState, cost)
        }

//        println
        go(finalPlayer, finalBoss, newState, turns + 1, newCost)
      }
    }

    val state = new State(p)
    go(p, b, state, 0, 0)
  }

  class State(val currentAttacker: Char, val activeSpells: Map[Spell, Int] = Map.empty) {
    override def toString = s"State(currentAttacker=$currentAttacker, activeSpells=$activeSpells)"
  }

  trait Char

  class Boss(val hp: Int = bossStartHp) extends Char {
    val damage = bossDamage

    override def toString = s"Boss(damage=$damage, hp=$hp)"
  }

  class Player(val armor: Int,
               val spells: Queue[Spell] = Queue.empty,
               val mana: Int = 500,
               val hp: Int = 50) extends Char {

    override def toString = s"Player(armor=$armor, spells=$spells, mana=$mana, hp=$hp)"
  }

  trait Turns {
    def turns: Int

    def atEnd(p: Player, b: Boss): (Player, Boss) = (p, b)
  }

  trait EachTurn {
    def eachTurn(p: Player, b: Boss): (Player, Boss)
  }

  abstract class Spell(val cost: Int) {
    def fire(p: Player, b: Boss): (Player, Boss) = {
//      println
      (new Player(p.armor, p.spells, p.mana - this.cost, p.hp), b)
    }
  }

  class MagicMissile extends Spell(53) {
    val damage = 4

    override def fire(p: Player, b: Boss): (Player, Boss) = {
//      println(s" dealing $damage damage")
      (new Player(p.armor, p.spells, p.mana - this.cost, p.hp), new Boss(b.hp - this.damage))
    }

    override def toString = s"MagicMissile"

    def canEqual(other: Any): Boolean = other.isInstanceOf[MagicMissile]

    override def equals(other: Any): Boolean = other match {
      case that: MagicMissile => true
      case _ => false
    }
  }

  class Drain extends Spell(73) {
    val heal = 2
    val damage = 2

    override def fire(p: Player, b: Boss): (Player, Boss) = {
//      println(s" dealing $damage damage; and healing self $heal")
      (new Player(p.armor, p.spells, p.mana - this.cost, p.hp + this.heal), new Boss(b.hp - this.damage))
    }

    override def toString = s"Drain"

    override def equals(other: Any): Boolean = other match {
      case that: Drain => true
      case _ => false
    }
  }

  class Shield extends Spell(113) with Turns {
    val armor = 7

    override def fire(p: Player, b: Boss): (Player, Boss) = {
//      println(s" increasing armor by $armor damage")
      (new Player(p.armor + this.armor, p.spells, p.mana - this.cost, p.hp), b)
    }

    override def atEnd(p: Player, b: Boss): (Player, Boss) = {
      (new Player(p.armor - this.armor, p.spells, p.mana, p.hp), b)
    }

    override def turns: Int = 6

    override def toString = s"Shield"

    override def equals(other: Any): Boolean = other match {
      case that: Shield => true
      case _ => false
    }
  }

  class Poison extends Spell(173) with Turns with EachTurn {
    val damage = 3

    override def eachTurn(p: Player, b: Boss): (Player, Boss) = {
      (new Player(p.armor, p.spells, p.mana, p.hp), new Boss(b.hp - this.damage))
    }

    override def turns: Int = 6

    override def toString = s"Poison"

    override def equals(other: Any): Boolean = other match {
      case that: Poison => true
      case _ => false
    }
  }

  class Recharge extends Spell(229) with Turns with EachTurn {
    val mana = 101

    override def eachTurn(p: Player, b: Boss): (Player, Boss) = {
      (new Player(p.armor, p.spells, p.mana + this.mana, p.hp), b)
    }

    override def turns: Int = 5

    override def toString = s"Recharge"

    override def equals(other: Any): Boolean = other match {
      case that: Recharge => true
      case _ => false
    }
  }

}
