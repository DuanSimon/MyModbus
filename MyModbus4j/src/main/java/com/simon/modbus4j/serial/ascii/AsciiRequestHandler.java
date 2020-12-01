package com.simon.modbus4j.serial.ascii;

import com.simon.modbus4j.ModbusSlaveSet;
import com.simon.modbus4j.base.BaseRequestHandler;
import com.simon.modbus4j.msg.ModbusRequest;
import com.simon.modbus4j.msg.ModbusResponse;
import com.simon.modbus4j.sero.messaging.IncomingRequestMessage;
import com.simon.modbus4j.sero.messaging.OutgoingResponseMessage;

public class AsciiRequestHandler extends BaseRequestHandler {

    public AsciiRequestHandler(ModbusSlaveSet slave){
        super(slave);
    }

    public OutgoingResponseMessage handleRequest(IncomingRequestMessage req) throws Exception{
        AsciiMessageRequest asciiRequest = (AsciiMessageRequest) req;
        ModbusRequest request = asciiRequest.getModbusRequest();
        ModbusResponse response = handleRequestImpl(request);
        if(response == null){
            return null;
        }
        return new AsciiMessageResponse(response);
    }
}
