//package memory
//
//import chisel3._
//import chisel3.util._
//import scala.language.experimental.macros
//class DualPortMem[T <: Data](addrWidth: T, dataType: T, depth: Int) extends Module {
//  val io = IO(new Bundle {
//    val raddr = Input(addrWidth.cloneType)
//    val rdata = Output(dataType.cloneType)
//    val waddr = Input(addrWidth.cloneType)
//    val wdata = Input(dataType.cloneType)
//    val wen = Input(Bool())
//  })
//
//  val mem = SyncReadMem(depth, dataType)
//  val writeReg = RegNext(io.wdata)
//
//  when(io.wen) {
//    mem.write(io.waddr, io.wdata)
//  }
//}
