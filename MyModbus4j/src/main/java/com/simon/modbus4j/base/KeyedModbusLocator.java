package com.simon.modbus4j.base;

import com.simon.modbus4j.locator.BaseLocator;

public class KeyedModbusLocator<K> {
    private final K key;
    private final BaseLocator<?> locator;

    public KeyedModbusLocator(K key, BaseLocator<?> locator) {
        this.key = key;
        this.locator = locator;
    }

    public K getKey() {
        return key;
    }

    public BaseLocator<?> getLocator() {
        return locator;
    }

    @Override
    public String toString() {
        return "KeyedModbusLocator(key=" + key + ", locator=" + locator + ")";
    }

    public int getDataType() {
        return locator.getDataType();
    }

    public int getOffset() {
        return locator.getOffset();
    }

    public SlaveAndRange getSlaveAndRange() {
        return new SlaveAndRange(locator.getSlaveId(), locator.getRange());
    }

    public int getEndOffset() {
        return locator.getEndOffset();
    }

    public int getRegisterCount() {
        return locator.getRegisterCount();
    }

    public Object bytesToValue(byte[] data, int requestOffset) {
        try {
            return locator.bytesToValue(data, requestOffset);
        } catch (ArrayIndexOutOfBoundsException e) {
            return new ExceptionResult(ExceptionCode.ILLEGAL_DATA_ADDRESS);
        }
    }
}
