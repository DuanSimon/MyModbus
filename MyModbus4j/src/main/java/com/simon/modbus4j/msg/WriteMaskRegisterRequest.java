package com.simon.modbus4j.msg;

import com.simon.modbus4j.Modbus;
import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusIdException;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class WriteMaskRegisterRequest extends ModbusRequest {
    private int writeOffset;

    private int andMask;

    private int orMask;

    public WriteMaskRegisterRequest(int slaveId, int writeOffset) throws ModbusTransportException{
        this(slaveId, writeOffset, 0xffff, 0);
    }

    public WriteMaskRegisterRequest(int slaveId, int writeOffset, int andMask, int orMask) throws ModbusTransportException{
        super(slaveId);
        this.writeOffset = writeOffset;
        this.andMask = andMask;
        this.orMask = orMask;
    }

    @Override
    public void validate(Modbus modbus) throws ModbusTransportException{
        ModbusUtils.validateOffset(writeOffset);
    }

    public void setBit(int bit, boolean value){
        if(bit < 0 || bit > 15){
            throw new ModbusIdException("Bit must be between 0 and 15 inclusive");
        }
        andMask = andMask & ~(1 << bit);

        if(value){
            orMask = orMask | 1 << bit;
        }else{
            orMask = orMask & ~(1 << bit);
        }
    }

    WriteMaskRegisterRequest(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    protected void writeRequest(ByteQueue queue){
        ModbusUtils.pushShort(queue, writeOffset);
        ModbusUtils.pushShort(queue, andMask);
        ModbusUtils.pushShort(queue, orMask);
    }

    @Override
    ModbusResponse handleImpl(ProcessImage processImage) throws ModbusTransportException{
        short value = processImage.getHoldingRegister(writeOffset);
        value = (short)((value & andMask) | (orMask & (~andMask)));
        processImage.writeHoldingRegister(writeOffset, value);
        return new WriteMaskRegisterResponse(slaveId, writeOffset, andMask, orMask);
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.WRITE_MASK_REGISTER;
    }

    @Override
    ModbusResponse getResponseInstance(int slaveId) throws ModbusTransportException{
        return new WriteMaskRegisterResponse(slaveId);
    }

    @Override
    protected void readRequest(ByteQueue queue){
        writeOffset = ModbusUtils.popUnsignedShort(queue);
        andMask = ModbusUtils.popUnsignedShort(queue);
        orMask = ModbusUtils.popUnsignedShort(queue);
    }
}
