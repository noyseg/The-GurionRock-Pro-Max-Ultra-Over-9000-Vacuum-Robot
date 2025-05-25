package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FusionSlamTest {
    private FusionSlam fusionSlam;
    // New LandMark
    Double x1;
    Double y1;
    Double x2;
    Double y2;
    CloudPoint c1;
    CloudPoint c2;
    List<CloudPoint> Door;
    Pose p;
    List<CloudPoint> globalDoor;

    List<CloudPoint> Door1;
    Double x3;
    Double y3;
    Double x12;
    Double y12;
    Double x22;
    Double y22;
    CloudPoint c12;
    CloudPoint c22;
    CloudPoint c3;
    Pose p1;
    List<CloudPoint> globalDoor1;

    List<CloudPoint> Door2;
    Double x13;
    Double y13;
    CloudPoint c13;
    Pose p2;
    List<CloudPoint> globalDoor2;

    /**
     * Sets up the test environment for the FusionSlamTest class.
     * Initializes the FusionSlam instance, landmarks, and various CloudPoint and Pose objects
     * to be used in the test cases.
     */
    @BeforeEach
    void setUp() {
        this.fusionSlam = FusionSlam.getInstance();
        x1 = 0.5;
        y1 = -2.1;
        x2 = 0.8;
        y2 = -2.3;
        c1 = new CloudPoint(x1, y1);
        c2 = new CloudPoint(x2, y2);
        Door = new LinkedList<>();
        Door.add(c1);
        Door.add(c2);
        p = new Pose(1, -2.366f, 0.9327f, -28.08f);
        globalDoor = FusionSlam.poseTransformation(p, Door);

        Door1 = new LinkedList<>();
        x3 = 0.7;
        y3 = -2.2;
        x12 = 0.49;
        y12 = -2.17;
        x22 = 0.79;
        y22 = -2.31;
        c12 = new CloudPoint(x12, y12);
        c22 = new CloudPoint(x22, y22);
        c3 = new CloudPoint(x3, y3);
        p1 = new Pose(2, -0.436f, 2.8818f, 38.85f);
        Door1.add(c12);
        Door1.add(c22);
        Door1.add(c3);
        globalDoor1 = FusionSlam.poseTransformation(p1, Door1);

        Door2 = new LinkedList<>();
        x13 = 0.78;
        y13 = -2.4;
        c13 = new CloudPoint(x13, y13);
        Door2.add(c13);
        p2 = new Pose(3, 0.5f, 4.4f, 68.76f);
        globalDoor2 = FusionSlam.poseTransformation(p2, Door2);
    }

    /**
     * Tests the pose transformation functionality of the FusionSlam class.
     * Verifies that the transformed coordinates of CloudPoints in various doors
     * match the expected values after applying the pose transformation.
     * The test checks the x and y coordinates of each CloudPoint in the globalDoor,
     * globalDoor1, and globalDoor2 lists against the expected values.
     */
    @Test
    void poseTransformationTest() {
        assertEquals(globalDoor.get(0).getX(), -2.913332578606659);
        assertEquals(globalDoor.get(0).getY(), -1.1554635639732926);
        assertEquals(globalDoor.get(1).getX(), -2.742785996686237);
        assertEquals(globalDoor.get(1).getY(), -1.4731329886827864);

        assertEquals(globalDoor1.get(0).getX(), 1.3068130629604096);
        assertEquals(globalDoor1.get(0).getY(), 1.4991927382298889);
        assertEquals(globalDoor1.get(1).getX(), 1.628270035319742);
        assertEquals(globalDoor1.get(1).getY(), 1.5783471122187698);
        assertEquals(globalDoor1.get(2).getX(), 1.4891776552338944);
        assertEquals(globalDoor1.get(2).getY(), 1.6075585791804652);

        assertEquals(globalDoor2.get(0).getX(), 3.019545461240221);
        assertEquals(globalDoor2.get(0).getY(), 4.257554776933302);


    }

    /**
     * Tests the functionality of setting landmarks in the FusionSlam system.
     * This test verifies that the landmarks are correctly transformed and stored
     * in the FusionSlam instance when provided with tracked objects and their poses.
     * <p>
     * The test checks the transformed coordinates of the landmarks against expected values.
     */
    @Test
    void setLandMarksTest() {
        TrackedObject trackedObject = new TrackedObject(1, "door", "door", Door);
        fusionSlam.setLandMarks(trackedObject, p);
        assertEquals(trackedObject.getTime(),p.getTime());
        assertEquals(-2.913332578606659, fusionSlam.getLandMarks().get("door").getCoordinates().get(0).getX());
        assertEquals(-1.1554635639732926, fusionSlam.getLandMarks().get("door").getCoordinates().get(0).getY());
        assertEquals(-2.742785996686237, fusionSlam.getLandMarks().get("door").getCoordinates().get(1).getX());
        assertEquals(-1.4731329886827864, fusionSlam.getLandMarks().get("door").getCoordinates().get(1).getY());

        TrackedObject trackedObject1 = new TrackedObject(2, "door", "door", Door1);
        fusionSlam.setLandMarks(trackedObject1, p1);
        assertEquals(trackedObject1.getTime(),p1.getTime());
        assertEquals(-0.8032597578231248, fusionSlam.getLandMarks().get("door").getCoordinates().get(0).getX());
        assertEquals(0.1718645871282981, fusionSlam.getLandMarks().get("door").getCoordinates().get(0).getY());
        assertEquals(-0.5572579806832476, fusionSlam.getLandMarks().get("door").getCoordinates().get(1).getX());
        assertEquals(0.05260706176799168, fusionSlam.getLandMarks().get("door").getCoordinates().get(1).getY());
        assertEquals(1.4891776552338944, fusionSlam.getLandMarks().get("door").getCoordinates().get(2).getX());
        assertEquals(1.6075585791804652, fusionSlam.getLandMarks().get("door").getCoordinates().get(2).getY());

        TrackedObject trackedObject2 = new TrackedObject(3, "door", "door", Door2);
        fusionSlam.setLandMarks(trackedObject2, p2);
        assertEquals(trackedObject2.getTime(),p2.getTime());
        assertEquals(1.108142851708548, fusionSlam.getLandMarks().get("door").getCoordinates().get(0).getX());
        assertEquals(2.2147096820308003, fusionSlam.getLandMarks().get("door").getCoordinates().get(0).getY());
        assertEquals(-0.5572579806832476, fusionSlam.getLandMarks().get("door").getCoordinates().get(1).getX());
        assertEquals(0.05260706176799168, fusionSlam.getLandMarks().get("door").getCoordinates().get(1).getY());
        assertEquals(1.4891776552338944, fusionSlam.getLandMarks().get("door").getCoordinates().get(2).getX());
        assertEquals(1.6075585791804652, fusionSlam.getLandMarks().get("door").getCoordinates().get(2).getY());

    }
}