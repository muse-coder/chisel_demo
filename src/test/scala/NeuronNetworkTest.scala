import chisel3._
import chisel3.experimental.FixedPoint
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class NeuronNetworkTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "NeuronNetworkTest"
  // test class body here
  it should "do something" in {
    val Step: FixedPoint => FixedPoint = { x => Mux(x <= 0.F(8.BP), 0.F(8.BP), 1.F(8.BP)) }
    val ReLU: FixedPoint => FixedPoint = { x => Mux(x <= 0.F(8.BP), 0.F(8.BP), x) }
    test(new NeuronNetwork(2, Step)) { c =>
      val inputs = Seq(Seq(-1, -1), Seq(-1, 1), Seq(1, -1), Seq(1, 1))

      // make this a sequence of two values
      val weights = Seq(2.0,2.0)

      // push data through our Neuron and check the result (AND gate)
      c.reset.poke(true.B)
      c.clock.step(5)
      c.reset.poke(false.B)
      for (i <- inputs) {
        c.io.in(0).poke(i(0).F(8.BP))
        c.io.in(1).poke(i(1).F(8.BP))
        c.io.weights(0).poke(weights(0).F(16.W, 8.BP))
        c.io.weights(1).poke(weights(1).F(16.W, 8.BP))
        c.io.out.expect((if (i(0) + i(1) > 0) 1 else 0).F(16.W, 8.BP))
        c.clock.step(1)
      }

    }
  }
}
