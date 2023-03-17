import chisel3._
import chisel3.util._
import chisel3.experimental._
import chisel3.internal.firrtl.KnownBinaryPoint
class NeuronNetwork(inputs: Int, act: FixedPoint => FixedPoint) extends Module {
  val io = IO(new Bundle {
    val in      = Input(Vec(inputs, FixedPoint(16.W, 8.BP)))
    val weights = Input(Vec(inputs, FixedPoint(16.W, 8.BP)))
    val out     = Output(FixedPoint(16.W, 8.BP))
  })
  val sum = io.in.zip(io.weights).map{
    case (a , b) => a * b
  }.reduce(_ +& _)
  io.out := act(sum)
}

object NeuronNetwork_inst  extends  App{

  val Step: FixedPoint => FixedPoint = { x => Mux(x <= 0.F(8.BP), 0.F(8.BP), 1.F(8.BP)) }
  val ReLU: FixedPoint => FixedPoint = { x => Mux(x <= 0.F(8.BP), 0.F(8.BP), x) }
  println(
    new(chisel3.stage.ChiselStage).emitVerilog(
      new NeuronNetwork(2 , ReLU) ,
      Array(
        "--target-dir", "output/" + "NeuronNetwork"
      )
    )
  )
}