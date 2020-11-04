package com.simon.modbus4j.msg;

import com.simon.modbus4j.Modbus;
import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;
import com.sun.org.apache.xpath.internal.operations.Mod;

public class WriteRegistersRequest extends ModbusRequest {
    private int startOffset;
    private byte[] data;

    public WriteRegistersRequest(int slaveId, int startOffset, short[] sdata) throws ModbusTransportException{
        super(slaveId);
        this.startOffset = startOffset;
        data = convertToBytes(sdata);
    }

    @Override
    public void validate(Modbus modbus) throws ModbusTransportException{
        ModbusUtils.validateOffset(startOffset);
        int registerCount = data.length / 2;
        if(registerCount < 1 || registerCount > modbus.getMaxWriteRegisterCount()){
            throw new ModbusTransportException("Invalid number of registers: " + registerCount, slaveId);
        }
        ModbusUtils.validateEndOffset(startOffset + registerCount - 1);
    }

    WriteRegistersRequest(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    protected void writeRequest(ByteQueue queue){
        ModbusUtils.pushShort(queue, startOffset);
        ModbusUtils.pushShort(queue, data.length / 2);
        ModbusUtils.pushByte(queue, data.length);
        queue.push(data);
    }

    @Override
    ModbusResponse handleImpl(ProcessImage processImage) throws ModbusTransportException{
        short[] sdata = convertToShorts(data);
        for (int i = 0; i < sdata.length; i++) {
            processImage.writeHoldingRegister(startOffset + i, sdata[i]);
        }
        return new WriteRegistersResponse(slaveId, startOffset, sdata.length);
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.WRITE_REGISTERS;
    }

    @Override
    ModbusResponse getResponseInstance(int slaveId) throws ModbusTransportException{
        return new WriteRegistersResponse(slaveId);
    }

    @Override
    protected void readRequest(ByteQueue queue){
        startOffset = ModbusUtils.popUnsignedShort(queue);
        ModbusUtils.popUnsignedShort(queue);
        data = new byte[ModbusUtils.popUnsignedByte(queue)];
        queue.pop(data);
    }
}
