package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TerminatedBroadcast implements Broadcast {
    private String senderId;
    private final int statistic;

    public TerminatedBroadcast(String senderId,int statistic) {
        this.senderId = senderId;
        this.statistic=statistic;
    }

    public String getSenderId() {
        return senderId;
    }
    public int getStatistic() {
        return statistic;
    }
}
