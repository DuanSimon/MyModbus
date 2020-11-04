package com.simon.modbus4j.msg;

import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class ExceptionResponse extends ModbusResponse {
    private final byte functionCode;

    public ExceptionResponse(int slaveId, byte functionCode, byte exceptionCode) throws ModbusTransportException{
        super(slaveId);
        this.functionCode = functionCode;
        setException(exceptionCode);
    }

    @Override
    public byte getFunctionCode(){
        return functionCode;
    }

    @Override
    protected void readResponse(ByteQueue queue){

    }

    @Override
    protected void writeResponse(ByteQueue queue){

    }
}
