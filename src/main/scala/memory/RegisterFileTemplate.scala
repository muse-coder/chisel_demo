package memory

import chisel3._
import chisel3.util._
import scala.language.experimental.macros
class RegisterFileTemplate [T <: Data ](readPorts : Int ,depth :Int,dataType:T) extends Module {
  require(readPorts >= 0)
  require(isPow2(depth),println("depth must be pow of 2"))
//  require(dataType.asTypeOf(UInt))
  val io = IO(new Bundle {
    val wen = Input(Bool())
    val waddr = Input(UInt(log2Ceil(depth).W))
    val wdata = Input(dataType.cloneType)
    val raddr = Input(Vec(readPorts , UInt(log2Ceil(depth).W)))
    val rdata = Output(Vec(readPorts, dataType.cloneType))
  })
  val reg = Reg(Vec(depth,dataType.cloneType))
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

object RegisterFileTemplate_u extends App{
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new RegisterFileTemplate(readPorts = 2,depth =8,dataType = UInt(32.W)) ,
      Array(
        "--target-dir", "output/" + "memory/template"
      )
    )
  )
}
