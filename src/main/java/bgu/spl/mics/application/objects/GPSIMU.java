package bgu.spl.mics.application.objects;

import java.util.List;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private List<Pose> PoseList;
    private int numOfCameras;
    private int numOfLidars;


    public GPSIMU(int currentTick, List<Pose> PoseList, int numOfCameras, int numOfLidars){
        this.currentTick = currentTick;
        this.status = STATUS.UP;
        this.PoseList = PoseList;
        this.numOfCameras = numOfCameras;
        this.numOfLidars = numOfLidars;
    }

    public int getCurrentTime(){
        return this.currentTick;
    }

    public STATUS getStatus(){
        return this.status;
    }

    public List<Pose> getPoseList(){
        return this.PoseList;
    }

    public void setCurrentTick(int updateTick){
         this.currentTick = updateTick;
    }

    public void setStatus(STATUS status){
        this.status = status;
    }

    public void setPoseList( List<Pose> PoseList){
        this.PoseList = PoseList;
    }

    public Pose getPose(){
        if (PoseList.size() >= currentTick){
            return this.PoseList.get(currentTick-1);
        }
        return null;
    }

    public PoseEvent processTick() {
        PoseEvent poseEvent = new PoseEvent(getPose());
        return poseEvent;
    }

    public void updateLastPose(){
        ErrorCoordinator.getInstance().setRobotPoses(getPose());
    }

    public int getCameraCount(){
        return this.numOfCameras;
    }

    public void setCameraCount(){
        this.numOfCameras -= 1;
    }

    public int getLidarCount(){
        return this.numOfCameras;
    }

    public void setLidarCount(){
        this.numOfCameras -= 1;
    }



}
