package com.simon.modbus4j.ip.xa;

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
