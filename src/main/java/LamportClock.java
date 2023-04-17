public class LamportClock {

    public int getTime(){
        return this.time;
    }

    public void increment(){
        this.time++;
    }

    public void setTime(int newTime){
        this.time = newTime;
    }
    private int time = 0;
}
