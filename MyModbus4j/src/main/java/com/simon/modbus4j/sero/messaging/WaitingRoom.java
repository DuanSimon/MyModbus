package com.simon.modbus4j.sero.messaging;

class WaitingRoom {
    private static final Log LOG = LogFactory.getLog(WatingRoom.class);

    private final Map<WaitingRoomKey, Member> waitHere = new HashMap<~>();

    private WaitingRoomKeyFactory;

    void setKeyFactory(WaitingRoomKeyFactory){
        this.keyFactory = keyFactory;
    }

    void enter(WaitingRoomKey key){

    }
}
