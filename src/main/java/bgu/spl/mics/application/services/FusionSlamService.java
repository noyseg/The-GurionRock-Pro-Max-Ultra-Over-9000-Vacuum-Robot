package bgu.spl.mics.application.services;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.ErrorCoordinator;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam fusionSlam;
    private boolean isTimeServiceTerminated;
    private boolean error;
    private final ErrorCoordinator errorCoordinator;
    private HashMap<Integer,List<TrackedObject>> waitingTracked;

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
        // Handle TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            StatisticalFolder.getInstance().incrementSystemRunTime(1);
        });

        // Handle TrackedObjectsEvent
        subscribeEvent(TrackedObjectsEvent.class, trackedObj -> { 
            handleTrackedObjectsEvent(trackedObj);
        });

        // Handle PoseEvent
        subscribeEvent(PoseEvent.class, pose -> {
            fusionSlam.addPose(pose.getPose());
            List<TrackedObject> ObjectsToUpdate = waitingTracked.get(pose.getPose().getTime());
            if (ObjectsToUpdate != null){
                for(TrackedObject ObjecttToUpdate :ObjectsToUpdate){
                    updateLandMarks(ObjecttToUpdate,pose.getPose());
                }
            }
        });

        // Handle TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            // Time Service was terminated 
            if (terminate.getSenderName().equals("TimeService"))
                isTimeServiceTerminated = true;
            // fix this put substring c 
            if(terminate.getSenderType().equals("Camera")){
                fusionSlam.decrementCameraCount();
            }
            if (!terminate.getSenderName().equals("TimeService"))
                fusionSlam.decrementMicroserviceCount(); 
            // All other microservices finished their run
            if (fusionSlam.getMicroservicesCounter() == 0 && isTimeServiceTerminated){
                createOutputFile();
                terminate(); // Fusion Slum's Time to finish 
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, crash -> { 
            error = true; 
        });

        // Subscribes to TickBroadcast, TrackedObjectsEvent, PoseEvent, TerminatedBroadcast,CrashedBroadcast
    }

    private void handleTrackedObjectsEvent(TrackedObjectsEvent trackedObj){
        List<TrackedObject> trackedObjects = trackedObj.getTrackedObjects();
        for (TrackedObject trackO: trackedObjects){
            // If fusion slum holds the relevent pose 
            if (trackO.getTime() <= fusionSlam.getPoses().size()){
                updateLandMarks(trackO,fusionSlam.getPoses().get(trackO.getTime()-1));
            }
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

    private void updateLandMarks(TrackedObject trackedObject,Pose currentPose){
        List<CloudPoint> globaCloudPoints = fusionSlam.poseTranformation(currentPose, trackedObject.getCoordinates());
        // Adding New LandMark
        if (fusionSlam.getLandMarks().get(trackedObject.getId()) == null){
            LandMark newLandMark = new LandMark(trackedObject.getId(), trackedObject.getDescription(), globaCloudPoints);
            fusionSlam.addLandMark(newLandMark);
            StatisticalFolder.getInstance().incrementLandmarks(1);
        } 
        // Improving Coordinates 
        else{
            fusionSlam.updateLandMark(fusionSlam.getLandMarks().get(trackedObject.getId()),globaCloudPoints);
        }
    }

    public void createOutputFile() {
        // Prepare data to serialize
        LinkedHashMap<String, Object> outputData = new LinkedHashMap<>();
        Collection<LandMark> values = fusionSlam.getLandMarks().values(); 
  
        // Creating an ArrayList of values 
        ArrayList<LandMark> landMarkslist = new ArrayList<>(values);
        if (error){
            outputData.put("error",errorCoordinator.getCrashType()+" disconnected");
            outputData.put("faultySensor", errorCoordinator.getFaultSensor());
            outputData.put("lastCamerasFrame",errorCoordinator.getLastFramesCameras());
            outputData.put("lastLiDarWorkerTrackersFrame",errorCoordinator.getLastFramesLidars());
            outputData.put("poses", errorCoordinator.getRobotPoses());
            outputData.put("statistics",StatisticalFolder.getInstance());
        }
        else{
            outputData.put("systemRuntime", StatisticalFolder.getInstance().getSystemRunTime());
            outputData.put("numDetectedObjects", StatisticalFolder.getInstance().getNumDetectedObjects());
            outputData.put("numTrackedObjects", StatisticalFolder.getInstance().getNumTrackedObjects());
            outputData.put("numLandmarks", StatisticalFolder.getInstance().getNumLandmarks());
            outputData.put("landMarks", landMarkslist);
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
