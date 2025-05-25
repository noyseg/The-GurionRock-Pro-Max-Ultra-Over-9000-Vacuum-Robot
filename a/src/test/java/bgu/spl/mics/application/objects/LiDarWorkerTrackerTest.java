package bgu.spl.mics.application.objects;


import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class LiDarWorkerTrackerTest {


    private LiDarWorkerTracker tracker;
    private List<CloudPoint> coordinates1 = new LinkedList<>();
    private List<CloudPoint> coordinates2 = new LinkedList<>();
    DetectedObject detectedObject1;
    DetectedObject detectedObject2;
    DetectedObject detectedObject3;
    String sender;
    int timeOfObject1;
    int timeOfObject2;

    TickBroadcast tick1;

    @BeforeEach
    void setUp() {
        tracker = new LiDarWorkerTracker(1, 1, "src/test/resources/lidar_data_toTest.json", 1);
        detectedObject1 = new DetectedObject("Wall_1", "Wall1");
        timeOfObject1 = 2;

        coordinates1.add(new CloudPoint(0.1176, 3.6969));
        coordinates1.add(new CloudPoint(0.11362, 3.6039));
        detectedObject2 = new DetectedObject("Wall_3", "Wall3");
        timeOfObject2 = 4;
        coordinates2.add(new CloudPoint(3.0451, -0.38171));
        coordinates2.add(new CloudPoint(3.0637, -0.17392));
        sender = "camera1";

        tick1 = new TickBroadcast(6);
        detectedObject3 = new DetectedObject("ERROR", "errorTest");

    }

    /**
     * Tests the detectToTrack method of LiDarWorkerTracker with a single object.
     * This test verifies that the tracker correctly prepares data(DetectObject) before sending it to the FusionService,
     * ensuring that the returned TrackedObject matches the input DetectedObject in terms of
     * ID, description, and coordinates.
     * <p>
     * The test performs the following checks:
     * 1. Verifies that a non-null TrackedObject is returned.
     * 2. Confirms that the ID and description of the TrackedObject match the input DetectedObject.
     * 3. Ensures that the number of coordinates in the TrackedObject matches the expected number.
     * 4. Compares each coordinate (x and y values) of the TrackedObject with the expected coordinates.
     */
    @Test
    void detectToTrackWithSingleObject() {
        TrackedObject trackedObject = tracker.detectToTrack(detectedObject1, timeOfObject1, sender);
        assertNotNull(trackedObject);
        assertEquals(trackedObject.getId(), detectedObject1.getId());
        assertEquals(trackedObject.getDescription(), detectedObject1.getDescription());

        List<CloudPoint> trackedCoordinates = trackedObject.getCoordinates();
        assertEquals(coordinates1.size(), trackedCoordinates.size());

        for (int i = 0; i < coordinates1.size(); i++) {
            CloudPoint expected = coordinates1.get(i);
            CloudPoint actual = trackedCoordinates.get(i);
            assertEquals(expected.getX(), actual.getX(), 0.0001);
            assertEquals(expected.getY(), actual.getY(), 0.0001);
        }
    }

    /**
     * Tests the detectToTrack method of LiDarWorkerTracker with multiple objects.
     * This test verifies that the tracker correctly processes multiple DetectedObjects
     * and returns corresponding TrackedObjects with matching IDs, descriptions, and coordinates.
     * <p>
     * The test performs the following checks for each object:
     * 1. Verifies that a non-null TrackedObject is returned.
     * 2. Confirms that the ID and description of the TrackedObject match the input DetectedObject.
     * 3. Ensures that the number of coordinates in the TrackedObject matches the expected number.
     * 4. Compares each coordinate (x and y values) of the TrackedObject with the expected coordinates.
     */
    @Test
    void detectToTrackWithMultipleObjects() {
        TrackedObject trackedObject1 = tracker.detectToTrack(detectedObject1, timeOfObject1, sender);
        TrackedObject trackedObject2 = tracker.detectToTrack(detectedObject2, timeOfObject2, sender);

        assertNotNull(trackedObject1);
        assertEquals(trackedObject1.getId(), detectedObject1.getId());
        assertEquals(trackedObject1.getDescription(), detectedObject1.getDescription());

        List<CloudPoint> trackedCoordinates1 = trackedObject1.getCoordinates();
        assertEquals(coordinates1.size(), trackedCoordinates1.size());

        for (int i = 0; i < coordinates1.size(); i++) {
            CloudPoint expected = coordinates1.get(i);
            CloudPoint actual = trackedCoordinates1.get(i);
            assertEquals(expected.getX(), actual.getX(), 0.0001);
            assertEquals(expected.getY(), actual.getY(), 0.0001);
        }

        assertNotNull(trackedObject2);
        assertEquals(trackedObject2.getId(), detectedObject2.getId());
        assertEquals(trackedObject2.getDescription(), detectedObject2.getDescription());

        List<CloudPoint> trackedCoordinates2 = trackedObject2.getCoordinates();
        assertEquals(coordinates2.size(), trackedCoordinates2.size());

        for (int i = 0; i < coordinates2.size(); i++) {
            CloudPoint expected = coordinates2.get(i);
            CloudPoint actual = trackedCoordinates2.get(i);
            assertEquals(expected.getX(), actual.getX(), 0.0001);
            assertEquals(expected.getY(), actual.getY(), 0.0001);
        }
    }
    /**
     * Tests the processTick method of the LiDarWorkerTracker.
     * This test verifies the behavior of the tracker when processing tick broadcasts,
     * including handling of detection events that can be sent immediately or need to be queued(processed).
     * <p>
     * The test performs the following checks:
     * 1. Verifies that no TrackedObjectsEvent is returned when there are no objects to send.
     * 2. Confirms that a detection event can be processed and sent immediately.
     * 3. Ensures that a detection event that cannot be sent immediately is queued.
     * 4. Checks that queued events are processed and sent at the time they should.
     * 5. Validates that the tracker handles "ERROR" objects correctly.
     */
    @Test
    void testProcessTick() {
        // Testing process tick without tracking objects to send
        TrackedObjectsEvent trackedEvent1 = tracker.processTick(tick1);
        assertNull(trackedEvent1);

        // Testing if a detection event that the lidar can send immediately
        List<DetectedObject> detectedObjects1 = new LinkedList<>();
        detectedObjects1.add(detectedObject1);
        DetectObjectsEvent event1 = new DetectObjectsEvent(new StampedDetectedObjects(2, detectedObjects1), 5, sender);
        TrackedObjectsEvent trackedEvent2 = tracker.processDetectedEvent(event1);
        assertNotNull(trackedEvent2);
        assertFalse(tracker.getEventToProcess().contains(event1));

        // Testing if a detection event that the lidar can't send immediately
        List<DetectedObject> detectedObjects2 = new LinkedList<>();
        detectedObjects2.add(detectedObject2);
        // Current tick is 6
        DetectObjectsEvent event2 = new DetectObjectsEvent(new StampedDetectedObjects(4, detectedObjects2), 6, sender);
        TrackedObjectsEvent trackedEvent3 = tracker.processDetectedEvent(event2);
        assertNull(trackedEvent3);
        assertTrue(tracker.getEventToProcess().contains(event2));
        assertFalse(tracker.getEventToProcess().isEmpty());

        // Testing process tick which the lidar should send TrackedObjects that were previously processed
        TickBroadcast tick2 = new TickBroadcast(7);
        TrackedObjectsEvent trackedEvent4 = tracker.processTick(tick2);
        assertNotNull(trackedEvent4);
        assertTrue(tracker.getEventToProcess().isEmpty());

        // Testing process tick which the lidar should track and "ERROR" object
        TickBroadcast tick3 = new TickBroadcast(8);
        TrackedObjectsEvent trackedEvent5 = tracker.processTick(tick3);
        assertNull(trackedEvent5);
        assertTrue(tracker.getEventToProcess().isEmpty());


    }
}