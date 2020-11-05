package com.simon.modbus4j.ip.encap;

import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.msg.ModbusRequest;
import com.simon.modbus4j.sero.messaging.OutgoingRequestMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class EncapMessageRequest extends EncapMessage implements OutgoingRequestMessage, IncomingRequestMessage {

    static EncapMessageRequest createEncapMessageRequest(ByteQueue queue) throws ModbusTransportException{

        ModbusRequest request = ModbusRequest.createModbusRequest(queue);
        EncapMessageRequest encapRequest = new EncapMessageRequest(request);

        return encapRequest;
    }

    public EncapMessageRequest(ModbusRequest modbusRequest){
        super(modbusRequest);
    }

    public boolean expectsResponse(){
        return modbusMessage.getSlaveId() != 0;
    }

    public ModbusRequest getModbusRequest(){
        return (ModbusRequest) modbusMessage;
    }
}
