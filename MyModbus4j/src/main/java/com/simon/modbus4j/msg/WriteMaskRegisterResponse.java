package com.simon.modbus4j.msg;

import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;
import com.sun.org.apache.xpath.internal.operations.Mod;

public class WriteMaskRegisterResponse extends ModbusResponse {
    private int writeOffset;
    private int andMask;
    private int orMask;

    @Override
    public byte getFunctionCode(){
        return FunctionCode.WRITE_MASK_REGISTER;
    }

    WriteMaskRegisterResponse(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    WriteMaskRegisterResponse(int slaveId, int writeOffset, int andMask, int orMask) throws ModbusTransportException{
        super(slaveId);
        this.writeOffset = writeOffset;
        this.andMask = andMask;
        this.orMask = orMask;
    }

    @Override
    protected void writeResponse(ByteQueue queue){
        ModbusUtils.pushShort(queue, writeOffset);
        ModbusUtils.pushShort(queue, andMask);
        ModbusUtils.pushShort(queue, orMask);
    }

    @Override
    protected void readResponse(ByteQueue queue){
        writeOffset = ModbusUtils.popUnsignedShort(queue);
        andMask = ModbusUtils.popUnsignedShort(queue);
        orMask = ModbusUtils.popUnsignedShort(queue);
    }

    public int getWriteOffset(){
        return writeOffset;
    }

    public int getAndMask(){
        return andMask;
    }

    public int getOrMask(){
        return orMask;
    }
}
