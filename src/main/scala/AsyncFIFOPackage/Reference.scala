package AsyncFIFOPackage
import chisel3._
import chisel3.util._
//import math
import scala.math.pow
class Reference(bitwidth: Int) extends Module {
  val io = IO(new Bundle{
    val in = Input(UInt(bitwidth.W))
    val out = Output(UInt(bitwidth.W))
    val encode = Input(Bool()) // decode on false
  })
//  io.in.litValue>>1
  when (io.encode) { //encode
    io.out := io.in ^ (io.in >> 1.U).asUInt
  } .otherwise { // decode, much more complicated
    io.out := Seq.fill(log2Ceil(bitwidth))(Wire(UInt(bitwidth.W))).zipWithIndex.fold((io.in, 0)){
      case ((w1: UInt, i1: Int), (w2: UInt, i2: Int)) => {
        w2 := w1 ^ (w1 >> pow(2, log2Ceil(bitwidth)-i2-1).toInt.U).asUInt
        (w2, i1)
      }
    }._1
  }
}