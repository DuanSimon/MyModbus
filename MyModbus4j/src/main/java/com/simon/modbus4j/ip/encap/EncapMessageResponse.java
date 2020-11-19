package com.simon.modbus4j.ip.encap;

import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.msg.ModbusResponse;

public class EncapMessageResponse extends EncapMessage implements IpMessageResponse {

    static EncapMessageResponse createEncapMessageResponse(ByteQueue queue) throws ModbusTransportException{
        //Create the modbus response.
        ModbusResponse response = ModbusResponse.createModbusResponse(queue);
        EncapMessageResponse encapResponse = new EncapMessageResponse(response);

        //Check the CRC
        ModbusUtils.checkCRC(encapResponse.modbusMessage, queue);

        return encapResponse;
    }

    public EncapMessageResponse(ModbusResponse modbusResponse){
        super(modbusResponse);
    }

    public ModbusResponse getModbusResponse(){
        return (ModbusResponse) modbusMessage;
    }
}
