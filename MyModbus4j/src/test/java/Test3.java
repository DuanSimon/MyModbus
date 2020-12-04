import com.simon.modbus4j.ModbusFactory;
import com.simon.modbus4j.ModbusMaster;
import com.simon.modbus4j.code.DataType;
import com.simon.modbus4j.locator.BaseLocator;

public class Test3 {
    public static void main(String[] args) throws Exception {
        String commPortId = "COM1";
        int baudRate = 9600;
        int flowControlIn = 0;
        int flowControlOut = 0;
        int dataBits = 8;
        int stopBits = 2;
        int parity = 0;

        TestSerialPortWrapper wrapper = new TestSerialPortWrapper(commPortId, baudRate, flowControlIn, flowControlOut, dataBits, stopBits, parity);

        ModbusMaster master = new ModbusFactory().createRtuMaster(wrapper);
        master.init();

        System.out.println(master.testSlaveNode(5));

        //Define the point locator.
        BaseLocator<Number> loc = BaseLocator.holdingRegister(1, 0, DataType.TWO_BYTE_INT_UNSIGNED);

        //Set the point value
        master.setValue(loc, 1800);

        //Get the point value
        System.out.println(master.getValue(loc));
    }
}
