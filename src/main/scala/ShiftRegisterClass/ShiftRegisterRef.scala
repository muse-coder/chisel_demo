package ShiftRegisterClass

import chisel3._
import chisel3.util._
class ShiftRegisterIO[T <: Data](gen: T, n: Int) extends Bundle {
  require (n >= 0, "Shift register must have non-negative shift")

  val in = Input(gen)
  val out = Output(Vec(n + 1, gen)) // + 1 because in is included in out
//  override def cloneType: this.type = (new ShiftRegisterIO(gen, n)).asInstanceOf[this.type]
}

//class ShiftRegisterRef[T <: Data](gen: T, n: Int) extends Module {
//  val io = IO(new ShiftRegisterIO(gen, n))
//
//  io.out.foldLeft(io.in) { case (in, out) =>
//    out := in
//    RegNext(in)
//  }
//}

class ShiftRegisterRef[T <: Data](gen: T, n: Int) extends Module {
  val io = IO(new ShiftRegisterIO(gen, n))
  val regs = Reg(Vec(n+1,gen))
  for (i <- 0 until  n+1){
    if(i==0 ) regs(i):=io.in
    else
      regs(i) := regs(i-1)
  }
  io.out := regs
//  io.out.foldLeft(io.in) { case (in, out) =>
//    out := in
//    RegNext(in)
//  }
}


object ShiftRegisterRef extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new ShiftRegisterRef(UInt(4.W),5),
      Array(
        "--target-dir", "output/"+"ShiftRegister/ShiftRegisterRef"
      )
    )
  )
}
