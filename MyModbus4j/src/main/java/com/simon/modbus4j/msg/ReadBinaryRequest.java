package com.simon.modbus4j.msg;

import com.simon.modbus4j.Modbus;
import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

abstract class ReadBinaryRequest extends ModbusRequest {
    private int startOffset;
    private int numberOfBits;

    public ReadBinaryRequest(int slaveId, int startOffset, int numberOfBits) throws ModbusTransportException{
        super(slaveId);
        this.startOffset = startOffset;
        this.numberOfBits = numberOfBits;
    }

    @Override
    public void validate(Modbus modbus) throws ModbusTransportException{
        ModbusUtils.validateOffset(startOffset);
        modbus.validateNumberOfBits(numberOfBits);
        ModbusUtils.validateEndOffset(startOffset + numberOfBits - 1);
    }

    ReadBinaryRequest(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    protected void writeRequest(ByteQueue queue){
        ModbusUtils.pushShort(queue, startOffset);
        ModbusUtils.pushShort(queue, numberOfBits);
    }

    @Override
    protected void readRequest(ByteQueue queue){
        startOffset = ModbusUtils.popUnsignedShort(queue);
        numberOfBits = ModbusUtils.popUnsignedShort(queue);
    }

    protected byte[] getData(ProcessImage processImage) throws ModbusTransportException{
        boolean[] data = new boolean[numberOfBits];

        for (int i = 0; i < numberOfBits; i++) {
            data[i] = getBinary(processImage, i + startOffset);
        }
        return convertToBytes(data);
    }

    abstract protected boolean getBinary(ProcessImage processImage, int index) throws ModbusTransportException;

    @Override
    public String toString(){
        return "ReadBinaryRequest [startOffset=" + startOffset + ", numberOfBits=" + numberOfBits + "]";
    }
}
