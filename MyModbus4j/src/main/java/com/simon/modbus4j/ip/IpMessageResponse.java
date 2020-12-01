package com.simon.modbus4j.ip;

import com.simon.modbus4j.msg.ModbusResponse;
import com.simon.modbus4j.sero.messaging.IncomingResponseMessage;
import com.simon.modbus4j.sero.messaging.OutgoingResponseMessage;

public interface IpMessageResponse extends OutgoingResponseMessage, IncomingResponseMessage {

    ModbusResponse getModbusResponse();
}
