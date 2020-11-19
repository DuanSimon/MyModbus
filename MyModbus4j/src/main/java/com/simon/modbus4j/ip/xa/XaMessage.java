package com.simon.modbus4j.ip.xa;

import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.msg.ModbusMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class XaMessage IpMessage {

    protected final int transactionId;

    public XaMessage(ModbusMessage modbusMessage, int transactionId){
        super(modbusMessage);
        this.transactionId = transactionId;
    }

    public byte[] getMessageData(){
        ByteQueue msgQueue = new ByteQueue();

        //Write the particular message.
        modbusMessage.write(msgQueue);

        //Create the XA message.
        ByteQueue xaQueue = new ByteQueue();
        ModbusUtils.pushShort(xaQueue, transactionId);
        ModbusUtils.pushShort(xaQueue, ModbusUtils.IP_PROTOCOL_ID);
        ModbusUtils.pushShort(xaQueue, msgQueue.size());
        xaQueue.push(msgQueue);

        //Return the data.
        return xaQueue.popAll();
    }

    public int getTransactionId(){
        return transactionId;
    }

    @Override
    public ModbusMessage getModbusMessage(){
        return modbusMessage;
    }

    @Override
    public String toString(){
        return "XaMessage [transactionId=" + transactionId + ", message=" + modbusMessage + "]";
    }
}
