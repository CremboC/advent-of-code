
import scala.annotation.tailrec
import scala.collection.immutable.Queue
import scala.util.Random

object Day22 {

  val bossDamage = 10
  val bossStartHp = 71

  val debug = false
  val hardMode = false

//    val bossDamage = 8
//    val bossStartHp = 14

  val availableSpells = Vector(
    MagicMissile(),
    Drain(),
    Shield(),
    Poison(),
    Recharge()
  )

  def main(args: Array[String]) {

//    val spells = Queue(Recharge(), Shield(), Drain(), Poison(), MagicMissile())
//    val won = try playerWon(Player(0, spells, 250, 10)) catch {
//      case _: Throwable => (false, -2)
//    }
//
//    if (won._1) {
//      println(won)
//    }
    simulate(Player(0), Boss())

    // var finished = false
    // var combos = 0
    // while (!finished) {
    //   val won = try {
    //     playerWon(Player(0, generateSpellSet()))
    //   } catch {
    //     case _: Throwable => (false, -2)
    //   }
    //   combos += 1
    //   if (debug) println(combos)
    //   if (won._1) {
    //     println(won)
    //     finished = true
    //   }
    // }
  }

  def lastThreeAreCleanFrom(s: Spell, spells: Vector[Spell]): Boolean = {
    spells.size match {
      case 0 => true
      case 1 => spells.last != s
      case 2 => !spells.slice(spells.length - 2, spells.length).contains(s)
      case _ => !spells.slice(spells.length - 3, spells.length).contains(s)
    }
  }

  val spells = (10 to 15).toVector

  def randomSpellAmount: Int = Random.shuffle(spells).head

  def generateSpellSet(): Queue[Spell] = {
    def nextSpell(): Spell = Random.shuffle(availableSpells).head

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
    if (debug)
      println(set)
    Queue(set: _*)
  }

  def playerWon(p: Player, b: Boss = new Boss): (Boolean, Int) = simulate(p, b) match {
    case (player: Player, c: Int) => (true, c)
    case (boss: Boss, _) => (false, -1)
  }

  def simulate(p: Player, b: Boss = new Boss): (Char, Int) = {
    // @tailrec
    def run(p: Player, b: Boss, s: State, turns: Int, cost: Int): Game = {
      if (b.hp <= 0) {
        if (debug) println("Boss has died")
        Game(p, b, s, turns, cost)
        // (p, cost)
      }
      else if (p.hp <= 0) {
        if (debug) println("Player has died")
        Game(p, b, s, turns, cost)
        // (b, cost)
      }
      else if (p.mana < 0) {
        if (debug) println("Player ran out of mana")
        Game(p, b, s, turns, cost)
        // (b, cost)
      }
      else {
        if (debug) {
          println(s"${s.currentAttacker.getClass}'s turn; Turn $turns")
          println("––––––––––––––––––––––")
          println(s"Player has ${p.hp} hp, ${p.armor} armor, ${p.mana} mana")
          println(s"Boss has ${b.hp} hp")
        }

        val prePlayer = s.currentAttacker match {
          case _: Player if hardMode =>
            if (debug) println(s"Player is hit by 1 dmg and has ${p.hp - 1} hp")
            Player(p.armor, p.spells, p.mana, p.hp - 1)
          case _ => p
        }

        if (prePlayer.hp <= 0) {
          if (debug) println("Player has died")
          return Game(p, b, s, turns, cost)
          // (b, cost)
        }

        // for spells that have ended, do their end-effect
        val (aPlayer, aBoss) = s.activeSpells.filter(_._2 == 0).foldLeft((prePlayer, b)) { (carry, spell) =>
          if (debug) println(s"Spell ${spell._1} has ended")
          spell._1.asInstanceOf[Spell with Turns].atEnd(carry._1, carry._2)
        }

        // reduce numbers of turns left for active spells
        val activeSpells = s.activeSpells.foldLeft(Map.empty[Spell, Int]) { (carry, ac) =>
          ac match {
            case (spell, turnsLeft) if turnsLeft == 0 => carry
            case (spell, turnsLeft) => carry + (spell -> (turnsLeft - 1))
          }
        }

        // calculate player and boss after applying effects of turn-based spells
        val (newPlayer, newBoss) = activeSpells.isEmpty match {
          case true => (aPlayer, aBoss)
          case false =>
            // only apply spells that have a "each turn" effect
            activeSpells.foldLeft((aPlayer, aBoss)) { (carry, activeSpell) =>
              activeSpell._1 match {
                case spell: Spell with EachTurn =>
                  if (debug) println(s"Spell $spell ticks with ${activeSpell._2} turns left")
                  spell.eachTurn(carry._1, carry._2)
                case _ => carry
              }
            }
        }

        if (newBoss.hp <= 0) {
          if (debug) println("Boss has died after applying tick effects.")
          return Game(newPlayer, b, s, turns, cost)
        }

        // get the final player, boss and the state after applying the spell for this turn
        val (finalPlayer, finalBoss, newState, newCost) = s.currentAttacker match {
          // if this is the player's turn
          case _: Player =>
            val (nextSpell, spellsLeft) = newPlayer.spells.dequeue

            val newState = nextSpell match {
              case spell: Spell with Turns => State(b, activeSpells + (spell -> spell.turns))
              case _ => State(b, activeSpells)
            }

            if (debug) println(s"Player casts $nextSpell")
            val (afterSpellPlayer, finalBoss) = nextSpell.fire(newPlayer, newBoss)
            val finalPlayer = Player(afterSpellPlayer.armor, spellsLeft, afterSpellPlayer.mana, afterSpellPlayer.hp)

            (finalPlayer, finalBoss, newState, cost + nextSpell.cost)

          // else if this is the boss's turn
          case _: Boss =>
            val damage = math.max(newBoss.damage - newPlayer.armor, 1)
            val finalPlayer = Player(newPlayer.armor, newPlayer.spells, newPlayer.mana, newPlayer.hp - damage)
            val finalState = State(finalPlayer, activeSpells)

            if (debug) println(s"Boss does $damage damage")

            (finalPlayer, newBoss, finalState, cost)
        }

        if (debug) println

        Game(finalPlayer, finalBoss, newState, turns + 1, newCost)
        // go(finalPlayer, finalBoss, newState, turns + 1, newCost)
      }
    }

    val state = State(p)

    // run simulation with all spells
    // choose best simulation
    // select it as best combo
    // repeat

    def sim(g: Game): Game = {
      val postSpell = availableSpells.map { spell => 
        run(Player(g.p.armor, Queue(spell), g.p.mana, g.p.hp), g.boss, g.state, g.turns, g.cost)
      }

      postSpell.maxBy(_.p.hp)
    }


    println("hi")

    (Player(0), 0)


    // run(p, b, state, 0, 0)
  }

