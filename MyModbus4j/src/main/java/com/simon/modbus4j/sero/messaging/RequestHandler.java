package com.simon.modbus4j.sero.messaging;

public interface RequestHandler {

    OutgoingResponseMessage handleRequest(IncomingRequestMessage request) throws Exception;
}
