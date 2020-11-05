package com.simon.modbus4j;

import com.simon.modbus4j.sero.util.ProgressiveTaskListener;

public interface NodeScanListener extends ProgressiveTaskListener {
    void nodeFound(int nodeNumber);
}
