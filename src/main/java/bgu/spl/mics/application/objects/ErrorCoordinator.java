package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

public class ErrorCoordinator {
    private static class ErrorCoordinatorHolder {
        private static ErrorCoordinator instance = new ErrorCoordinator();
    }
    private List<List<DetectedObject>> lastFramesCameras;
    private List<List<TrackedObject>> lastFramesLidars;

    private ErrorCoordinator(){
        this.lastFramesCameras = new LinkedList<List<DetectedObject>>();
        this.lastFramesLidars = new LinkedList<List<TrackedObject>>();
    }

    public static ErrorCoordinator getInstance(){
        return ErrorCoordinatorHolder.instance;
    }

    
}