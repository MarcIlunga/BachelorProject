package lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers;

public class StopWatch {

    private long startTime;
    private long stopTime;
    private static StopWatch stopWatch;


    private StopWatch(){

    }

    public void start(){
        startTime = System.currentTimeMillis();
    }

    public void stop(){
        stopTime = System.currentTimeMillis();
    }

    public long ellapsedTime(){
        return stopTime - startTime;
    }

    public static StopWatch getStopWatch(){
        if(stopWatch == null){
            stopWatch = new StopWatch();
        }
        return stopWatch;
    }
}
