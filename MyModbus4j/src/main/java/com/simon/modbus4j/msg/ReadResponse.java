package com.simon.modbus4j.msg;

import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;
import com.sun.xml.internal.ws.util.StreamUtils;

abstract public class ReadResponse extends ModbusResponse {
    private byte[] data;

    ReadResponse(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    ReadResponse(int slaveId, byte[] data) throws ModbusTransportException{
        super(slaveId);
        this.data = data;
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

    public short[] getShortData(){
        return convertToShorts(data);
    }

    public boolean[] getBooleanData(){
        return convertToBooleans(data);
    }

    public String toString(boolean numeric){
        if(data == null){
            return "ReadResponse [null]";
        }
        return "ReadResponse [len=" + (numeric ? data.length / 2 : data.length * 8) + ", " + StreamUtils.dumpHex(data) + "]";
    }
}
