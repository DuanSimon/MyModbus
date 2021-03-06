import com.simon.modbus4j.code.DataType;
import com.simon.modbus4j.BatchRead;
import com.simon.modbus4j.BatchResults;
import com.simon.modbus4j.ModbusFactory;
import com.simon.modbus4j.ModbusMaster;
import com.simon.modbus4j.exception.ErrorResponseException;
import com.simon.modbus4j.ip.IpParameters;
import com.simon.modbus4j.locator.BaseLocator;

public class MasterTest2 {
    public static void main(String[] args) throws Exception{
        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost("localhost");
        ipParameters.setPort(502);

        ModbusFactory modbusFactory = new ModbusFactory();
        ModbusMaster master = modbusFactory.createTcpMaster(ipParameters,false);
        master.setTimeout(4000);
        master.setRetries(1);

        BatchRead<Integer> batch = new BatchRead<Integer>();
        batch.addLocator(0, BaseLocator.holdingRegister(5,80, DataType.TWO_BYTE_INT_SIGNED));
        //batch.addLocator(1,BaseLocator.holdingRegister(5,82,DataType.EIGHT_BYTE_INT_SIGNED));
        try {
            master.init();
            while(true){
                batch.setContiguousRequests(false);
                BatchResults<Integer> results = master.send(batch);
                System.out.println(results.getValue(0));
                //System.out.println(results.getValue(1));

                Thread.sleep(1000);
            }
        }catch (ErrorResponseException e){
            System.out.println(e.getErrorResponse().getExceptionMessage());
        }finally {
            master.destroy();
        }
    }
}
