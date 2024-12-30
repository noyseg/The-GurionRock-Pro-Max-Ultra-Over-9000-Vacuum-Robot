package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class CrashedBroadcast implements Broadcast {
    private String senderName;
    private final String CrashType;
    

    public CrashedBroadcast(String senderName, String CrashType) {
        this.senderName = senderName;
        this.CrashType = CrashType;
    }

    public String getSenderName() {
        return senderName;
    }


    public String getType(){
        return this.CrashType;
    }
}
