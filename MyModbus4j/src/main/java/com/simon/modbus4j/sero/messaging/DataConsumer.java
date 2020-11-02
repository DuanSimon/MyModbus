package com.simon.modbus4j.sero.messaging;

import java.io.IOException;

public interface DataConsumer {
    public void data(byte[] b, int len);

    public void handleIOException(IOException e);
}
