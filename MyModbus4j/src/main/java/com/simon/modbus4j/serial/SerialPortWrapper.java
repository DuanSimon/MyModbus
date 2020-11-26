package com.simon.modbus4j.serial;

public interface SerialPortWrapper {

    void close() throws Exception;

    void open() throws Exception;

    InputStream getInputStream();

    OutputStream getOutputStream();

    int getBaudRate();

    int getDataBits();

    int getStopBits();

    int getParity();
}
