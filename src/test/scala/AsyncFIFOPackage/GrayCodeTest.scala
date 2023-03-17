package AsyncFIFOPackage

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import scala.math.pow
class GrayCodeTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "GrayCodeTest"
  // test class body here
  it should "do something" in {
    // test case body here
    val bitwidth = 5
    test(new GrayCoder(bitwidth)) { c =>
      def toBinary(i: Int, digits: Int = 8) = {
        String.format("%" + digits + "s", i.toBinaryString).replace(' ', '0')
      }
      println("Encoding:")
      for (i <- 0 until pow(2, bitwidth).toInt) {
        c.io.in.poke(i.U)
        c.io.encode.poke(true.B)
        c.clock.step(1)
        println(s"Binary = ${toBinary(i, bitwidth)}, Gray = ${toBinary(c.io.out.peek().litValue.toInt, bitwidth)}")
      }

      println("Decoding:")
      for (i <- 0 until pow(2, bitwidth).toInt) {
        c.io.in.poke(i.U)
        c.io.encode.poke(false.B)
        c.clock.step(1)
        println(s"Gray = ${toBinary(i, bitwidth)}, Binary = ${toBinary(c.io.out.peek().litValue.toInt, bitwidth)}")
      }

    }
  }
}
