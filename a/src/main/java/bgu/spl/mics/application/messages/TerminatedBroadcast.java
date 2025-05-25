package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TerminatedBroadcast implements Broadcast {
    private String senderName; // The name of the sender that has terminated

    /**
     * Constructs a new TerminatedBroadcast with the given sender name.
     *
     * @param senderName The name of the sender (e.g., microservice or sensor) that has terminated.
     */
    public TerminatedBroadcast(String senderName) {
        this.senderName = senderName;
    }

    /**
     * Retrieves the name of the sender that has terminated.
     *
     * @return The name of the sender.
     */
    public String getSenderName() {
        return this.senderName;
    }
}
