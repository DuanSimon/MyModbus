package com.simon.modbus4j.msg;

import com.simon.modbus4j.Modbus;
import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.ShouldNeverHappenException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class ExceptionRequest extends ModbusRequest {
    private final byte functionCode;
    private final byte exceptionCode;

    public ExceptionRequest(int slaveId, byte functionCode, byte exceptionCode) throws ModbusTransportException{
        super(slaveId);
        this.functionCode = functionCode;
        this.exceptionCode = exceptionCode;
    }

    public void validate(Modbus modbus){

    }

    @Override
    protected void writeRequest(ByteQueue queue){
        throw new ShouldNeverHappenException("wha");
    }

    @Override
    protected void readRequest(ByteQueue queue){
        queue.clear();
    }

    @Override
    ModbusResponse getResponseInstance(int slaveId) throws ModbusTransportException{
        return new ExceptionResponse(slaveId, functionCode, exceptionCode);
    }

    @Override
    ModbusResponse handleImpl(ProcessImage processImage) throws ModbusTransportException{
        return getResponseInstance(slaveId);
    }

    @Override
    public byte getFunctionCode(){
        return functionCode;
    }

    public byte getExceptionCode(){
        return exceptionCode;
    }
}
