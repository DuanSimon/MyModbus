package com.simon.modbus4j.msg;

import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;

public class ReadDiscreteInputsResponse extends ReadResponse {
    ReadDiscreteInputsResponse(int slaveId, byte[] data) throws ModbusTransportException{
        super(slaveId, data);
    }

    ReadDiscreteInputsResponse(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.READ_DISCRETE_INPUTS;
    }

    @Override
    public String toString(){
        return "ReadDiscreteInputResponse [exceptionCode=" + exceptionCode + ", slaveId=" + slaveId
                + ", getFunctionCode()=" + getFunctionCode() + ", isException()=" + isException()
                + ", getExceptionMessage()=" + getExceptionMessage() + ", getExceptionCode()=" + getExceptionCode()
                + super.toString(false) + "]";
    }
}
