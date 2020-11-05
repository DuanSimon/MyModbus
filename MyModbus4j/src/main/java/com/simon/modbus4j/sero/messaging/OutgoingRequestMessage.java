package com.simon.modbus4j.sero.messaging;

public interface OutgoingRequestMessage extends OutgoingMessage {

    boolean expectsResponse();
}
