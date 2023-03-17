import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class MealyStateMachineTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "ArbiterTest"
  // test class body here
  it should "do something" in {
    val nStates = 3
    val s0 =2
//
    def stateTransition(currentState: Int, in: Boolean): Int = {
      if (in) {
        1
      }
      else {
        0
      }
    }

    def output(state: Int, in: Boolean): Int = {
      if (state == 2) {
        return 0
      }
      if ((state == 1 && !in) || (state == 0 && in)) {
        return 1
      } else {
        return 0
      }
    }
    val testParams = MealyParams(stateNum= nStates, initialSate = s0, stateTransition = stateTransition, output = output)

    test(new MealyStateMachine(testParams)) { c =>
      c.io.in.poke(false.B)
      c.io.out.expect(0.U)
      c.clock.step(1)
      c.io.in.poke(false.B)
      c.io.out.expect(0.U)
      c.clock.step(1)
      c.io.in.poke(false.B)
      c.io.out.expect(0.U)
      c.clock.step(1)
      c.io.in.poke(true.B)
      c.io.out.expect(1.U)
      c.clock.step(1)
      c.io.in.poke(true.B)
      c.io.out.expect(0.U)
      c.clock.step(1)
      c.io.in.poke(false.B)
      c.io.out.expect(1.U)
      c.clock.step(1)
      c.io.in.poke(true.B)
      c.io.out.expect(1.U)
      c.clock.step(1)
      c.io.in.poke(false.B)
      c.io.out.expect(1.U)
      c.clock.step(1)
      c.io.in.poke(true.B)
      c.io.out.expect(1.U)
    }

    println("SUCCESS!!") // Scala Code: if we get here, our tests passed!
  }
}
