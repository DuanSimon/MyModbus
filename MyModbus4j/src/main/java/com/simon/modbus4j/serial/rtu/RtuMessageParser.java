package com.simon.modbus4j.serial.rtu;

import com.simon.modbus4j.base.BaseMessageParser;
import com.simon.modbus4j.sero.messaging.IncomingMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class RtuMessageParser extends BaseMessageParser {

    public RtuMessageParser(boolean master){
        super(master);
    }

    @Override
    protected IncomingMessage parseMessageImpl(ByteQueue queue) throws Exception{
        if(master){
            return RtuMessageResponse.createRtuMessageResponse(queue);
        }
        return RtuMessageRequest.createRtuMessageRequest(queue);
    }
//
//    public static void main(String[] args) throws Exception{
//    ByteQueue queue = new ByteQueue(new byte[]{5, 3, 2, 0, (byte) 0xdc, (byte) 0x48, (byte) 0x1d, 0});
//    RtuMessageParser p = new RtuMessageParser(false);
//    System.out.println(p.parseResponse(queue));
//
}
