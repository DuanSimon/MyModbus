package com.simon.modbus4j.msg;

import com.simon.modbus4j.Modbus;
import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;

public class ReadInputRegistersRequest extends ReadNumericRequest {

    public ReadInputRegistersRequest(int slaveId, int startOffset, int numberOfRegisters) throws ModbusTransportException{
        super(slaveId, startOffset, numberOfRegisters);
    }

    ReadInputRegistersRequest(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.READ_INPUT_REGISTERS;
    }

    @Override
    ModbusResponse handleImpl(ProcessImage processImage) throws ModbusTransportException{
        return new ReadInputRegistersResponse(slaveId, getData(processImage));
    }

    @Override
    protected short getNumeric(ProcessImage processImage, int index) throws ModbusTransportException{
        return processImage.getInputRegister(index);
    }

    @Override
    ModbusResponse getResponseInstance(int slaveId) throws ModbusTransportException{
        return new ReadInputRegistersResponse(slaveId);
    }

    @Override
    public String toString(){
        return "ReadInputRegistersRequest [slaveId=" + slaveId + ", getFunctionCode()=" + getFunctionCode()
                + ", toString()=" + super.toString() + "]";
    }
}
