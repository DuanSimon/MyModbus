package com.simon.modbus4j;

public interface NodeScanListener extends ProgressiveTastListener {
    void nodeFound(int nodeNumber);
}
