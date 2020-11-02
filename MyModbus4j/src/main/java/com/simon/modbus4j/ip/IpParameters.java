package com.simon.modbus4j.ip;

import com.simon.modbus4j.base.ModbusUtils;

public class IpParameters {
    private String host;
    private int port = ModbusUtils.TCP_PORT;
    private boolean encapsulated;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isEncapsulated() {
        return encapsulated;
    }

    public void setEncapsulated(boolean encapsulated) {
        this.encapsulated = encapsulated;
    }
}
