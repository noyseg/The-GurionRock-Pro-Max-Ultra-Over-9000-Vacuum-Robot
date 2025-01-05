package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * TimeService acts as the global timer for the system, broadcasting
 * TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    private final int tickTime; // Duration of each tick in seconds
    private final int duration; // Total number of ticks before termination
    private int currentTick; // Current tick count

    /**
     * Constructor for TimeService.
     *
     * @param tickTime The duration of each tick in seconds.
     * @param Duration The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.tickTime = TickTime;
        this.duration = Duration;
        this.currentTick = 0;
    }

    /**
     * Initializes the TimeService.
     * - Starts a new thread to simulate the ticking process.
     * - Broadcasts TickBroadcast messages at regular intervals based on the configured tick time.
     * - Terminates after the specified duration or when there are no more active microservices.
     */
    @Override
    protected void initialize() {

        System.out.println(getName() + " started");

            // Continue running until the specified duration is reached or there are no more active microservices
            while (FusionSlam.getInstance().getFinished() == false && currentTick < duration) {
                try{
                    // Increment the tick counter and broadcast TickBroadcast
                    currentTick++;
                    System.out.println("Time Service: " + currentTick);
                    StatisticalFolder.getInstance().incrementSystemRunTime(1);
                    sendBroadcast(new TickBroadcast(currentTick));
                    // Sleep for tickTime duration (convert to milliseconds)
                    Thread.sleep(tickTime * 1000);
                }
                 catch (InterruptedException e) {
                    // Handle interruption of the thread
                    terminate();
                    Thread.currentThread().interrupt(); // Restore interrupt status
                }
        }

        // After the duration or if no microservices are left, send the termination broadcast
        sendBroadcast(new TerminatedBroadcast(getName()));
        terminate();
    }
}
