package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StatisticalFolder;

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
        this.tickTime = TickTime;
        this.duration = Duration;
        this.currentTick = 0;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified
     * duration.
     */
    @Override
    protected void initialize() {

         new Thread(() -> {
            try {
                boolean runningMicroservices = true;
                while (runningMicroservices & currentTick < duration) {
                    // Increment the tick counter
                    currentTick++;

                    System.out.println("Time Service: "+ currentTick);

                    // Broadcast the TickBroadcast message
                    if (FusionSlam.getInstance().getMicroservicesCounter() > 0){
                        StatisticalFolder.getInstance().incrementSystemRunTime(1);
                        sendBroadcast(new TickBroadcast(getName(),currentTick));
                        // Sleep for tickTime duration
                        Thread.sleep(tickTime*1000);
                    }
                    else{
                        runningMicroservices = false;
                    }
                }

                // After duration or if there are no more microServices , broadcast termination
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
            }
        }).start();

    }
}
