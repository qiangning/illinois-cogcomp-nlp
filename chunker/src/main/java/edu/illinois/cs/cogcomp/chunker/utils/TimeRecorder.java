package edu.illinois.cs.cogcomp.chunker.utils;

/**
 * Created by Qiang Ning on 9/7/16.
 */

public class TimeRecorder {
    public enum Unit {seconds, minutes, hours}
    private String name;
    private Unit unit;
    private long startTime;
    private long endTime;
    private long totalTime;

    public TimeRecorder(String name){
        this(name,Unit.seconds);
    }
    public TimeRecorder(String name, Unit unit){
        this.name = name;
        this.unit = unit;
    }
    public void begin(){
        startTime = System.currentTimeMillis();
    }
    public void end(){
        endTime = System.currentTimeMillis();
        totalTime = endTime-startTime;
    }
    public void finish(){
        end();
        printTime();
    }
    public void printTime(){
        switch(unit){
            case seconds:
                System.out.println("TimeRecorder: "+name+" took "+(totalTime/1000.0)+" sec.");
                break;
            case minutes:
                System.out.println("TimeRecorder: "+name+" took "+(totalTime/1000.0/60.0)+" min.");
                break;
            case hours:
                System.out.println("TimeRecorder: "+name+" took "+(totalTime/1000.0/60.0/60.0)+" hr.");
                break;
            default:
                System.out.println("TimeRecorder: Wrong unit encountered.");
        }
    }
    public String getName(){
        return name;
    }
    public Unit getUnit(){
        return unit;
    }
    public long getTotalTime(){
        return totalTime;
    }
}
