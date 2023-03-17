package FIR

import chisel3._

import scala.collection._
import scala.collection.immutable.Seq
class FirFilter(consts: Seq[Int] , bitWidth:Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(UInt(bitWidth.W))
    val out = Output(UInt(bitWidth.W))
  })
  val regs = mutable.ArrayBuffer[UInt]()
  for (i<-0 until consts.length){
    if(i == 0 ) regs += io.in
    else        regs += RegNext(regs(i-1) ,0.U)
  }
  val muls = mutable.ArrayBuffer[UInt]()
  for (i<-0 until consts.length){
    muls += regs(i) * consts(i).U
  }
  val scan = mutable.ArrayBuffer[UInt]()
  for (i <- 0 until consts.length){
    if(i ==0 ) scan += muls(i)
    else  scan += muls(i) + scan(i-1)
  }
  io.out := scan.last
}

class MyManyDynamicElementVecFir(length: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
    val consts = Input(Vec(length, UInt(8.W)))
  })

  // Reference solution
  val regs = RegInit(VecInit(Seq.fill(length - 1)(0.U(8.W))))
  for(i <- 0 until length - 1) {
    if(i == 0) regs(i) := io.in
    else       regs(i) := regs(i - 1)
  }

  val muls = Wire(Vec(length, UInt(8.W)))
  for(i <- 0 until length) {
    if(i == 0) muls(i) := io.in * io.consts(i)
    else       muls(i) := regs(i - 1) * io.consts(i)
  }

  val scan = Wire(Vec(length, UInt(8.W)))
  for(i <- 0 until length) {
    if(i == 0) scan(i) := muls(i)
    else scan(i) := muls(i) + scan(i - 1)
  }

  io.out := scan(length - 1)
}

object FirFilter_u extends App {
  // These lines generate the Verilog output
  val taps = Seq.fill(6)(10)
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new FirFilter(taps, 32),
      Array(
        "--target-dir", "output/"+"FirFilter"
      )
    )
  )
}
//
