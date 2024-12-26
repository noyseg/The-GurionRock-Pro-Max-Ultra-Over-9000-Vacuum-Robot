package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
<<<<<<< HEAD
=======
import bgu.spl.mics.application.objects.FusionSlam;
>>>>>>> 9adfc1fa37254a4a9c8910abb32cdc95fb1e48a6

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("Change_This_Name");
        // TODO Implement this
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
<<<<<<< HEAD
        // TODO Implement this
=======
        // Subscribes to TickBroadcast, TrackedObjectsEvent, PoseEvent, TerminatedBroadcast,CrashedBroadcast
>>>>>>> 9adfc1fa37254a4a9c8910abb32cdc95fb1e48a6
    }
}
