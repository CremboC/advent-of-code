import scala.annotation.tailrec
import scala.collection.immutable.Queue

object Day22 {

  val startBoss = new Boss

  def main(args: Array[String]) {
    val testSpells = Queue(
      new Shield(),
      new Poison(),
      new MagicMissile(),
      new Drain(),
      new MagicMissile(),
      new MagicMissile(),
      new MagicMissile(),
      new Shield(),
      new MagicMissile()
    )
    val test = new Player(0, testSpells)

    simulate(test)
  }

  def simulate(p: Player): Char = {
    @tailrec
    def go(p: Player, b: Boss, s: State): Char = {
      println(s"${s.currentAttacker.getClass}'s turn")
      println(s"Player is $p")
      println(s"Boss is $b")
      if (p.hp <= 0) p
      else if (b.hp <= 0) b
      else {

        // for spells that have ended, do their end-effect
        val (aPlayer, aBoss) = s.activeSpells.filter(_._2 == 0).foldLeft((p, b)) { (carry, spell) =>
          println(s"Spell ${spell._1} has ended")
          spell._1.asInstanceOf[Spell with Turns].atEnd(carry._1, carry._2)
        }

        // reduce numbers of turns left for active spells
        val newActiveSpells = s.activeSpells.foldLeft(Map.empty[Spell, Int]) { (carry, ac) =>
          ac match {
            case (spell, turnsLeft) if turnsLeft == 0 => carry
            case (spell, turnsLeft) => carry + (spell -> (turnsLeft - 1))
          }
        }

        val (newPlayer, newBoss) = s.activeSpells.isEmpty match {
          case true => (aPlayer, aBoss)
          case false =>
            // get new players and boss after active spells have done their effect
            s.activeSpells.foldLeft((aPlayer, aBoss)) { (carry, activeSpell) =>
              activeSpell._1 match {
                case spell: Spell with EachTurn =>
                  println(s"Spell $spell ticks with ${activeSpell._2} turns left")
                  spell.eachTurn(carry._1, carry._2)
                case _ => carry
              }
            }
        }

        // get the final player, boss and the state after applying the spell for this turn
        val (finalPlayer, finalBoss, newState) = s.currentAttacker match {
          case _: Player =>
            val (nextSpell, spellsLeft) = newPlayer.spells.dequeue

            val newState = nextSpell match {
              case spell: Spell with Turns if !s.activeSpells.contains(spell) => new State(b, newActiveSpells + (spell -> spell.turns))
              case _ => new State(b, newActiveSpells)
            }

            printf(s"Player casts $nextSpell")
            val (afterSpellPlayer, finalBoss) = nextSpell.fire(newPlayer, newBoss)
            val finalPlayer = new Player(afterSpellPlayer.armor, spellsLeft, afterSpellPlayer.mana, afterSpellPlayer.hp)


            (finalPlayer, finalBoss, newState)
          case _: Boss =>
            val damage = math.max(newBoss.damage - newPlayer.armor, 1)
            val hp = newPlayer.hp - damage
            val finalPlayer = new Player(newPlayer.armor, newPlayer.spells, newPlayer.mana, hp)
            val finalState = new State(finalPlayer, newActiveSpells)

            println(s"Boss does $damage damage")

            (finalPlayer, newBoss, finalState)
        }

        println
        go(finalPlayer, finalBoss, newState)
      }
    }

    val state = new State(p)
    go(p, startBoss, state)
  }

  class State(val currentAttacker: Char, val activeSpells: Map[Spell, Int] = Map.empty) {
    override def toString = s"State(currentAttacker=$currentAttacker, activeSpells=$activeSpells)"
  }

  trait Char

  class Boss(val hp: Int = 71) extends Char {
    val damage = 10

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

    def atEnd(p: Player, b: Boss): (Player, Boss)
  }

//  trait Instant {
//    def fire(p: Player, b: Boss): (Player, Boss)
//  }

  trait EachTurn {
    def eachTurn(p: Player, b: Boss): (Player, Boss)
  }

  abstract class Spell(val cost: Int) {
    def fire(p: Player, b: Boss): (Player, Boss) = {
      println
      (new Player(p.armor, p.spells, p.mana - this.cost, p.hp), b)
    }
  }

  class MagicMissile extends Spell(53) {
    val damage = 4

    override def fire(p: Player, b: Boss): (Player, Boss) = {
      println(s" dealing $damage damage")
      (new Player(p.armor, p.spells, p.mana - this.cost, p.hp), new Boss(b.hp - this.damage))
    }

    override def toString = s"MagicMissile"
  }

  class Drain extends Spell(73) {
    val heal = 2
    val damage = 2

    override def fire(p: Player, b: Boss): (Player, Boss) = {
      println(s" dealing $damage damage; and healing self $heal")
      (new Player(p.armor, p.spells, p.mana - this.cost, p.hp + this.heal), new Boss(b.hp - this.damage))
    }

    override def toString = s"Drain"
  }

  class Shield extends Spell(113) with Turns {
    val armor = 7

    override def fire(p: Player, b: Boss): (Player, Boss) = {
      println(s" increasing armor by $armor damage")
      (new Player(p.armor + this.armor, p.spells, p.mana - this.cost, p.hp), b)
    }

    override def atEnd(p: Player, b: Boss): (Player, Boss) = {
      (new Player(p.armor - this.armor, p.spells, p.mana, p.hp), b)
    }

    override def turns: Int = 7

    override def toString = s"Shield"
  }

  class Poison extends Spell(173) with Turns with EachTurn {
    val damage = 3

    override def eachTurn(p: Player, b: Boss): (Player, Boss) = {
      (new Player(p.armor, p.spells, p.mana, p.hp), new Boss(b.hp - this.damage))
    }

    override def atEnd(p: Player, b: Boss): (Player, Boss) = (p, b)

    override def turns: Int = 6

    override def toString = s"Poison"
  }

  class Recharge extends Spell(229) with Turns with EachTurn {
    val mana = 101

    override def atEnd(p: Player, b: Boss): (Player, Boss) = (p, b)

    override def eachTurn(p: Player, b: Boss): (Player, Boss) = {
      (new Player(p.armor, p.spells, p.mana + this.mana, p.hp), b)
    }

    override def turns: Int = 5

    override def toString = s"Recharge"
  }

}
