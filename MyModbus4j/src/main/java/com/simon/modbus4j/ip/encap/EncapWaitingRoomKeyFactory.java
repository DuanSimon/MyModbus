package com.simon.modbus4j.ip.encap;

import com.simon.modbus4j.sero.messaging.WaitingRoomKeyFactory;

public class EncapWaitingRoomKeyFactory implements WaitingRoomKeyFactory {

    @Override
    public WaitingRoomKey createWaitingRoomKey(OutgoingRequestMessage request){
        return createWaitingRoomKey(((IpMessage) request).getModbusMessage());
    }

    @Override
    public WaitingRoomKey createWaitingRoomKey(IncomingresponseMessage response){
        return createWaitingRoomKey(((IpMessage) response).getModbusMessage());
    }

    public WaitingRoomKey createWaitignRoomKey(ModbusMessage msg){
        return new EncapWaitingRoomKey(msg.getSlaveId(), msg.getFunctionCode());
    }

    class encapWaitingRoomKey implements WaitingRoomKey{
        private final int slaveId;
        private final byte functionCode;

        public EncapWaitingRoomKey(int slaveID, byte functionCode){
            this.slaveId = slaveId;
            this.functionCode = functionCode;
        }

        @Override
        public int hashCode(){
            final int prime = 31;
            int result = 1;
            result = prime * result + functionCode;
            result = prime * result + slaveId;
            return result;
        }

        @Override
        public boolean equals(Object obj){
            if(this == obj){
                return true;
            }
            if(obj == null){
                return false;
            }
            if(getClass() != obj.getClass()){
                return false;
            }
            EncapWaitingRoomKey other = (EncapWaitingRoomKey) obj;
            if(functionCode != other.functionCode){
                return false;
            }
            if(slaveId != other.slaveId){
                return false;
            }
            return true;
        }
    }
}