package com.simon.modbus4j.msg;

import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;

public class ReadCoilsResponse extends ReadResponse {
    ReadCoilsResponse(int slaveId, byte[] data) throws ModbusTransportException {
        super(slaveId, data);
    }

    ReadCoilsResponse(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.READ_COILS;
    }

    @Override
    public String toString(){
        return "ReadCoilsResponse [exceptionCode=" + exceptionCode + ", slaveId=" + slaveId + ", getFunctionCode()=" + getFunctionCode() + ", isException()=" + isException() + ",getExceptionMessage()=" + getExceptionMessage() + ", getExceptionCode()=" + getExceptionCode() + ", toString()=" + super.toString(false) + "]";
    }
}
