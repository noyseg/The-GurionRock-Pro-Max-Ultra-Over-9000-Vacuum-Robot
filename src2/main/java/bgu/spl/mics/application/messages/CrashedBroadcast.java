package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class CrashedBroadcast implements Broadcast {
    private String senderName; // The name of the sender that triggered the crash

    /**
     * Constructs a new CrashedBroadcast with the given sender name.
     *
     * @param senderName The name of the sender (e.g., the sensor or microservice)
     *                   that triggered the crash broadcast.
     */
    public CrashedBroadcast(String senderName) {
        this.senderName = senderName;
    }

    /**
     * Retrieves the name of the sender that triggered the crash broadcast.
     *
     * @return The name of the sender.
     */
    public String getSenderName() {
        return senderName;
    }
}
