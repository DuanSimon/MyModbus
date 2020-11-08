package com.simon.modbus4j.sero.messaging;

import com.simon.modbus4j.sero.util.queue.ByteQueue;

public interface MessageParser {

    IncomingMessage parseMessage(ByteQueue queue) throws Exception;
}
