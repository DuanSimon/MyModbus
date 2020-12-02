package com.simon.modbus4j.ip.xa;

import com.simon.modbus4j.base.BaseMessageParser;
import com.simon.modbus4j.sero.messaging.IncomingMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class XaMessageParser extends BaseMessageParser {

    public XaMessageParser(boolean master){
        super(master);
    }

    @Override
    protected IncomingMessage parseMessageImpl(ByteQueue queue) throws Exception{
        if(master){
            return XaMessageResponse.createXaMessageResponse(queue);
        }
        return XaMessageRequest.createXaMessageRequest(queue);
    }
}
