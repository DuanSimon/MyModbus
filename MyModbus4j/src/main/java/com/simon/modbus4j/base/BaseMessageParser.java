package com.simon.modbus4j.base;

import com.simon.modbus4j.sero.util.queue.ByteQueue;

abstract public class BaseMessageParser implements MessageParser {
    protected final boolean master;

    public BaseMessageParser(boolean master) {
        this.master = master;
    }

    @Override
    public IncomingMessage parseMesssage(ByteQueue queue) throws Exception {
        try {
            return parseMesssage(queue);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    abstract protected IncomingMessage parseMessageImpl(ByteQueue queue) throws Exception;
}
