package com.simon.modbus4j.serial.rtu;

import com.simon.modbus4j.msg.ModbusMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class RtuMessage extends SerialMessage {

    public RtuMessage(ModbusMessage modbusMessage){
        super(modbusMessage);
    }

    public byte[] getMessageData(){
        ByteQueue queue = new ByteQueue();

        //Write the CRC
        ModbusUtils.pushShort(queue, ModbusUtils.calculateCRC(modbusMessage));

        //Return the data.
        return queue.popAll();
    }
}
