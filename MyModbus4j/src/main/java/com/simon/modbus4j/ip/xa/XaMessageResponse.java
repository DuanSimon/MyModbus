package com.simon.modbus4j.ip.xa;

import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.ip.IpMessageResponse;
import com.simon.modbus4j.msg.ModbusResponse;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class XaMessageResponse extends XaMessage implements IpMessageResponse {

    static XaMessageResponse createXaMessageResponse(ByteQueue queue) throws ModbusTransportException{
        //Remove the XA header
        int transactionId = ModbusUtils.popShort(queue);
        int protocolId = ModbusUtils.popShort(queue);
        if(protocolId != ModbusUtils.IP_PROTOCOL_ID){
            throw new ModbusTransportException("Unsupported IP protocol id: " + protocolId);
        }
        ModbusUtils.popShort(queue);

        //Create the modbus response.
        ModbusResponse response = ModbusResponse.createModbusResponse(queue);
        return new XaMessageResponse(response, transactionId);
    }

    public XaMessageResponse(ModbusResponse modbusResponse, int transactionId){
        super(modbusResponse, transactionId);
    }

    public ModbusResponse getModbusResponse(){
        return (ModbusResponse) modbusMessage;
    }
}
