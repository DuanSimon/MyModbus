package com.simon.modbus4j;

import com.simon.modbus4j.exception.ModbusInitException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

abstract public class ModbusSlaveSet extends Modbus {
    private LinkedHashMap<Integer, ProcessImage> processImages = new LinkedHashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public void addProcessImage(ProcessImage processImage) {
        lock.writeLock().lock();
        try {
            processImages.put(processImage.getSlaveId(), processImage);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean removeProcessImage(int slaveId) {
        lock.writeLock().lock();
        try {
            return (processImages.remove(slaveId) != null);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean removeProcessImage(ProcessImage processImage) {
        lock.writeLock().lock();
        try {
            return (processImages.remove(processImage.getSlaveId()) != null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public ProcessImage getProcessImage(int slaveId) {
        lock.readLock().lock();
        try {
            return processImages.get(slaveId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Collection<ProcessImage> getProcessImages() {
        lock.readLock().lock();
        try {
            return new HashSet<>(processImages.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    abstract public void start() throws ModbusInitException;

    abstract public void stop();

}
