
import scala.annotation.tailrec
import scala.collection.immutable.Queue
import scala.util.Random

object Day22 {

  val bossDamage = 10
  val bossStartHp = 71

  val availableSpells = Vector(
    new MagicMissile(),
    new Drain(),
    new Shield(),
    new Poison(),
    new Recharge()
  )

  val availableSpellsQueue = Queue(availableSpells: _*)

  def main(args: Array[String]) {

    val set = Queue(new Shield,
      new Recharge,
      new Poison,
      new Shield,
      new MagicMissile,
      new Recharge,
      new Poison,
      new MagicMissile,
      new MagicMissile,
      new Recharge,
      new Poison,
      new Drain,
      new MagicMissile,
      new Drain)
    println(simulate(new Player(0, set)))

//    var finished = false
//    var combos = 0
//    while (!finished) {
//      val won = try {
//        playerWon(new Player(0, generateSpellSet()))
//      } catch {
//        case _: Throwable => (false, -1)
//      }
//      combos += 1
//      println(combos)
//      if (won._1) {
//        println(won)
//        finished = true
//      }
//    }
  }

  def generateSpellSet(): Queue[Spell] = {
    def nextSpell(): Spell = Random.shuffle(availableSpells).head match {
      case _: MagicMissile => new MagicMissile
      case _: Drain => new Drain
      case _: Shield => new Shield
      case _: Poison => new Poison
      case _: Recharge => new Recharge
    }

    @tailrec
    def go(queue: Queue[Spell], cost: Int): Queue[Spell] = {
      if (cost >= 1750) queue
      else {
        var next: Spell = null
        var finished = false
        while (!finished) {
          next = nextSpell()
          finished = next match {
            case _: Shield if queue.last.isInstanceOf[Shield] => false
            case _: Recharge if queue.last.isInstanceOf[Recharge] => false
            case _: Poison if queue.last.isInstanceOf[Poison] => false
            case _ => true
          }
        }
        go(queue :+ next, cost + next.cost)
      }
    }

    val shield = new Shield
    val set = go(Queue(shield), shield.cost)
    println(set)
    set
  }

  def playerWon(p: Player): (Boolean, Int) = simulate(p) match {
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
//        println(s"Player has ${p.hp} hp, ${p.mana} mana, ${p.armor} armor")
//        println(s"Boss has ${b.hp} hp")
//        println(s"Cost is $cost")

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
              case spell: Spell with Turns if !s.activeSpells.keySet.contains(spell) => new State(b, newActiveSpells + (spell -> spell.turns))
              case _ => new State(b, newActiveSpells)
            }

//            print(s"Player casts $nextSpell")
            val (afterSpellPlayer, finalBoss) = nextSpell.fire(newPlayer, newBoss)
            val finalPlayer = new Player(afterSpellPlayer.armor, spellsLeft, afterSpellPlayer.mana, afterSpellPlayer.hp)

            println(cost, nextSpell.cost)

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
