package bgu.spl.mics.application.objects;

public class objectDataTracker {
    private String id; 
    private int time;

    public objectDataTracker(String id, int time){
        this.id = id;
        this.time = time;
    }
    
    public String getId(){
        return this.id;
    }

    public int getTime(){
        return this.time;
    }


}