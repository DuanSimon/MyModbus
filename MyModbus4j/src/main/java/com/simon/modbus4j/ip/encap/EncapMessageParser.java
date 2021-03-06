package com.simon.modbus4j.ip.encap;

import com.simon.modbus4j.base.BaseMessageParser;
import com.simon.modbus4j.sero.messaging.IncomingMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

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
