package bgu.spl.mics.application.objects;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update
 * a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam
 * exists.
 * 
 * @inv landMarks != null
 * @inv poses != null
 * @inv microservicesCounter >= 0
 * @inv finished == true implies microservicesCounter == 0
 */
public class FusionSlam {
    // Singleton instance holder
    private static class FusionSlamHolder {
        private static FusionSlam instance = new FusionSlam();
    }

    private final HashMap<String, LandMark> landMarks; // Stores the landmarks identified in the map
    private final List<Pose> poses; // Stores the list of robot poses over time
    private int microservicesCounter; // Keeps track of the number of active microservices
    private boolean finished; // Checks if fusionSlam finished his job and can be terminated
    private String outputFilePath; // The filePath of the output file
    private HashMap<Integer, List<TrackedObject>> waitingTracked; // Map to store tracked objects waiting for a pose

    /**
     * Private constructor to initialize the FusionSlam instance.
     * Initializes the collections to store landmarks and poses.
     */
    private FusionSlam() {
        this.landMarks = new HashMap<String, LandMark>();
        this.poses = new LinkedList<Pose>();
        this.microservicesCounter = 0;
        this.finished = false;
        this.outputFilePath = "";
        this.waitingTracked = new HashMap<>();
    }

    /**
     * Returns the singleton instance of FusionSlam.
     *
     * @return The FusionSlam singleton instance.
     */
    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    /**
     * Returns the filePath of the output file
     *
     * @return The filePath of the output file
     */
    public String getOutputFilePath() {
        return this.outputFilePath;
    }

    /**
     * Decrements the count of active microservices by 1.
     */
    public void decrementMicroserviceCount() {
        this.microservicesCounter -= 1;
    }

    /**
     * Returns the map of landmarks in the global map.
     *
     * @return The map of landmarks.
     */
    public HashMap<String, LandMark> getLandMarks() {
        return this.landMarks;
    }

    /**
     *
     * @return true if waitingTracked is empty and false otherwise 
     */
    public boolean isWaitingTrackedEmpty() {
        return this.waitingTracked.isEmpty();
    }

    /**
     * Returns the current count of active microservices.
     *
     * @return The number of active microservices.
     */
    public int getMicroservicesCounter() {
        return this.microservicesCounter;
    }

    /**
     * Returns finished status for fusion slam.
     *
     * @return true if fusionSlam can be terminated and false otherwise.
     */
    public boolean getFinished() {
        return this.finished;
    }

     /**
     * Returns the list of poses recorded by the robot over time.
     *
     * @return The list of poses.
     */
    public List<Pose> getPoses() {
        return this.poses;
    }

    /**
     * Adds a new landmark to the global map.
     *
     * @param lm The new landmark to be added.
     */
    public void addLandMark(LandMark lm) {
        landMarks.put(lm.getId(), lm);
    }

    /**
     * Adds a new pose to the list of poses recorded by the robot.
     *
     * @param p The new pose to be added.
     */
    public void addPose(Pose p) {
        poses.add(p);
    }

    /**
     * Updates landmarks with the given tracked object and current pose.
     * If the landmark is new, it adds it; if it already exists, it updates its
     * coordinates.
     *
     * @param trackedObject The tracked object to update.
     * @param currentPose   The current pose to be used for updating the landmark's
     *                      position.
     * @pre trackedObject != null && currentPose != null
     * @pre trackedObject.getTime() == currentPose.getTime()
     * @post getLandMarks().containsKey(trackedObject.getId())
     */
    public void setLandMarks(TrackedObject trackedObject, Pose currentPose) {
        List<CloudPoint> globalCloudPoints = poseTransformation(currentPose, trackedObject.getCoordinates());
        // If the landmark is new, add it
        if (getLandMarks().get(trackedObject.getId()) == null) {
            LandMark newLandMark = new LandMark(trackedObject.getId(), trackedObject.getDescription(),
                    globalCloudPoints);
            addLandMark(newLandMark);
            StatisticalFolder.getInstance().incrementLandmarks(1);
        }
        // If the landmark exists, update its coordinates
        else {
            updateLandMark(this.landMarks.get(trackedObject.getId()), globalCloudPoints);
        }
    }

    /**
     * Updates an existing landmark with improved coordinates.
     * The new coordinates are calculated as the average between the old and the
     * improved ones.
     *
     * @param landM         The landmark to be updated.
     * @param improvePoints The improved coordinates for the landmark.
     */
    public void updateLandMark(LandMark landM, List<CloudPoint> improvePoints) {
        List<CloudPoint> oldCloudPoints = this.landMarks.get(landM.getId()).getCoordinates();
        List<CloudPoint> newCloudPoints = new LinkedList<>();
        int lenImprovePoints = improvePoints.size();
        int lenOldCloudPoints = oldCloudPoints.size();
        // Calculate the average of oldCloudPoints and improvePoints and store in
        // newCloudPoints
        for (int i = 0; i < lenImprovePoints && i < lenOldCloudPoints; i++) {
            CloudPoint oldP = oldCloudPoints.get(i);
            CloudPoint impP = improvePoints.get(i);
            CloudPoint newCp = new CloudPoint((impP.getX() + oldP.getX()) / 2.0, (impP.getY() + oldP.getY()) / 2.0);
            newCloudPoints.add(newCp);
        }

        // If improvePoints was longer than oldCloudPoints, add the remaining
        // improvePoints
        if (newCloudPoints.size() < lenImprovePoints) {
            for (int i = lenOldCloudPoints; i < lenImprovePoints; i++) {
                CloudPoint impP = improvePoints.get(i);
                CloudPoint newCp = new CloudPoint(impP.getX(), impP.getY());
                newCloudPoints.add(newCp);
            }
        }

        // If oldCloudPoints was longer than improvePoints, add the remaining
        // oldCloudPoints
        if (newCloudPoints.size() < lenOldCloudPoints) {
            for (int i = lenImprovePoints; i < lenOldCloudPoints; i++) {
                CloudPoint oldP = oldCloudPoints.get(i);
                CloudPoint newCp = new CloudPoint(oldP.getX(), oldP.getY());
                newCloudPoints.add(newCp);
            }
        }
        // Set the updated coordinates for the landmark
        this.landMarks.get(landM.getId()).setCoordinates(newCloudPoints);
    }

