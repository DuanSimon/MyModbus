package com.simon.modbus4j.msg;

import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;

public class ReadCoilsRequest extends ReadBinaryRequest {
    public ReadCoilsRequest(int slaveId, int startOffset, int numberOfBits) throws ModbusTransportException{
        super(slaveId, startOffset, numberOfBits);
    }

    RedaCoilsRequest(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.READ_COILS;
    }

    @Override
    ModbusResponse handleImpl(ProcessImage processImage) throws ModbusTransportException{
        return new ReadCoilsResponse(slaveId, getData(processImage));
    }

    @Override
    protected boolean getBinary(ProcessImage processImage, int index) throws ModbusTransportException{
        return processImage.getCoil(index);
    }

    @Override
    ModbusResponse getResponseInstance(int slaveId) throws ModbusTransportException{
        return new ReadCoilsResponse(slaveId);
    }

    @Override
    public String toString(){
        return "ReadCoilsRequest [slaveId=" + slaveId + ", getFunctionCode()" + getFunctionCode() + ", toString()=" + super.toString() + "]";
    }
}
