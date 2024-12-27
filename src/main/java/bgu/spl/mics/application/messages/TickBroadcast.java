package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private String senderId;
    private final int tickTime;

    public TickBroadcast(String senderId, int tickTime) {
        this.senderId = senderId;
        this.tickTime = tickTime;
    }

    public String getSenderId() {
        return senderId;
    }

    public int getCurrentTime() {
        return tickTime;
    }
}
