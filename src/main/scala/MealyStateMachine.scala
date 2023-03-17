import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.experimental.BundleLiterals._


import scala.language.experimental.macros

import chisel3.internal._
import chisel3.internal.firrtl._
import chisel3.internal.sourceinfo.SourceInfo
case class MealyParams(
                      stateNum: Int,
                      initialSate:  Int,
                      stateTransition:  (Int , Boolean) => Int,
                      output: (Int , Boolean) => Int
                      ){
  require(stateNum >=0 , s"stateNum $stateNum must >= 0")
  require(initialSate < stateNum && initialSate >=0, s"initialState error ")

}


class MealyStateMachine (val mealyParams: MealyParams) extends Module {
  val io = IO(new Bundle() {
    val in: Bool = Input(Bool())
    val out: UInt = Output(UInt())
  })
  private val currentState = RegInit( UInt(1.W) , mealyParams.initialSate.U )
  io.out := 0.U
  for (i <- 0 until mealyParams.stateNum){
    when(currentState === i.U){
      when(io.in === true.B){
        currentState := mealyParams.stateTransition(i, true ).U
        io.out := mealyParams.output(i,true).U
      }.otherwise{
        currentState := mealyParams.stateTransition(i, false).U
        io.out := mealyParams.output(i, false).U
      }
    }
  }

}

object MealyStateMachine extends App {
  // These lines generate the Verilog output
  val nStates = 3
  val s0 = 2

  //
  def stateTransition(currentState: Int, in: Boolean): Int = {
    if (in) {
      1
    }
    else {
      0
    }
  }

  def output(state: Int, in: Boolean): Int = {
    if (state == 2) {
      return 0
    }
    if ((state == 1 && !in) || (state == 0 && in)) {
      return 1
    } else {
      return 0
    }
  }

  val testParams = MealyParams(stateNum = nStates, initialSate = s0, stateTransition = stateTransition, output = output)

  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new MealyStateMachine(testParams),
      Array(
        "--target-dir", "output/"+"MealyStateMachine"
      )
    )
  )
}
