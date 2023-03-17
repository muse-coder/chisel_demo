package FIR
import scala.math.{pow, sin, Pi}
import chisel3._
import chisel3.tester._
import chisel3._
import chisel3.util._
import chisel3.tester._
import chisel3.tester.RawTester.test
import chisel3.experimental._
import chisel3.internal.firrtl.KnownBinaryPoint
import scala.collection._
import breeze.signal.{filter, OptOverhang}
import breeze.signal.support.{CanFilter, FIRKernel1D}
import breeze.linalg.DenseVector
import scala.collection.immutable.Seq
import scala.math.{abs, round, cos, Pi, pow}
import chiseltest.ChiselScalatestTester
import org.scalatest.flatspec.AnyFlatSpec

class ConciseFirFilterTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "ConciseFirFilterTest"
  // test class body here
  it should "do something" in {
    /**
     * run a test comparing software and hardware filters
     * run for at least twice as many samples as taps
     */
    val TriangularWindow: (Int, Int) => Seq[Int] = (length, bitwidth) => {
      val raw_coeffs = (0 until length).map((x: Int) => 1 - abs((x.toDouble - (length - 1) / 2.0) / ((length - 1) / 2.0)))
      val scaled_coeffs = raw_coeffs.map((x: Double) => round(x * pow(2, bitwidth)).toInt)
      scaled_coeffs
    }

    // Hamming window
    val HammingWindow: (Int, Int) => Seq[Int] = (length, bitwidth) => {
      val raw_coeffs = (0 until length).map((x: Int) => 0.54 - 0.46 * cos(2 * Pi * x / (length - 1)))
      val scaled_coeffs = raw_coeffs.map((x: Double) => round(x * pow(2, bitwidth)).toInt)
      scaled_coeffs
    }

    val length = 7
    val bitwidth = 12 // must be less than 15, otherwise Int can't represent the data, need BigInt
    val window = TriangularWindow

    // test our FIR
    test(new ConciseFirFilter(length, bitwidth, window)) { c =>

      // test data
      val n = 100 // input length
      val sine_freq = 10
      val samp_freq = 100

      // sample data, scale to between 0 and 2^bitwidth
      val max_value = pow(2, bitwidth)-1
      val sine = (0 until n).map(i => (max_value/2 + max_value/2*sin(2*Pi*sine_freq/samp_freq*i)).toInt)
      //println(s"input = ${sine.toArray.deep.mkString(", ")}")

      // coefficients
      val coeffs = window(length, bitwidth)
      //println(s"coeffs = ${coeffs.toArray.deep.mkString(", ")}")

      // use breeze filter as golden model; need to reverse coefficients
      val expected = filter(
        DenseVector(sine.toArray),
        FIRKernel1D(DenseVector(coeffs.reverse.toArray), 1.0, ""),
        OptOverhang.None
      )
      expected.toArray // seems to be necessary
      //println(s"exp_out = ${expected.toArray.deep.mkString(", ")}") // this seems to be necessary

      // push data through our FIR and check the result
      c.reset.poke(true.B)
      c.clock.step(5)
      c.reset.poke(false.B)
      for (i <- 0 until n) {
        c.io.in.poke(sine(i).U)
        if (i >= length-1) { // wait for all registers to be initialized since we didn't zero-pad the data
          val expectValue = expected(i-length+1)
          //println(s"expected value is $expectValue")
          c.io.out.expect(expected(i-length+1).U)
          //println(s"cycle $i, got ${c.io.out.peek()}, expect ${expected(i-length+1)}")
        }
        c.clock.step(1)
      }
    }
  }
}


