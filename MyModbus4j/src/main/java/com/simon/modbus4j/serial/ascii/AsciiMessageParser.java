package com.simon.modbus4j.serial.ascii;

import com.simon.modbus4j.base.BaseMessageParser;
import com.simon.modbus4j.sero.messaging.IncomingMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class AsciiMessageParser extends BaseMessageParser {

    public AsciiMessageParser(boolean master){
        super(master);
    }

    @Override
    protected IncomingMessage parseMessageImpl(ByteQueue queue) throws Exception{
        if(master){
            return AsciiMessageResponse.createAsciiMessageResponse(queue);
        }
        return AsciiMessageRequest.createAsciiMessageRequest(queue);
    }
}
