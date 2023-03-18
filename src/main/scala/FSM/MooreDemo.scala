package FSM
import chisel3._
import chisel3.util._
import firrtl.Utils.True

import scala.collection.immutable
class MooreDemo extends Module {
  val io = IO(new Bundle() {
    val badEvent = Input(Bool())
    val clear = Input(Bool())
    val ringBell = Output(Bool())

  })
  val green :: orange :: red :: Nil = Enum(3)
  val stateReg = RegInit(green)
//use chisel3 to create moore state machine
  //

  switch(stateReg){
    is(green){
      when( Cat(io.badEvent,io.clear) ===("b00".U) ) {
        stateReg := green
      }.elsewhen(io.badEvent === ("b1".U)){
        stateReg := orange
      }
    }

    is(orange){
      when(Cat(io.badEvent, io.clear) === ("b00".U)) {
        stateReg := orange
      }.elsewhen(io.badEvent === ("b1".U)) {
        stateReg := red
      }

    }
    is(red) {
      when(Cat(io.badEvent, io.clear) === ("b00".U)) {
          stateReg := red
      }.elsewhen(io.badEvent === ("b1".U)) {
          stateReg := green
      }
    }
  }
  io.ringBell := (stateReg=== red)
}


object MooreDemo_u extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new MooreDemo,
      Array(
        "--target-dir", "output/"+"FSM/MooreDemo"
      )
    )
  )
}
//