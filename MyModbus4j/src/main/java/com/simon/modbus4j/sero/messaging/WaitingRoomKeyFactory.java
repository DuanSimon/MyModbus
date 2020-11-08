package com.simon.modbus4j.sero.messaging;

public interface WaitingRoomKeyFactory {

    WaitingRoomKey createWaitingRoomKey(OutgoingRequestMessage request);

    WaitingRoomKey createWaitingRoomKey(IncomingResponseMessage response);
}
