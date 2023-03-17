package RegisterFile
import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.experimental.BundleLiterals._
import math._

import scala.language.experimental.macros

import chisel3.internal._
import chisel3.internal.firrtl._
import chisel3.internal.sourceinfo.SourceInfo
class RegisterFile_1 (readPorts : Int ,depth :Int,dataWidth:Int) extends Module {
  require(readPorts >= 0)
  require(isPow2(depth),println("depth must be pow of 2"))

  val io = IO(new Bundle {
    val wen = Input(Bool())
    val waddr = Input(UInt(log2Ceil(depth).W))
    val wdata = Input(UInt(dataWidth.W))
    val raddr = Input(Vec(readPorts , UInt(log2Ceil(depth).W)))
    val rdata = Output(Vec(readPorts, UInt(dataWidth.W)))
  })
  val reg = RegInit(VecInit(Seq.fill(depth)(0.U(dataWidth.W))))
  when(io.wen === true.B){
    reg(io.waddr) := io.wdata
  }

  for (i<- 0  until readPorts){
    when(io.raddr(i)===0.U){
      io.rdata(i) := 0.U
    }.otherwise{
      io.rdata(i) := reg(io.raddr(i))
    }
  }
}

object register extends App{
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new RegisterFile_1(readPorts = 2,depth =8,dataWidth = 32) ,
      Array(
        "--target-dir", "output/" + "RegisterFile"
      )
    )
  )
}

//object Main extends App {
//  // These lines generate the Verilog output
//  println(
//    new (chisel3.stage.ChiselStage).emitVerilog(
//      new Arbiter,
//      Array(
//        "--target-dir", "output/"+"Arbiter"
//      )
//    )
//  )
//}
//
