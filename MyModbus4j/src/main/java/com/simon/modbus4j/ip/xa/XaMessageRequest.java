package com.simon.modbus4j.ip.xa;

import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.msg.ModbusRequest;
import com.simon.modbus4j.sero.messaging.IncomingRequestMessage;
import com.simon.modbus4j.sero.messaging.OutgoingRequestMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class XaMessageRequest extends XaMessage implements OutgoingRequestMessage, IncomingRequestMessage {

    static XaMessageRequest createXaMessageRequest(ByteQueue queue) throws ModbusTransportException{
        //Remove the XA header
        int transactionId = ModbusUtils.popShort(queue);
        int protocolId = ModbusUtils.popShort(queue);
        if(protocolId != ModbusUtils.IP_PROTOCOL_ID){
            throw new ModbusTransportException("Unsupported IP protocol id: " + protocolId);
        }
        ModbusUtils.popShort(queue);

        //Create the modbus response.
        ModbusRequest request = ModbusRequest.createModbusRequest(queue);
        return new XaMessageRequest(request, transactionId);
    }

    public XaMessageRequest(ModbusRequest modbusRequest, int transactionId){
        super(modbusRequest, transactionId);
    }

    public boolean expectsResponse(){
        return modbusMessage.getSlaveId() != 0;
    }

    public ModbusRequest getModbusRequest(){
        return (ModbusRequest) modbusMessage;
    }
}
