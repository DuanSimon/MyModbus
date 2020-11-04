package com.simon.modbus4j.msg;

import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;

public class ReadHoldingRegistersResponse extends ReadResponse {
    ReadHoldingRegistersResponse(int slaveId, byte[] data) throws ModbusTransportException{
        super(slaveId, data);
    }

    ReadHoldingRegistersResponse(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.READ_HOLDING_REGISTERS;
    }

    @Override
    public String toString(){
        return "ReadHoldingRegistersResponse [exceptionCode=" + exceptionCode + ", slaveId=" + slaveId
                + ", getFunctionCode()=" + getFunctionCode() + ", isException()=" + isException()
                + ", getExceptionMessage()=" + getExceptionMessage() + ", getExceptionCode()=" + getExceptionCode()
                + ", toString()=" + super.toString(true) + "]";
    }
}
