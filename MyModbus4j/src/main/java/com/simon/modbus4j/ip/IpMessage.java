package com.simon.modbus4j.ip;

import com.simon.modbus4j.msg.ModbusMessage;

abstract public class IpMessage {
    protected final ModbusMessage modbusMessage;

    public IpMessage(ModbusMessage modbusMessage){
        this.modbusMessage = modbusMessage;
    }

    public ModbusMessage getModbusMessage(){
        return modbusMessage;
    }
}
