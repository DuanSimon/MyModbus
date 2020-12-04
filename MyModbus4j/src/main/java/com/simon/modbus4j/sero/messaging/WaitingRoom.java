package com.simon.modbus4j.sero.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

class WaitingRoom {
    private static final Log LOG = LogFactory.getLog(WaitingRoom.class);

    private final Map<WaitingRoomKey, Member> waitHere = new HashMap<WaitingRoomKey, Member>();

    private WaitingRoomKeyFactory keyFactory;

    void setKeyFactory(WaitingRoomKeyFactory keyFactory){
        this.keyFactory = keyFactory;
    }

    void enter(WaitingRoomKey key){
        Member member = new Member();
        synchronized (this){
            while(waitHere.get(key) != null){
                if(LOG.isDebugEnabled()){
                    LOG.debug("Duplicate waiting room key found. Waiting for member to leave.");
                }
                try {
                    wait();
                }catch (InterruptedException e){

                }
            }
            waitHere.put(key, member);
        }
    }

    IncomingResponseMessage getResponse(WaitingRoomKey key, long timeout) throws WaitingRoomException{
        Member member;
        synchronized(this){
            member = waitHere.get(key);
        }

        if(member == null){
            throw new WaitingRoomException("No member for key " + key);
        }

        return member.getResponse(timeout);
    }

    void leave(WaitingRoomKey key){
        synchronized (this){
            waitHere.remove(key);
            notifyAll();
        }
    }

    void response(IncomingResponseMessage response) throws WaitingRoomException{
        WaitingRoomKey key = keyFactory.createWaitingRoomKey(response);
        if(key == null){
            return;
        }
        Member member;

        synchronized(this){
            member = waitHere.get(key);
        }
        if(member != null){
            member.setResponse(response);
        }else{
            throw new WaitingRoomException("No recipient was found waiting for response for key " + key);
        }
    }

    class Member{
        private IncomingResponseMessage response;

        synchronized void setResponse(IncomingResponseMessage response){
            this.response = response;
            notify();
        }

        synchronized IncomingResponseMessage getResponse(long timeout){
            if(response != null){
                return response;
            }

            waitNoThrow(timeout);
            return response;
        }

        private void waitNoThrow(long timeout){
            try {
                wait(timeout);
            }catch (InterruptedException e){

            }
        }
    }
}
