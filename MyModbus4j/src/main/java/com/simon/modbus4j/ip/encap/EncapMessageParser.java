package com.simon.modbus4j.ip.encap;

public class EncapMessageParser extends BaseMessageParser {

    public EncapMessageParser(boolean master){
        super(master);
    }

    @Override
    protected IncomingMessage parseMessageImpl(ByteQueue queue) throws Exception{
        if(master){
            return EncapMessageResponse.createEncapMessageResponse(queue);
        }
        return EncapMessageRequest.createEncapMessageRequest(queue);
    }
}
