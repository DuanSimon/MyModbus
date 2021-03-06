package com.simon.modbus4j.ip.encap;

import com.simon.modbus4j.ModbusSlaveSet;
import com.simon.modbus4j.base.BaseRequestHandler;
import com.simon.modbus4j.msg.ModbusRequest;
import com.simon.modbus4j.msg.ModbusResponse;
import com.simon.modbus4j.sero.messaging.IncomingRequestMessage;
import com.simon.modbus4j.sero.messaging.OutgoingResponseMessage;

public class EncapRequestHandler extends BaseRequestHandler {

    public EncapRequestHandler(ModbusSlaveSet slave){
        super(slave);
    }

    public OutgoingResponseMessage handleRequest(IncomingRequestMessage req) throws Exception{
        EncapMessageRequest tcpRequest = (EncapMessageRequest) req;
        ModbusRequest request = tcpRequest.getModbusRequest();
        ModbusResponse response = handleRequestImpl(request);
        if(response == null){
            return null;
        }
        return new EncapMessageResponse(response);
    }
}