  case class Game(player: Player, boss: Boss, state: State, turns: Int, Cost: Int) {

  }

  case class State(currentAttacker: Char, activeSpells: Map[Spell, Int] = Map.empty) {
    override def toString = s"State(currentAttacker=$currentAttacker, activeSpells=$activeSpells)"
  }

  trait Char

  case class Boss(hp: Int = bossStartHp, damage: Int = bossDamage) extends Char {
    override def toString = s"Boss(damage=$damage, hp=$hp)"
  }

  case class Player(armor: Int = 0, spells: Queue[Spell] = Queue.empty, mana: Int = 500, hp: Int = 50) extends Char {
    override def toString = s"Player(armor=$armor, spells=$spells, mana=$mana, hp=$hp)"
  }

  trait Turns {
    val turns: Int

    def atEnd(p: Player, b: Boss): (Player, Boss) = (p, b)
  }

  trait EachTurn {
    def eachTurn(p: Player, b: Boss): (Player, Boss)
  }

  abstract class Spell(val cost: Int) {
    def fire(p: Player, b: Boss): (Player, Boss) = {
      (Player(p.armor, p.spells, p.mana - this.cost, p.hp), b)
    }
  }

  case class MagicMissile(damage: Int = 4) extends Spell(53) {
    override def fire(p: Player, b: Boss): (Player, Boss) = {
      (Player(p.armor, p.spells, p.mana - this.cost, p.hp), Boss(b.hp - this.damage))
    }
  }

  case class Drain(heal: Int = 2, damage: Int = 2) extends Spell(73) {
    override def fire(p: Player, b: Boss): (Player, Boss) = {
      (Player(p.armor, p.spells, p.mana - this.cost, p.hp + this.heal), Boss(b.hp - this.damage))
    }
  }

  case class Shield(armor: Int = 7, turns: Int = 6) extends Spell(113) with Turns {
    override def fire(p: Player, b: Boss): (Player, Boss) = {
      (Player(p.armor + this.armor, p.spells, p.mana - this.cost, p.hp), b)
    }

    override def atEnd(p: Player, b: Boss): (Player, Boss) = {
      (Player(p.armor - this.armor, p.spells, p.mana, p.hp), b)
    }
  }

  case class Poison(damage: Int = 3, turns: Int = 6) extends Spell(173) with Turns with EachTurn {
    override def eachTurn(p: Player, b: Boss): (Player, Boss) = {
      (Player(p.armor, p.spells, p.mana, p.hp), Boss(b.hp - this.damage))
    }
  }

  case class Recharge(mana: Int = 101, turns: Int = 5) extends Spell(229) with Turns with EachTurn {
    override def eachTurn(p: Player, b: Boss): (Player, Boss) = {
      (Player(p.armor, p.spells, p.mana + this.mana, p.hp), b)
    }
  }

}
