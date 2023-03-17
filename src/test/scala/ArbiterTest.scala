import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ArbiterTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "ArbiterTest"
  // test class body here
  it should "do something" in {
    // test case body here
    test(new Arbiter) { c =>
      // verify that the max of the three inputs is correct
      c.io.in1.poke(6.U)
      c.io.in2.poke(4.U)
      c.io.in3.poke(2.U)
      c.io.out.expect(6.U)  // input 1 should be biggest
      c.io.in2.poke(7.U)
      c.io.out.expect(7.U)  // now input 2 is
      c.io.in3.poke(11.U)
      c.io.out.expect(11.U) // and now input 3
      c.io.in3.poke(3.U)
      c.io.out.expect(7.U)  // show that decreasing an input works as well
      c.io.in1.poke(9.U)
      c.io.in2.poke(9.U)
      c.io.in3.poke(6.U)
      c.io.out.expect(9.U)  // still get max with tie
    }
  }
}
