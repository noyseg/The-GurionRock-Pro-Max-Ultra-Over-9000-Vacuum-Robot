package bgu.spl.mics.application.messages;
import bgu.spl.mics.application.objects.*;

import bgu.spl.mics.Event;

public class PoseEvent implements Event<Boolean> {
    private final Pose pose;
    //private final String senderName;

    public PoseEvent(Pose pose){
        this.pose = pose;
        //this.senderName = senderName;
    }

    public Pose getPose(){
        return this.pose;
    }

    //public String getSenderName(){
    //    return this.senderName;
    //}
    
}
