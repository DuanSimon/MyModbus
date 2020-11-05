package com.simon.modbus4j.msg;

import com.simon.modbus4j.Modbus;
import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;
import com.sun.org.apache.xpath.internal.operations.Mod;

public class WriteRegisterRequest extends ModbusRequest {
    private int writeOffset;
    private int writeValue;

    public WriteRegisterRequest(int slaveId, int writeOffset, int writeValue) throws ModbusTransportException{
        super(slaveId);
        this.writeOffset = writeOffset;
        this.writeValue = writeValue;
    }

    @Override
    public void validate(Modbus modbus) throws ModbusTransportException{
        ModbusUtils.validateOffset(writeOffset);
    }

    WriteRegisterRequest(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    protected void writeRequest(ByteQueue queue){
        ModbusUtils.pushShort(queue, writeOffset);
        ModbusUtils.pushShort(queue, writeValue);
    }

    @Override
    ModbusResponse handleImpl(ProcessImage processImage) throws ModbusTransportException{
        processImage.writeHoldingRegister(writeOffset, (short) writeValue);
        return new WriteRegisterResponse(slaveId, writeOffset, writeValue);
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.WRITE_REGISTER;
    }

    @Override
    ModbusResponse getResponseInstance(int slaveId) throws ModbusTransportException{
        return new WriteRegisterResponse(slaveId);
    }

    @Override
    protected void readRequest(ByteQueue queue){
        writeOffset = ModbusUtils.popUnsignedShort(queue);
        writeValue = ModbusUtils.popUnsignedShort(queue);
    }
}
