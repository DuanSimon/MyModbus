package com.simon.modbus4j.serial.ascii;

import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.msg.ModbusMessage;
import com.simon.modbus4j.msg.ModbusRequest;
import com.simon.modbus4j.sero.messaging.IncomingRequestMessage;
import com.simon.modbus4j.sero.messaging.OutgoingRequestMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class AsciiMessageRequest extends AsciiMessage implements OutgoingRequestMessage, IncomingRequestMessage {

    static AsciiMessageRequest createAsciiMessageRequest(ByteQueue queue) throws ModbusTransportException{
        ByteQueue msgQueue = getUnasciiMessage(queue);
        ModbusRequest request = ModbusRequest.createModbusRequest(msgQueue);
        AsciiMessageRequest asciiRequest = new AsciiMessageRequest(request);

        //Return the data.
        return asciiRequest;
    }

    public AsciiMessageRequest(ModbusMessage modbusMessage){
        super(modbusMessage);
    }

    @Override
    public boolean expectsResponse(){
        return modbusMessage.getSlaveId() != 0;
    }

    public ModbusRequest getModbusRequest(){
        return (ModbusRequest) modbusMessage;
    }
}
