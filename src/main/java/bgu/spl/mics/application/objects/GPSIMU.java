package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private List<Pose> PoseList;

    public GPSIMU(int currentTick, List<Pose> PoseList){
        this.currentTick = currentTick;
        this.status = STATUS.UP;
        this.PoseList = PoseList;
    }

    public int getCurrentTime(){
        return this.currentTick;
    }

    public STATUS getsStatus(){
        return this.status;
    }

    public List<Pose> getPoseList(){
        return this.PoseList;
    }

    public void setCurrentTick(int updateTick){
         this.currentTick = updateTick;
    }

    public void setsStatus(STATUS status){
        this.status = status;
    }

    public void setPoseList( List<Pose> PoseList){
        this.PoseList = PoseList;
    }

    public Pose getPose(){
        return this.PoseList.get(currentTick-1);
    }

}
