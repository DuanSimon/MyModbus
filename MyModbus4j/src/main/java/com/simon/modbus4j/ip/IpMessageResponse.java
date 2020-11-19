package com.simon.modbus4j.ip;

public interface IpMessageResponse extends OutgoingResponseMessage, IncomingResponseMessage {

    ModbusResponse getModbusResponse();
}
