package com.simon.modbus4j.msg;

import com.simon.modbus4j.Modbus;
import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.code.FunctionCode;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class ReportSlaveIdResponse extends ModbusResponse {
    private byte[] data;

    ReportSlaveIdResponse(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    ReportSlaveIdResponse(int slaveId, byte[] data) throws ModbusTransportException{
        super(slaveId);
        this.data = data;
    }

    @Override
    public byte getFunctionCode(){
        return FunctionCode.REPORT_SLAVE_ID;
    }

    @Override
    protected void readResponse(ByteQueue queue){
        int numberOfBytes = ModbusUtils.popUnsignedByte(queue);
        if(queue.size() < numberOfBytes){
            throw new ArrayIndexOutOfBoundsException();
        }
        data = new byte[numberOfBytes];
        queue.pop(data);
    }

    @Override
    protected void writeResponse(ByteQueue queue){
        ModbusUtils.pushByte(queue, data.length);
        queue.push(data);
    }

    public byte[] getData(){
        return data;
    }
}
