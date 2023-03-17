package AsyncFIFOPackage
import chisel3._
import chisel3.util._
import chisel3.experimental._

import scala.collection.mutable
class GrayCoder (bitWidth : Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(bitWidth.W))
    val encode = Input(Bool())
    val out = Output(UInt(bitWidth.W))
  })
    val tmp = mutable.ArrayBuffer[UInt]()
  when (io.encode) { //encode
    io.out := io.in ^ Cat(0.U(1.W),io.in(bitWidth-1,1))
  }.otherwise {
    for (i <- (bitWidth - 1) to 0 by -1) {
      if (i == bitWidth - 1)
        tmp += io.in(i)
      else
        tmp += tmp(bitWidth - i - 2) ^ io.in(i)
    }
    io.out := tmp.reduce((a, b) => Cat(a, b))
  }
}
object GrayCoder_u extends App{
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new GrayCoder(5),
      Array(
        "--target-dir","output/"+"AsyncFIFO/Graycoder"
      )
    )
  )
}