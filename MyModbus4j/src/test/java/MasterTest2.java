import ip.IpParameters;

public class MasterTest2 {
    public static void main(String[] args) throws Exception{
        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost("localhost");
        ipParameters.setPort(502);

        ModbusFactory modbusFactory = new ModbusFactory();
    }
}
