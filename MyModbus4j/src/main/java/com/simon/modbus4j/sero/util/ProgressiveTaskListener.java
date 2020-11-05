package com.simon.modbus4j.sero.util;

public interface ProgressiveTaskListener {

    void progressUpdate(float progress);

    void taskCancelled();

    void taskCompleted();
}
