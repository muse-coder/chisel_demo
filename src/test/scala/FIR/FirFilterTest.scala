package FIR

import chisel3._
import chisel3.tester._
import chiseltest.ChiselScalatestTester
import org.scalatest.flatspec.AnyFlatSpec
class ScalaFirFilter(taps :Seq[Int]) {
  var pseudoRegisters: List[Int] = List.fill(taps.length)(0)
  def poke(value :Int): Int = {
//    pseudoRegisters = (value :: pseudoRegisters.take(taps.length-1))
    pseudoRegisters = (value :: pseudoRegisters.take(taps.length-1))
    var sum = 0
    for (i <- taps.indices){
      sum += taps(i) * pseudoRegisters(i)
    }
    return sum
  }
}

class FirFilterTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "FirFilterTest"
  // test class body here
  it should "do something" in {
    def r(): Int = {
      scala.util.Random.nextInt(1024)
    }
    /**
     * run a test comparing software and hardware filters
     * run for at least twice as many samples as taps
     */
    def runOneTest(taps: Seq[Int]): Unit = {
      val goldenModel = new ScalaFirFilter(taps)

      test(new FirFilter(taps, 32)) { c =>
        for(i <- 0 until 2 * taps.length) {
          val input = r()

          val goldenModelResult = goldenModel.poke(input)

          c.io.in.poke(input.U)

          c.io.out.expect(goldenModelResult.U, s"i $i, input $input, gm $goldenModelResult, ${c.io.out.peek().litValue}")
          println(s"goldenModelResult: $goldenModelResult , chiselResult: ${c.io.out.peek().litValue}")
          c.clock.step(1)
        }
      }
    }

    for(tapSize <- 6 until 100 by 10) {
      val taps = Seq.fill(tapSize)(r())  // create a sequence of random coefficients
      runOneTest(taps)
    }
    println("Success")
  }
}


