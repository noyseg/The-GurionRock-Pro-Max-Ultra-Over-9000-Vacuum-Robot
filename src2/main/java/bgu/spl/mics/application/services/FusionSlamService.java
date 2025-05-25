package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;


/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam fusionSlam; // The FusionSLAM singleton  associated with this service 
    private boolean isTimeServiceTerminated; // Flag indicating whether the TimeService has been terminated
    private boolean error; // Flag indicating whether an error occurred

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlam");
        this.fusionSlam = fusionSlam;
        this.isTimeServiceTerminated = false;
        this.error = false;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {

        // Handle TrackedObjectsEvent: updates landmarks with new tracked objects
        subscribeEvent(TrackedObjectsEvent.class, trackedObj -> { 
            fusionSlam.handleTrackedObjectsEvent(trackedObj);
        });

        // Handle PoseEvent: updates landmarks using the current pose
        subscribeEvent(PoseEvent.class, pose -> {
            fusionSlam.handlePoseEvent(pose);
        });

        // Handle TerminatedBroadcast: checks when other services are terminated
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            // Time Service was terminated 
            if (terminate.getSenderName().equals("TimeService"))
                isTimeServiceTerminated = true;
            // Microservice counter is for all Microservices but TimeService
            if (!terminate.getSenderName().equals("TimeService"))
                fusionSlam.decrementMicroserviceCount(); 
             // If all services are terminated and TimeService has ended, create output file and terminate 
            if (fusionSlam.getMicroservicesCounter() == 0 && isTimeServiceTerminated){
                fusionSlam.createOutputFile(error);
                terminate(); // Fusion Slum's Time to finish 
            }
            if (fusionSlam.getMicroservicesCounter() == 0 && fusionSlam.isWaitingTrackedEmpty() && fusionSlam.getFinished() == false){
                fusionSlam.setFinished();
            }
        });

        // Handle CrashedBroadcast: handles any crash event from other services
        subscribeBroadcast(CrashedBroadcast.class, crash -> { 
            fusionSlam.decrementMicroserviceCount(); 
            error = true; // Indicates that output file should be with error details 
        });

    }
}
