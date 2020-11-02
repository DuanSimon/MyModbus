package com.simon.modbus4j;

import java.util.Map;

public class BatchResults<K> {
    private final Map<K, Object> data = new HashMap<>();

    public void addResult(K key, Object value) {
        data.put(key, value);
    }

    public Object getValue(K key) {
        return data.get(key);
    }

    public Integer getInValue(K key) {
        return (Integer) getValue(key);
    }

    public Long getLongValue(K key) {
        return (Long) getValue(key);
    }

    public Double getDoubleValue(K key) {
        return (Double) getValue(key);
    }

    public Float getFloatValue(K key) {
        return (Float) getValue(key);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
