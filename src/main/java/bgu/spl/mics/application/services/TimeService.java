package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.STATUS;

/**
 * TimeService acts as the global timer for the system, broadcasting
 * TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    private final int tickTime;
    private final int duration;
    private int currentTick;

    /**
     * Constructor for TimeService.
     *
     * @param TickTime The duration of each tick in seconds.
     * @param Duration The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.tickTime = tickTime;
        this.duration = duration;
        this.currentTick = 0;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified
     * duration.
     */
    @Override
    protected void initialize() {

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            if (terminated.getSenderName() == "FusionSlam"){
                terminate();
            }});

        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            sendBroadcast(new TerminatedBroadcast(getName()));
            // Print what write in crashed if need
        });

        //??????
         new Thread(() -> {
            try {
                while (currentTick < duration) {
                    // Increment the tick counter
                    currentTick++;

                    // Broadcast the TickBroadcast message
                    sendBroadcast(new TickBroadcast(getName(),currentTick));

                    // Sleep for tickTime duration
                    Thread.sleep(tickTime);
                }

                // After duration, broadcast termination
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
            }
        }).start();

    }
}
