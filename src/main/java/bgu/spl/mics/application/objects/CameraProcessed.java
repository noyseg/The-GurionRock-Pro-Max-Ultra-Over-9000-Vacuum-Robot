package bgu.spl.mics.application.objects;

import java.util.List;

public class CameraProcessed {
    private final int processionTime;
    private final StampedDetectedObjects detectedObjects;

    public CameraProcessed(int time,StampedDetectedObjects detectedObjects){
        this.processionTime=time;
        this.detectedObjects = detectedObjects;
    }

    public int getProcessionTime() {
        return processionTime;
    }

    public StampedDetectedObjects getDetectedObject(){
        return detectedObjects;
    } 
}