package com.simon.modbus4j.sero.epoll;

import java.io.InputStream;

public interface InputStreamEPollWrapper {
    void add(InputStream in, Modbus4JInpubtStreamCallback inpubtStreamCallback);

    void remove(InputStream in);
}
