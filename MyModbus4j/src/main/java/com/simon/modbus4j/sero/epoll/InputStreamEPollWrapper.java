package com.simon.modbus4j.sero.epoll;

import java.io.InputStream;

public interface InputStreamEPollWrapper {
    void add(InputStream in, Modbus4JInputStreamCallback inpubtStreamCallback);

    void remove(InputStream in);
}
