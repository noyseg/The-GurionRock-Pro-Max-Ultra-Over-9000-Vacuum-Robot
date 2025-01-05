package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping
 * (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update
 * a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam
 * exists.
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

    /**
     * Private constructor to initialize the FusionSlam instance.
     * Initializes the collections to store landmarks and poses.
     */
    private FusionSlam() {
        this.landMarks = new HashMap<String, LandMark>();
        this.poses = new LinkedList<Pose>();
        this.microservicesCounter = 0;
        this.finished = false;
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
     * Returns the list of poses recorded by the robot over time.
     *
     * @return The list of poses.
     */
    public List<Pose> getPoses() {
        return this.poses;
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
     * @post result.size() == cloudPoints.size() with correct global coordinates.
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

}
