package com.simon.modbus4j.msg;

import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class WriteCoilResponse extends ModbusResponse {
    private int writeOffset;
    private boolean writeValue;

    @Override
    public byte getFunctionCode(){
        return FunctionCode.WRITE_COIL;
    }

    WriteCoilResponse(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    WriteCoilResponse(int slaveId, int writeOffset, boolean writeValue) throws ModbusTransportException{
        super(slaveId);
        this.writeOffset = writeOffset;
        this.writeValue = writeValue;
    }

    @Override
    protected void writeResponse(ByteQueue queue){
        ModbusUtils.pushShort(queue, writeOffset);
        ModbusUtils.pushShort(queue, writeValue ? 0xff00 : 0);
    }

    @Override
    protected void readResponse(ByteQueue queue){
        writeOffset = ModbusUtils.popUnsignedShort(queue);
        writeValue = ModbusUtils.popUnsignedShort(queue) == 0xff00;
    }

    public int getWriteOffset(){
        return writeOffset;
    }

    public boolean isWriteValue(){
        return writeValue;
    }

}
