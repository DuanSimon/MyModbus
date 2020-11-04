package com.simon.modbus4j.msg;

import com.simon.modbus4j.Modbus;
import com.simon.modbus4j.ProcessImage;
import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

abstract class ReadNumericRequest extends ModbusRequest {
    private int startOffset;
    private int numberOfRegisters;

    public ReadNumericRequest(int slaveId, int startOffset, int numberOfRegisters) throws ModbusTransportException{
        super(slaveId);
        this.startOffset = startOffset;
        this.numberOfRegisters = numberOfRegisters;
    }

    @Override
    public void validate(Modbus modbus) throws ModbusTransportException{
         ModbusUtils.validateOffset(startOffset);
         modbus.validateNumberOfRegisters(numberOfRegisters);
         ModbusUtils.validateEndOffset(startOffset + numberOfRegisters - 1);
    }

    ReadNumericRequest(int slaveId) throws ModbusTransportException{
        super(slaveId);
    }

    @Override
    protected void writeRequest(ByteQueue queue){
        ModbusUtils.pushShort(queue, startOffset);
        ModbusUtils.pushShort(queue, numberOfRegisters);
    }

    @Override
    protected void readRequest(ByteQueue queue) {
        startOffset = ModbusUtils.popUnsignedShort(queue);
        numberOfRegisters = ModbusUtils.popUnsignedShort(queue);
    }

    protected byte[] getData(ProcessImage processImage) throws ModbusTransportException{
        short[] data = new short[numberOfRegisters];

        for (int i = 0; i < numberOfRegisters; i++) {
            data[i] = getNumeric(processImage, i + startOffset);
        }
        return convertToBytes(data);
    }

    abstract protected short getNumeric(ProcessImage processImage, int index) throws ModbusTransportException;

    @Override
    public String toString(){
        return "ReadNumericRequest [startOffset=" + startOffset + ", numberOfRegisters=" + numberOfRegisters + "]";
    }
}
