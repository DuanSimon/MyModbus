package com.simon.modbus4j.msg;

import com.simon.modbus4j.Modbus;
import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class WriteCoilRequest extends ModbusRequest {
    private int writeOffset;
    private boolean writeValue;

    public WriteCoilRequest(int slvaveId, int writeOffset, boolean writeValue) throws ModbusTransportException{
        super(slvaveId);
        this.writeOffset = writeOffset;
        this.writeValue = writeValue;
    }

    @Override
    public void validate(Modbus modbus) throws ModbusTransportException{
        ModbusUtils.validateOffset(writeOffset);
    }

    WriteCoilRequest(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    protected void writeRequest(ByteQueue queue){
        ModbusUtils.pushShort(queue, writeOffset);
        ModbusUtils.pushShort(queue, writeValue ? 0xff00 : 0);
    }

    @Override
    ModbusResponse handleImpl(ProcessImage processImage) throws ModbusTransportException{
        processImage.writeCoil(writeOffset, writeValue);
        return new WriteCoilResponse(slaveId, writeOffset, writeValue);
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.WRITE_COIL;
    }

    @Override
    ModbusResponse getResponseInstance(int slaveId) throws ModbusTransportException{
        return new WriteCoilResponse(slaveId);
    }

    @Override
    protected void readRequest(ByteQueue queue){
        writeOffset = ModbusUtils.popUnsignedShort(queue);
        writeValue = ModbusUtils.popUnsignedShort(queue) == 0xff00;
    }
}
