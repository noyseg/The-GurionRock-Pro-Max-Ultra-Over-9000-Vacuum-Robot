package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private final int tickTime; // The current system time (tick)

    /**
     * Constructs a new TickBroadcast with the given tick time.
     *
     * @param tickTime The current system time (tick).
     */
    public TickBroadcast(int tickTime) {
        this.tickTime = tickTime;
    }

    /**
     * Retrieves the current system time (tick).
     *
     * @return The current tick time.
     */
    public int getCurrentTime() {
        return tickTime;
    }
}
