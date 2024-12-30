package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TerminatedBroadcast implements Broadcast {
    private String senderName;
    private String senderType;

    public TerminatedBroadcast(String senderName, String senderType) {
        this.senderName = senderName;
        this.senderType = senderName;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public String getSenderType() {
        return this.senderType;
    }


}
