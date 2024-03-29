package AsyncFIFOPackage
import chisel3._
import chisel3.util._
class AsyncFIFO(depth: Int = 16) extends Module {
  val io = IO(new Bundle{
    // write inputs
    val write_clock = Input(Clock())
    val write_enable = Input(Bool())
    val write_data = Input(UInt(32.W))

    // read inputs/outputs
    val read_clock = Input(Clock())
    val read_enable = Input(Bool())
    val read_data = Output(UInt(32.W))

    // FIFO status
    val full = Output(Bool())
    val empty = Output(Bool())
  })

  // add extra bit to counter to check for fully/empty status
  assert(isPow2(depth), "AsyncFIFO needs a power-of-two depth!")
  val write_counter = withClock(io.write_clock) { Counter(io.write_enable && !io.full, depth*2)._1 }
  val read_counter = withClock(io.read_clock) { Counter(io.read_enable && !io.empty, depth*2)._1 }

  // encode
  val encoder = new GrayCoder(write_counter.getWidth)
  encoder.io.in := write_counter
  encoder.io.encode := true.B

  // synchronize
  val sync = withClock(io.read_clock) { ShiftRegister(encoder.io.out, 2) }

  // decode
  val decoder = new GrayCoder(read_counter.getWidth)
  decoder.io.in := sync
  decoder.io.encode := false.B

  // status logic goes here

}