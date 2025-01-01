package bgu.spl.mics.application.objects;

public class ObjectDataTracker {
    private String id; 
    private int time;

    public ObjectDataTracker(String id, int time){
        this.id = id;
        this.time = time;
    }
    
    public String getId(){
        return this.id;
    }

    public int getTime(){
        return this.time;
    }

    @Override
    public String toString() {
        return "ObjectDataTracker{" +
               "id='" + id + '\'' +
               ", time=" + time +
               '}';
    }


}