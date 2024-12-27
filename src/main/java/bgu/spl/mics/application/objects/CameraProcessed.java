package bgu.spl.mics.application.objects;

import java.util.List;

public class CameraProcessed {
    private final int processionTime;
    private final List<DetectedObject> detectedObjects;

    public CameraProcessed(int time,List<DetectedObject> detectedObjects){
        this.processionTime=time;
        this.detectedObjects = detectedObjects;
    }

    public int getProcessionTime() {
        return processionTime;
    }

    public List<DetectedObject> getDetectedObject(){
        return detectedObjects;
    } 
}