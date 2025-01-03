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
        tracker = new LiDarWorkerTracker(1, 1, "src/test/resources/lidar_data.json",1);
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

    @AfterEach
    void tearDown() throws Exception {

    }

    @Test
    void detectToTrackWithSingleObject() {
        TrackedObject trackedObject = tracker.detectToTrack(detectedObject1, timeOfObject1,sender);
        assertNotNull(trackedObject);
        assertEquals(trackedObject.getId(), detectedObject1.getId());
        assertEquals(trackedObject.getDescription(), detectedObject1.getDescription());
        assertEquals(trackedObject.getCoordinates(), coordinates1);
    }

    @Test
    void detectToTrackWithMultipleObjects() {

    }

    @Test
    void detectToTrackAfterClearing() {

    }
}