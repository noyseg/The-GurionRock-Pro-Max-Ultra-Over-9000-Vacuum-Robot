package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private final GPSIMU gpsimu;

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("GPSIMU");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        // Handle TickBroadcast 
        subscribeBroadcast(TickBroadcast.class, tick -> {
            // He is the only one that was left
            if(FusionSlam.getInstance().getMicroservicesCounter() == 1){
                gpsimu.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();  
            }
            if (gpsimu.getStatus() == STATUS.UP){
                processTick(tick);
            }
        });

        // Handle CrashedBroadcast 
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            System.out.println("Crashed from gpu");
            gpsimu.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(getName()));
            terminate();  
        });
        // Handle CrashedBroadcast 
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            // Only Pose Service was left 
            if (terminate.getSenderName().equals("TimeService")){
                gpsimu.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(getName()));
                terminate();  
            }
        });
    }

    private void processTick(TickBroadcast tick) {
        gpsimu.setCurrentTick(tick.getCurrentTime());
        Pose pose = gpsimu.getPose();
        if (pose != null){
            PoseEvent poseEvent = new PoseEvent(gpsimu.getPose());
            gpsimu.updateLastPose();
            Future<Boolean> future = (Future<Boolean>) sendEvent(poseEvent);
        }
        else{
            gpsimu.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(getName()));
            terminate();  
        }
    }
}
