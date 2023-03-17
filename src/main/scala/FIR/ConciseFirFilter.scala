package FIR

import chisel3._

import scala.collection._
import scala.collection.immutable.Seq
import scala.math.{abs, round, cos, Pi, pow}

// simple triangular window

class ConciseFirFilter(length: Int, bitwidth: Int, window: (Int, Int) => Seq[Int]) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(bitwidth.W))
    val out = Output(UInt((bitwidth*2+length-1).W)) // expect bit growth, conservative but lazy
  })

  // calculate the coefficients using the provided window function, mapping to UInts
  val coeffs = window(length, bitwidth).map(_.U)

  // create an array holding the output of the delays
  // note: we avoid using a Vec here since we don't need dynamic indexing
  val delays = Seq.fill(length)(Wire(UInt(bitwidth.W))).scan(io.in){ case(prev: UInt, next: UInt)=> {
    next := RegNext(prev)
    next
    }
  }

  // multiply, putting result in "mults"
  val mults = delays.zip(coeffs).map{ case(delay: UInt, coeff: UInt) => delay * coeff }

  // add up multiplier outputs with bit growth
  val result = mults.reduce(_+&_)

  // connect output
  io.out := result
}

object ConciseFirFilter_u extends App {

  val TriangularWindow: (Int, Int) => Seq[Int] = (length, bitwidth) => {
    val raw_coeffs = (0 until length).map( (x:Int) => 1-abs((x.toDouble-(length-1)/2.0)/((length-1)/2.0)) )
    val scaled_coeffs = raw_coeffs.map( (x: Double) => round(x * pow(2, bitwidth)).toInt)
    scaled_coeffs
  }

  // Hamming window
  val HammingWindow: (Int, Int) => Seq[Int] = (length, bitwidth) => {
    val raw_coeffs = (0 until length).map( (x: Int) => 0.54 - 0.46*cos(2*Pi*x/(length-1)))
    val scaled_coeffs = raw_coeffs.map( (x: Double) => round(x * pow(2, bitwidth)).toInt)
    scaled_coeffs
  }


  val length = 7
  val bitwidth = 12 // must be less than 15, otherwise Int can't represent the data, need BigInt
  val window = TriangularWindow

  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new  ConciseFirFilter(length, bitwidth, window),
      Array(
        "--target-dir", "output/FIR/"+"ConciseFirFilter"
      )
    )
  )
}
//