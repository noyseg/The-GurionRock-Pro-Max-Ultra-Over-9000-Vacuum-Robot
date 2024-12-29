package bgu.spl.mics.application.messages;
import bgu.spl.mics.application.objects.*;

import bgu.spl.mics.Event;

public class PoseEvent implements Event<Boolean> {
    private final Pose pose;

    public PoseEvent(Pose pose){
        this.pose = pose;
    }

    public Pose getPose(){
        return this.pose;
    }
    
}
