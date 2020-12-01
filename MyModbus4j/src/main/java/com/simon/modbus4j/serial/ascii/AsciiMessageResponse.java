package com.simon.modbus4j.serial.ascii;

import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.msg.ModbusMessage;
import com.simon.modbus4j.msg.ModbusResponse;
import com.simon.modbus4j.sero.messaging.IncomingResponseMessage;
import com.simon.modbus4j.sero.messaging.OutgoingResponseMessage;
import com.simon.modbus4j.sero.util.queue.ByteQueue;

public class AsciiMessageResponse extends AsciiMessage implements OutgoingResponseMessage, IncomingResponseMessage {
    static AsciiMessageResponse createAsciiMessageResponse(ByteQueue queue) throws ModbusTransportException{
        ByteQueue msgQueue = getUnasciiMessage(queue);
        ModbusResponse response = ModbusResponse.createModbusResponse(msgQueue);
        AsciiMessageResponse asciiResponse = new AsciiMessageResponse(response);

        //Return the data.
        return asciiResponse;
    }

    public AsciiMessageResponse(ModbusMessage modbusMessage){
        super(modbusMessage);
    }

    public ModbusResponse getModbusResponse(){
        return (ModbusResponse) modbusMessage;
    }
}
