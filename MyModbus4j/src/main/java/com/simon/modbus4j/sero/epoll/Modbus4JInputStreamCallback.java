package com.simon.modbus4j.sero.epoll;

import java.io.IOException;

public interface Modbus4JInputStreamCallback {
    void input(byte[] buf, int len);

    void closed();

    void ioException(IOException e);

    void teminated();
}
