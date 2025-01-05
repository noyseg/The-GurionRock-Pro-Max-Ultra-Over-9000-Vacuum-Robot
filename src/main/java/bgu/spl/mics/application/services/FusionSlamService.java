package bgu.spl.mics.application.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    private final ErrorCoordinator errorCoordinator; // Singleton responsible for managing error information
    private HashMap<Integer, List<TrackedObject>> waitingTracked; // Map to store tracked objects waiting for a pose

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlam");
        this.errorCoordinator = ErrorCoordinator.getInstance();
        this.fusionSlam = fusionSlam;
        this.isTimeServiceTerminated = false;
        this.waitingTracked = new HashMap<>();
        this.error = false;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        
        System.out.println(getName() + " started");

        // Handle TrackedObjectsEvent: updates landmarks with new tracked objects
        subscribeEvent(TrackedObjectsEvent.class, trackedObj -> { 
            handleTrackedObjectsEvent(trackedObj);
        });

        // Handle PoseEvent: updates landmarks using the current pose
        subscribeEvent(PoseEvent.class, pose -> {
            fusionSlam.addPose(pose.getPose());
            List<TrackedObject> ObjectsToUpdate = waitingTracked.get(pose.getPose().getTime());
            if (ObjectsToUpdate != null){
                for(TrackedObject ObjectToUpdate :ObjectsToUpdate){
                    fusionSlam.setLandMarks(ObjectToUpdate,pose.getPose());
                }
            }
        });

        // Handle TerminatedBroadcast: checks when other services are terminated
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            System.out.println(waitingTracked.isEmpty());
            // Time Service was terminated 
            if (terminate.getSenderName().equals("TimeService"))
                isTimeServiceTerminated = true;
            // Microservice counter is for all Microservices but TimeService
            if (!terminate.getSenderName().equals("TimeService"))
                fusionSlam.decrementMicroserviceCount(); 
             // If all services are terminated and TimeService has ended, create output file and terminate 
            if (fusionSlam.getMicroservicesCounter() == 0 && isTimeServiceTerminated){
                createOutputFile();
                terminate(); // Fusion Slum's Time to finish 
            }
            if (fusionSlam.getMicroservicesCounter() == 0 && waitingTracked.isEmpty() && fusionSlam.getFinished() == false){
                fusionSlam.setFinished();
            }
        });

        // Handle CrashedBroadcast: handles any crash event from other services
        subscribeBroadcast(CrashedBroadcast.class, crash -> { 
            fusionSlam.decrementMicroserviceCount(); 
            error = true; // Indicates that output file should be with error details 
        });

        // Subscribes to TickBroadcast, TrackedObjectsEvent, PoseEvent, TerminatedBroadcast,CrashedBroadcast
    }

     /**
     * Handles the TrackedObjectsEvent and updates landmarks with new tracked objects.
     * If the relevant pose is not available, it stores the tracked object until the pose arrives.
     *
     * @param trackedObj The TrackedObjectsEvent containing the list of tracked objects.
     */
    private void handleTrackedObjectsEvent(TrackedObjectsEvent trackedObj){
        List<TrackedObject> trackedObjects = trackedObj.getTrackedObjects();
        for (TrackedObject trackO: trackedObjects){
            // If FusionSLAM has the relevant pose
            if (trackO.getTime() <= fusionSlam.getPoses().size()){
                fusionSlam.setLandMarks(trackO,fusionSlam.getPoses().get(trackO.getTime()-1));
            }
            // Store tracked object for later use when pose is available
            else{
                List<TrackedObject> alreadyWaiting = waitingTracked.get(trackO.getTime());
                if (alreadyWaiting !=null ){
                    alreadyWaiting.add(trackO);
                }
                else{
                    List<TrackedObject> startWaiting = new LinkedList<>();
                    startWaiting.add(trackO);
                    waitingTracked.put(trackO.getTime(),startWaiting);
                }
            }
        }
    }

    /**
     * Creates the output file in JSON format containing the final system statistics and data.
     * The output file includes information about landmarks, error descriptions, and sensor data.
     */
    private void createOutputFile() {
        // Prepare data to serialize
        LinkedHashMap<String, Object> outputData = new LinkedHashMap<>();
        Collection<LandMark> values = fusionSlam.getLandMarks().values(); 
 
        // Creating a list of landmarks
        ArrayList<LandMark> landMarkslist = new ArrayList<>(values);
        StatisticalFolder.getInstance().setLandMarkslist(landMarkslist);
        if (error){
            // If an error occurred, add error details to the output
            outputData.put("error",errorCoordinator.getDescription());
            outputData.put("faultySensor", errorCoordinator.getFaultSensor());
            outputData.put("lastCamerasFrame",errorCoordinator.getLastFramesCameras());
            outputData.put("lastLiDarWorkerTrackersFrame",errorCoordinator.getLastFramesLidars());
            outputData.put("poses", errorCoordinator.getRobotPoses());
            outputData.put("statistics",StatisticalFolder.getInstance());
        }
        else{
            // If no error occurred, add regular system data to the output
            outputData.put("systemRuntime", StatisticalFolder.getInstance().getSystemRunTime());
            outputData.put("numDetectedObjects", StatisticalFolder.getInstance().getNumDetectedObjects());
            outputData.put("numTrackedObjects", StatisticalFolder.getInstance().getNumTrackedObjects());
            outputData.put("numLandmarks", StatisticalFolder.getInstance().getNumLandmarks());
            outputData.put("landMarks",StatisticalFolder.getInstance().getLandMarks());
        }
        // Serialize to JSON and write to file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("output_file.json")) {
            gson.toJson(outputData, writer);
            System.out.println("Output file has been created: output_file.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
