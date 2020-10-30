import ip.IpParameters;
import ip.tcp.TcpMaster;

public class ModbusFactory {
    public ModbusMaster createRtuMaster(SerialPortWrapper wrapper){
        return  new RtuMaster(wrapper);
    }

    public ModbusMaster createAsciiMaster(SerialPortWrapper wrapper){
        return new AsciiMaster(wrapper);
    }

    public ModbusMaster createTcpMaster(IpParameters params, boolean keepAlive){
        return new TcpMaster(params,keepAlive);
    }
}
