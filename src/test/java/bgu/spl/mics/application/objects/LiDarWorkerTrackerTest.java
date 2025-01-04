package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
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
    String sender;
    int timeOfObject1;
    int timeOfObject2;

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
    }

//    @AfterEach
//    void tearDown() throws Exception {
//
//    }

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

    @Test
    void detectToTrackAfterClearing() {

    }
}