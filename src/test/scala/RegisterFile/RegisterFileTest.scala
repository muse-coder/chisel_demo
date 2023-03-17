package RegisterFile
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
class RegisterFileTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "RegisterFileTest"
  // test class body here
  it should "do something" in {
    // test case body here
    test(new RegisterFile_1(readPorts = 2,depth = 64,dataWidth = 32)) { c =>
      def readExpect(raddr: Int, value: Int, port: Int): Unit = {
        c.io.raddr(port).poke(raddr.U)
        c.io.rdata(port).expect(value.U)
      }
      def write(waddr:Int, value :Int ): Unit = {
        c.io.wen.poke(true.B)
        c.io.wdata.poke(value.U)
        c.io.waddr.poke(waddr.U)
        c.clock.step(1)
        c.io.wen.poke(false.B)
    }
      for (i<- 0 until 32){
        readExpect(raddr = i, value = 0, port = 0)
        readExpect(raddr = i, value = 0, port = 1)
      }
      for (i<- 0 until 32){
        write(waddr = i,value = 5 * i +3)
      }
      for (i <- 0 until 32) {
//        if(i ==16)
//          readExpect(raddr = i,value = ( if (i==0) 0 else 5*i+6),port = i%2)
//        else
          readExpect(raddr = i, value = (if (i == 0) 0 else 5 * i + 3), port = i % 2)
      }
    }
  }
}
