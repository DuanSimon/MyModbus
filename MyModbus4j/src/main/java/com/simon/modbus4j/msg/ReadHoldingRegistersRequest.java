package com.simon.modbus4j.msg;

import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;

public class ReadHoldingRegistersRequest extends ReadNumericRequest {

    public ReadHoldingRegistersRequest(int slaveId, int startOffset, int numberOfRegisters) throws ModbusTransportException{
        super(slaveId, startOffset, numberOfRegisters);
    }

    ReadHoldingRegistersRequest(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.READ_HOLDING_REGISTERS;
    }

    @Override
    ModbusResponse handleImpl(ProcessImage processImage) throws ModbusTransportException{
        return new ReadHoldingRegistersResponse(slaveId, getData(processImage));
    }

    @Override
    protected short getNumeric(ProcessImage processImage, int index) throws ModbusTransportException{
        return processImage.getHoldingRegister(index);
    }

    @Override
    ModbusResponse getResponseInstance(int slaveId) throws ModbusTransportException{
        return new ReadHoldingRegistersResponse(slaveId);
    }

    @Override
    public String toString(){
        return "ReadHoldingRegistersRequest [slaveId=" + slaveId + ", getFunctionCode()=" + getFunctionCode()
                + ", toString()=" + super.toString() + "]";
    }
}
