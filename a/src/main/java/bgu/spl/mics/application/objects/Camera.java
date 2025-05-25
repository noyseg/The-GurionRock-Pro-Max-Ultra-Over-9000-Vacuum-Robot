package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int id; // Unique identifier for the camera.
    private final String name; // The name for the camera.
    private final int frequency; // Camera's frequency 
    private STATUS status; // Current operational status of the camera ((UP, DOWN, ERROR).
    private final List<StampedDetectedObjects> detectedObjectsList; // A list containing detected objects along with timestamps.
    private LinkedList<CameraProcessed> waitingQueue; // Queue of camera's waiting data to be sent
    private StampedDetectedObjects lastDetectedObjects; // The last detected objects by the camera

        /**
     * Constructs a Camera object.
     *
     * @param id                  The unique identifier for the camera.
     * @param frequency           The detection frequency of the camera.
     * @param detectedObjectsList Initial list of detected objects with timestamps.
     */
    public Camera(int id, int frequency, List<StampedDetectedObjects> detectedObjectsList) {
        this.id = id;
        this.name = "Camera" + String.valueOf(id);
        this.frequency = frequency;
        this.detectedObjectsList = new LinkedList<>(detectedObjectsList);
        this.status = STATUS.UP; // Cameras start in the UP (active) state.
        this.waitingQueue = new LinkedList<>();
        lastDetectedObjects = new StampedDetectedObjects();
    }

    /**
     * Retrieves the unique ID of the camera.
     *
     * @return The camera's ID as an integer.
     */
     public int getID() {
        return id;
    }

    /**
     * Retrieves the camera's name.
     *
     * @return A string representing the name of the camera.
     */
    public String getName(){
        return this.name;
    }

    /**
     * Retrieves the current operational status of the camera.
     *
     * @return The camera's status as a `STATUS` enum value (e.g., UP, DOWN, ERROR).
     */
    public STATUS getStatus() {
        return status;
    }

    /**
     * Retrieves the camera's detection frequency.
     *
     * @return An integer representing the frequency of detections (e.g., every N ticks).
     */
    public int getFrequency() {
        return frequency;
    }

    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
    }

    /**
     *
     * @return Camera's lastDetectedObjects
     */
    public StampedDetectedObjects getLastDetectedObjects() {
        return lastDetectedObjects;
    }

    /**
     * Updates the camera's operational status.
     *
     * @param status The new status to set (e.g., UP, DOWN, ERROR).
    */
    public void setStatus(STATUS error) {
        this.status = error;
    }

    /**
     * Processes a {@link TickBroadcast} to detect objects at the appropriate frequency.
     * Handles detected objects, processes errors, and checks if detections are ready to be sent.
     *
     * @param tick The {@link TickBroadcast} containing the current simulation time.
     * @return A {@link DetectObjectsEvent} if detections are ready to be sent, otherwise null.
     */
    public DetectObjectsEvent processTick(TickBroadcast tick) {
        // Check if there are detections remaining
        if (this.detectedObjectsList.size() != 0) {
            // Potential detected objects at tick time
            StampedDetectedObjects nextDetectedObjects = getDetectedObjectsList().get(0);
            int currentTickTime = tick.getCurrentTime();
            if (currentTickTime == nextDetectedObjects.getTime()) {
                boolean error = checkForError(nextDetectedObjects, currentTickTime);
                // Case that no error was detected
                if (!error){
                    putOnWaitingQueue(nextDetectedObjects, currentTickTime);
                }
                else{
                    return null;
                }
            }
        }
        // Objects are ready to be sent to lidar
        DetectObjectsEvent doe = detectionToSend(tick.getCurrentTime());
        // Checks if camera finishes its job and terminate in case it is
        checkIfFinish();
        return doe;
    }


     /**
     * Checks if there are any errors in the detected objects.
     * If an error is found, the camera status is set to ERROR, and the error is reported to the {@link ErrorCoordinator}.
     *
     * @param nextDetectedObjects The detected objects to check.
     * @param tickTime            The current tick time.
     * @return {@code true} if an error was detected, otherwise {@code false}.
     */
    private boolean checkForError(StampedDetectedObjects nextDetectedObjects, int tickTime) {
        for (DetectedObject dob : nextDetectedObjects.getDetectedObjects()) {
            // Error was detected
            if (dob.getId().equals("ERROR")) {
                setStatus(STATUS.ERROR);
                ErrorCoordinator.getInstance().setLastFramesCameras(getName(), lastDetectedObjects);
                ErrorCoordinator.getInstance().setCrashed("Camera "+ String.valueOf(getID()), tickTime, dob.getDescription());
                return true;
            }
        }
        return false;
    }

    /**
     * Adds detected objects to the waiting queue for processing and updates system statistics.
     *
     * @param nextDetectedObjects The detected objects to be added to the waiting list.
     * @param tickTime            The current tick time when the objects were detected.
     */
    private void putOnWaitingQueue(StampedDetectedObjects nextDetectedObjects, int tickTime) {

        // Calculate the processing time based on the camera's frequency
        int processingTime = tickTime + this.frequency;

        // Create a CameraProcessed object with the processing time and detected objects

        CameraProcessed processedObject = new CameraProcessed(processingTime, nextDetectedObjects);
        // Add the processed object to the waiting list
        waitingQueue.add(processedObject);
        lastDetectedObjects = nextDetectedObjects;
        // Remove the detected objects from the camera's list and update statistics
        int detectedObjectCount = this.detectedObjectsList.remove(0).getDetectedObjects().size();
        StatisticalFolder.getInstance().incrementDetectedObjects(detectedObjectCount);
    }

    /**
     * Sends detected objects to the LiDAR service if their processing time matches the current tick.
     *
     * @param tickTime The current tick time.
     * @return A {@link DetectObjectsEvent} if detections are ready to be sent, otherwise null.
     */
    private DetectObjectsEvent detectionToSend(int tickTime) {

        // Process detections that are ready to be sent at the current tick
        if (getStatus() == STATUS.UP && !waitingQueue.isEmpty()
                && waitingQueue.getFirst().getProcessionTime() == tickTime) {

            // Remove the first waiting detection from the queue
            CameraProcessed toLidar = waitingQueue.removeFirst();

            // Extract the stamped detected objects
            StampedDetectedObjects stampedToLiDar = toLidar.getDetectedObject();

            // Create a DetectObjectsEvent
            DetectObjectsEvent doe = new DetectObjectsEvent(stampedToLiDar, stampedToLiDar.getTime(), getName());

            return doe;
        }
        return null;
    }

    /**
     * Checks if the camera has finished all its detections and terminates the
     * service if so.
     * Conditions for termination:
     * - The camera's status is `UP` (still active).
     * - There are no remaining detections.
     * - All waiting objects have been sent.
     */
    private void checkIfFinish() {
        if (getStatus() == STATUS.UP && this.detectedObjectsList.size() == 0 && waitingQueue.isEmpty()) {
            setStatus(STATUS.DOWN);
            ErrorCoordinator.getInstance().setLastFramesCameras(getName(), lastDetectedObjects);
        }
    }

    /**
     * Provides a string representation of the camera object, useful for debugging or logging.
     *
     * @return A string summarizing the camera's key attributes, including ID, name, 
     *         frequency, status, and detected objects.
     */
     @Override
     public String toString() {
         return "Camera{" +
             "id=" + id +
             ", name='" + name + '\'' +
             ", frequency=" + frequency +
             ", status=" + status +
             ", detectedObjectsList=" + detectedObjectsList.toArray().toString() +
             '}';
     }
}
         
