package com.simon.modbus4j.sero.messaging;

import java.io.IOException;

public interface Transport {
    abstract void setConsumer(DataConsumer consumer) throws IOException;

    abstract void removeConsumer();

    abstract void write(byte[] data) throws IOException;

    abstract void write(byte[] data, int len) throws IOException;
}
