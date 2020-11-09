package com.simon.modbus4j.sero.timer;

public class SystemTimeSource implements TimeSource {

    public long currentTimeMillis(){
        return System.currentTimeMillis();
    }
}
