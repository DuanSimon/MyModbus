package com.simon.modbus4j.serial.rtu;

import com.simon.modbus4j.ModbusSlaveSet;
import com.simon.modbus4j.base.BaseRequestHandler;
import com.simon.modbus4j.msg.ModbusRequest;
import com.simon.modbus4j.msg.ModbusResponse;
import com.simon.modbus4j.sero.messaging.IncomingMessage;
import com.simon.modbus4j.sero.messaging.OutgoingResponseMessage;

public class RtuRequestHandler extends BaseRequestHandler {

    public RtuRequestHandler(ModbusSlaveSet slave){
        super(slave);
    }

    public OutgoingResponseMessage handleRequest(IncomingMessage req) throws Exception{
        RtuMessageRequest rtuRequest = (RtuMessageRequest) req;
        ModbusRequest request = rtuRequest.getModbusRequest();
        ModbusResponse response = handleRequestImpl(request);
        if(response == null){
            return null;
        }
        return new RtuMessageResponse(response);
    }
}
