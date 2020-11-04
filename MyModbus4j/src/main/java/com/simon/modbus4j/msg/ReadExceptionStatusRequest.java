package com.simon.modbus4j.msg;

import com.simon.modbus4j.Modbus;
import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class ReadExceptionStatusRequest extends ModbusRequest {

    public ReadExceptionStatusRequest(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    public void validate(Modbus modbus){

    }

    @Override
    protected void writeRequest(ByteQueue queue){

    }

    @Override
    protected void readRequest(ByteQueue queue){

    }

    @Override
    ModbusResponse getResponseInstance(int slaveId) throws ModbusTransportException{
        return new ReadExceptionStatusResponse(slaveId);
    }

    @Override
    ModbusResponse handleImpl(ProcessImage processImage) throws ModbusTransportException{
        return new ReadExceptionStatusResponse(slaveId, processImage.getExceptionStatus());
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.READ_EXCEPTION_STATUS;
    }
}
