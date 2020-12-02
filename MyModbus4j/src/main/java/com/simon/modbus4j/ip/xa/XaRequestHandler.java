package com.simon.modbus4j.ip.xa;

import com.simon.modbus4j.ModbusSlaveSet;
import com.simon.modbus4j.base.BaseRequestHandler;
import com.simon.modbus4j.msg.ModbusRequest;
import com.simon.modbus4j.msg.ModbusResponse;
import com.simon.modbus4j.sero.messaging.IncomingRequestMessage;
import com.simon.modbus4j.sero.messaging.OutgoingResponseMessage;

public class XaRequestHandler extends BaseRequestHandler {

    public XaRequestHandler(ModbusSlaveSet slave){
        super(slave);
    }

    public OutgoingResponseMessage handleRequest(IncomingRequestMessage req) throws Exception{

        XaMessageRequest tcpRequest = (XaMessageRequest) req;
        ModbusRequest request = tcpRequest.getModbusRequest();
        ModbusResponse response = handleRequestImpl(request);
        if(response == null){
            return null;
        }
        return new XaMessageResponse(response, tcpRequest.transactionId);
    }
}
