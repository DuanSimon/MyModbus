package com.simon.modbus4j.sero.io;

public interface LineHandler {

    public void handleLine(String line);

    public void done();
}
