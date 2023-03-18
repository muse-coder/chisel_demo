package ParameterizeModule
import chisel3._
import chisel3.util._

//creat a chisel mux

def MyMux [T <:Data](sel:Bool,TrueSide:T, FalseSide:T):T = {
  val  Result = Wire(FalseSide.cloneType)
  when(sel){
    Result := TrueSide
  }.otherwise{
    Result := FalseSide
  }
    Result
}

class ComplexBus extends Bundle{
  val  data = UInt(10.W)
  val  en = Bool()
}
class ParamCollection [T<:Data] extends Module {
    val busA = Wire(new ComplexBus())
    val busB = Wire(new ComplexBus())
    busA.data := 32.U
    busA.en := true.B
    busB.data := 0.U
    busB.en := false.B
    val sel = Bool()
    val res = MyMux(sel , TrueSide = busA ,FalseSide = busB)
}