    /**
     * Transforms a set of cloud points based on the robot's current position
     * (pose).
     * This transformation takes into account the robot's position and orientation
     * to convert
     * local coordinates to global coordinates.
     *
     * @param robotPosition The current pose of the robot.
     * @param cloudPoints   The list of cloud points to be transformed.
     * @return The transformed list of cloud points in the global frame.
     * @pre robotPosition != null && cloudPoints != null
     * @post @return.size() == cloudPoints.size() with correct global coordinates.
     * @inv The input list of cloud points and the robot's pose remain unchanged during the transformation.
     */
    public static List<CloudPoint> poseTransformation(Pose robotPosition, List<CloudPoint> cloudPoints) {
        List<CloudPoint> newCloudPoints = new LinkedList<>();
        float xRobot = robotPosition.getX();
        float yRobot = robotPosition.getY();
        Double radians = robotPosition.getYaw() * Math.PI / 180.0;
        Double cos = Math.cos(radians);
        Double sin = Math.sin(radians);
        for (CloudPoint cp : cloudPoints) {
            Double xCloudPoint = (cos * cp.getX()) - (sin * cp.getY()) + xRobot;
            Double yCloudPoint = (sin * cp.getX()) + (cos * cp.getY()) + yRobot;
            CloudPoint newCloudPoint = new CloudPoint(xCloudPoint, yCloudPoint);
            newCloudPoints.add(newCloudPoint);
        }
        return newCloudPoints;
    }

    /**
     * Sets the count of active microservices.
     */
    public void setMicroserviceCount(int microservicesCounter) {
        this.microservicesCounter = microservicesCounter;
    }

    /**
     * Sets finish to be true
     */
    public void setFinished() {
        this.finished = true;
    }

    /**
     * Sets a new output file path
     */
    public void setOutputFilePath(String newOutputFilePath) {
        this.outputFilePath = newOutputFilePath;
    }

    /**
     * Handles the TrackedObjectsEvent and updates landmarks with new tracked objects.
     * If the relevant pose is not available, it stores the tracked object until the pose arrives.
     *
     * @param trackedObj The TrackedObjectsEvent containing the list of tracked objects.
     */
    public void handleTrackedObjectsEvent(TrackedObjectsEvent trackedObj){
        List<TrackedObject> trackedObjects = trackedObj.getTrackedObjects();
        for (TrackedObject trackO: trackedObjects){
            // If FusionSLAM has the relevant pose
            if (trackO.getTime() <= this.poses.size()){
                setLandMarks(trackO,this.poses.get(trackO.getTime()-1));
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

    public void handlePoseEvent(PoseEvent pose){
        addPose(pose.getPose());
        List<TrackedObject> ObjectsToUpdate = waitingTracked.get(pose.getPose().getTime());
        if (ObjectsToUpdate != null){
            for(TrackedObject ObjectToUpdate :ObjectsToUpdate){
                setLandMarks(ObjectToUpdate,pose.getPose());
            }
            waitingTracked.remove(pose.getPose().getTime());
        }
    }


    /**
     * Creates the output file in JSON format containing the final system statistics and data.
     * The output file includes information about landmarks, error descriptions, and sensor data.
     */
    public void createOutputFile(boolean error) {
        // Prepare data to serialize
        LinkedHashMap<String, Object> outputData = new LinkedHashMap<>();
        StatisticalFolder.getInstance().setLandMarksMap(this.landMarks);
        if (error){
            // If an error occurred, add error details to the output
            outputData.put("error",ErrorCoordinator.getInstance().getDescription());
            outputData.put("faultySensor", ErrorCoordinator.getInstance().getFaultSensor());
            outputData.put("lastCamerasFrames",ErrorCoordinator.getInstance().getLastFramesCameras());
            outputData.put("lastLidarFrames",ErrorCoordinator.getInstance().getLastFramesLidars());
            outputData.put("poses", ErrorCoordinator.getInstance().getRobotPoses());
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
        if (error){
            try (FileWriter writer = new FileWriter(this.outputFilePath + "/OutputError.json")) {
                gson.toJson(outputData, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try (FileWriter writer = new FileWriter(this.outputFilePath + "/output_file.json")) {
                gson.toJson(outputData, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
