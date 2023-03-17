package ShiftRegisterClass

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ShiftRegisterTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "ArbiterTest"
  // test class body here
  it should "do something" in {
    // test case body here
    for (i <- Seq(3, 4, 8, 24, 65)) {
      println(s"Testing n=$i")
      test(new ShiftRegister(n = i)) { c =>
        val inSeq = Seq(0, 1, 1, 1, 0, 1, 1, 0, 0, 1)
        var state = c.init
        var i = 0
        c.io.en.poke(true.B)
        while (i < 10 * c.n) {
          // poke in repeated inSeq
          val toPoke = inSeq(i % inSeq.length)
          c.io.in.poke((toPoke != 0).B)
          // update expected state
          state = ((state * 2) + toPoke) & BigInt("1" * c.n, 2)
          c.clock.step(1)
          c.io.out.expect(state.U)

          i += 1
        }
      }
      println("Success!")
    }
  }
}
