import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class QueueTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "QueueTest"
  // test class body here
  it should "do something" in {
    // test case body here
    test(new Queue(UInt(9.W), entries = 200)) { c =>
      // Example testsequence showing the use and behavior of Queue
      c.in.initSource()
      c.in.setSourceClock(c.clock)
      c.out.initSink()
      c.out.setSinkClock(c.clock)

      val testVector = Seq.tabulate(200){ i => i.U }

      testVector.zip(testVector).foreach { case (in, out) =>
        c.in.enqueueNow(in)
        c.out.expectDequeueNow(out)
      }
    }
  }
}
