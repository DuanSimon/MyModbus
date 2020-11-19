package com.simon.modbus4j.ip.encap;

public class EncapRequestHandler extends BaseRequestHandler {

    public EncapREquestHandler(ModbusSlaveSet slave){
        super(slave);
    }

    public OutgoingResponseMessage handleRequest(IncomingRequestMessage req) throws Exception{
        EncapMessgaeRequest tcpRequest = (EncapMessageRequest) req;
        ModbusRequest request = tcpRequest.getModbusRequest();
        ModbusResponse response = handleRequestImpl(request);
        if(response == null){
            return null;
        }
        return new EncapMessageResponse(response);
    }
}
