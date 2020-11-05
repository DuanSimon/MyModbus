package com.simon.modbus4j.ip.encap;

import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.ip.IpMessage;
import com.simon.modbus4j.msg.ModbusMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class EncapMessage extends IpMessage {

    public EncapMessage(ModbusMessage modbusMessage){
        super(modbusMessage);
    }

    public byte[] getMessageData(){
        ByteQueue msgQueue = new ByteQueue();

        modbusMessage.write(msgQueue);

        ModbusUtils.pushShort(msgQueue, ModbusUtils.calculateCRC(modbusMessage));

        return msgQueue.popAll();
    }
}
