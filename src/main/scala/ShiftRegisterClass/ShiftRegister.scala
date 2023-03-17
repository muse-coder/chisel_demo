package ShiftRegisterClass

import chisel3._
import chisel3.util._
class ShiftRegister(val n: Int, val init: BigInt = 1) extends Module {
  val io = IO(new Bundle {
    val en  = Input(Bool())
    val in  = Input(Bool())
    val out = Output(UInt(n.W))
  })

  val state = RegInit(init.U(n.W))
  val nextState = Cat(state(n-1,0),io.in)
  when(io.en === true.B){
      state := nextState
  }.otherwise(
    state := state
  )
  io.out := state
  printf("in: %b state: %b  nextState: %b\n",io.in,state,nextState)
}



object ShiftRegister extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new ShiftRegister(n = 6),
      Array(
        "--target-dir", "output/"+"ShiftRegister"
      )
    )
  )
}
