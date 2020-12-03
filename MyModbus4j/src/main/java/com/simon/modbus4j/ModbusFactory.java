package com.simon.modbus4j;

import com.simon.modbus4j.base.ModbusUtils;
import com.simon.modbus4j.code.RegisterRange;
import com.simon.modbus4j.exception.ModbusIdException;
import com.simon.modbus4j.exception.ModbusTransportException;
import com.simon.modbus4j.ip.IpParameters;
import com.simon.modbus4j.ip.listener.TcpListener;
import com.simon.modbus4j.ip.tcp.TcpMaster;
import com.simon.modbus4j.ip.tcp.TcpSlave;
import com.simon.modbus4j.ip.udp.UdpMaster;
import com.simon.modbus4j.ip.udp.UdpSlave;
import com.simon.modbus4j.msg.*;
import com.simon.modbus4j.serial.SerialPortWrapper;
import com.simon.modbus4j.serial.ascii.AsciiMaster;
import com.simon.modbus4j.serial.ascii.AsciiSlave;
import com.simon.modbus4j.serial.rtu.RtuMaster;
import com.simon.modbus4j.serial.rtu.RtuSlave;

public class ModbusFactory {
    public ModbusMaster createRtuMaster(SerialPortWrapper wrapper) {
        return new RtuMaster(wrapper);
    }

    public ModbusMaster createAsciiMaster(SerialPortWrapper wrapper) {
        return new AsciiMaster(wrapper);
    }

    public ModbusMaster createTcpMaster(IpParameters params, boolean keepAlive) {
        return new TcpMaster(params, keepAlive);
    }

    public ModbusMaster createUdpMaster(IpParameters params) {
        return new UdpMaster(params);
    }

    public ModbusMaster createTcpListener(IpParameters params) {
        return new TcpListener(params);
    }

    public ModbusSlaveSet createRtuSlave(SerialPortWrapper wrapper) {
        return new RtuSlave(wrapper);
    }

    public ModbusSlaveSet createAsciiSlave(SerialPortWrapper wrapper) {
        return new AsciiSlave(wrapper);
    }

    public ModbusSlaveSet createTcpSlave(boolean encapsulated) {
        return new TcpSlave(encapsulated);
    }

    public ModbusSlaveSet createUdpSlave(boolean encapsulated) {
        return new UdpSlave(encapsulated);
    }

    public ModbusRequest createReadRequest(int slaveId, int range, int offset, int length) throws ModbusTransportException, ModbusIdException {
        ModbusUtils.validateRegisterRange(range);
        if (range == RegisterRange.COIL_STATUS) {
            return new ReadCoilsRequest(slaveId, offset, length);
        }
        if (range == RegisterRange.INPUT_STATUS) {
            return new ReadDiscreteInputsRequest(slaveId, offset, length);
        }
        if (range == RegisterRange.INPUT_REGISTER) {
            return new ReadInputRegistersRequest(slaveId, offset, length);
        }
        return new ReadHoldingRegistersRequest(slaveId, offset, length);
    }
}
