import chisel3._
//import chisel3.util._
class Arbiter extends Module {
  val io = IO(new Bundle {
    val in1: UInt = Input(UInt(16.W))
    val in2: UInt = Input(UInt(16.W))
    val in3: UInt = Input(UInt(16.W))
    val out: UInt = Output(UInt(16.W))
  })

  when(io.in1 >= io.in2 && io.in1 >= io.in3) {
    io.out := io.in1
  }.elsewhen(io.in2 >= io.in3) {
    io.out := io.in2
  }.otherwise {
    io.out := io.in3
  }
}

object Main extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new Arbiter,
      Array(
        "--target-dir", "output/"+"Arbiter"
      )
    )
  )
}

