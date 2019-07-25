package com.PPRZonDroid;

import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by bwelton on 9/28/18.
 */

public class EventLogger {
    public static final int TAKEOFF = 0;
    public static final int LANDING = 1;
    public static final int EXECUTE = 2;
    public static final int PAUSE = 3;
    public static final int INSPECTION_LAUNCH = 4;
    public static final int INSPECTION_CLOSE = 5;
    public static final int INSPECTION_COMMAND_START = 6;
    public static final int INSPECTION_COMMAND_END = 7;
    public static final int WAYPOINT_CREATE = 8;
    public static final int WAYPOINT_DELETE = 9;
    public static final int WAYPOINT_MOVE = 10;
    public static final int WAYPOINT_ALTITUDE_ADJUST = 11;
    public static final int NO_EVENT = 12;
    public static final int START_ACTIVITY = 100;
    public static final int END_ACTIVITY = 101;

    private static final String SD_PATH = "/storage/A9B1-0805/Android/data/com.PPRZonDroid/files/";
    //private static final String SD_PATH = "This PC/Lenovo TAB 2 A10-70F/Internal storage/Android/data/com.PPRZonDroid/files/";
    private static final LatLng LAB_ORIGIN = new LatLng(36.005417, -78.940984);
    private File file;
    private FileWriter writer;

    private boolean inFlight;
    private boolean activityStarted;
    private long startTime;
    private long totalTime;

    public EventLogger(String filename){
        try {
            File path = new File(SD_PATH);
            if(!path.exists()){
                path.mkdir();
            }
            file = new File(SD_PATH + filename);
            writer = new FileWriter(file, true);
            buildFileHeaders();
            inFlight = false;
            activityStarted = false;
        } catch (IOException e) {
            Log.d("DroneLogging", "Failed to initialize the file writer.");
            e.printStackTrace();
        }
    }


    private void buildFileHeaders(){
        try {
            writer.append("Current Time (ms),");
            writer.append("Flight Time (s),");
            writer.append("Altitude (m),");
            writer.append("X-Position (m),");
            writer.append("Y-Position (m),");
            writer.append("Event,");
            writer.append("Manual Command,");
            writer.append("WP Start X,");
            writer.append("WP Start Y,");
            writer.append("WP Start Altitude,");
            writer.append("WP End X,");
            writer.append("WP End Y,");
            writer.append("WP End Altitude\n");

        } catch (IOException e) {
            Log.d("DroneLogging", "Failed to initialize the headers");
            e.printStackTrace();
        }
    }

    protected void logEvent(
            Telemetry.AirCraft aircraft,
            int event,
            float manualCommand) {
        if (activityStarted) {
            try {
                printRequiredInformation(aircraft, event, manualCommand);
                writer.append("-,"); //waypoint start x
                writer.append("-,"); //waypoint start y
                writer.append("-,"); //waypoint start alt
                writer.append("-,"); //waypoint end x
                writer.append("-,"); //waypoint end y
                writer.append("-\n"); //waypoint end alt
                writer.flush();

            } catch (IOException e) {
                Log.d("DroneLogging", "Failed to append a new line of data.");
                e.printStackTrace();
            }
        }
    }

    protected void logWaypointEvent(
            Telemetry.AirCraft aircraft,
            int event,
            float manualCommand,
            @Nullable LatLng startPosition,
            @Nullable LatLng endPosition,
            @Nullable String startAltitude,
            @Nullable String endAltitude) {
        try {
            printRequiredInformation(aircraft, event, manualCommand);

            if(startPosition != null) {
                //writer.append(Double.toString(latitude_to_relative_position(startPosition)));
                writer.append(Double.toString(longitude_to_relative_position_lab(startPosition)));
                writer.append(",");
                //writer.append(Double.toString(longitude_to_relative_position(startPosition)));
                writer.append(Double.toString(latitude_to_relative_position_lab(startPosition)));
                writer.append(",");
            }else{
                writer.append("-,");
                writer.append("-,");
            }
            writer.append(startAltitude != null ? startAltitude : "-");
            writer.append(",");

            if(endPosition != null) {
                //writer.append(Double.toString(latitude_to_relative_position(endPosition)));
                writer.append(Double.toString(longitude_to_relative_position_lab(endPosition)));
                writer.append(",");
                //writer.append(Double.toString(longitude_to_relative_position(endPosition)));
                writer.append(Double.toString(latitude_to_relative_position_lab(endPosition)));
                writer.append(",");
            }else{
                writer.append("-,");
                writer.append("-,");
            }
            writer.append(endAltitude != null ? endAltitude : "-");
            writer.append("\n");

            writer.flush();

        } catch (IOException e) {
            Log.d("DroneLogging", "Failed to append a new line of data.");
            e.printStackTrace();
        }
    }

    private void printRequiredInformation(
            Telemetry.AirCraft aircraft,
            int event,
            float manualCommand) {
        try {
            writer.append(Long.toString(totalTime));
            writer.append(",");
            if (inFlight){
                writer.append(aircraft.RawFlightTime);
                writer.append(",");
                writer.append(aircraft.LoggedAdjustedAltitude);
                writer.append(",");
                writer.append(Double.toString(longitude_to_relative_position(aircraft.Position)));
                writer.append(",");
                writer.append(Double.toString(latitude_to_relative_position(aircraft.Position)));
                writer.append(",");
            }
            else {
                writer.append("*");
                writer.append(",");
                writer.append("*");
                writer.append(",");
                writer.append("*");
                writer.append(",");
                writer.append("*");
                writer.append(",");
            }
            writer.append(Integer.toString(event));
            writer.append(",");
            writer.append(Float.toString(manualCommand));
            writer.append(",");
        }
        catch (IOException e) {
            Log.d("DroneLogging", "Failed to append a new line of data.");
            e.printStackTrace();
        }
    }

    private double latitude_to_relative_position(LatLng latLng){
        return (MainActivity.convert_to_lab(latLng).latitude -
                //MainActivity.convert_to_lab(LAB_ORIGIN).latitude)*35879.28814;
                MainActivity.convert_to_lab(LAB_ORIGIN).latitude)*20714.99254;
    }

    private double longitude_to_relative_position(LatLng latLng){
        return (MainActivity.convert_to_lab(latLng).longitude -
                //MainActivity.convert_to_lab(LAB_ORIGIN).longitude)*29709.19639;
                MainActivity.convert_to_lab(LAB_ORIGIN).longitude)*16619.76984;
    }
    private double latitude_to_relative_position_lab(LatLng latLng){
        return (latLng.latitude - MainActivity.convert_to_lab(LAB_ORIGIN).latitude)*20714.99254;
    }

    private double longitude_to_relative_position_lab(LatLng latLng){
        return (latLng.longitude - MainActivity.convert_to_lab(LAB_ORIGIN).longitude)*16619.76984;
    }

    //change boolean flag to true, avoid null pointer exception in printing aircraft information
    protected void startFlight(){
        inFlight = true;
    }

    protected void endFlight(){
        inFlight = false;
    }

    protected void startActivity(){
        startTime = SystemClock.elapsedRealtime();
        activityStarted = true;
    }

    protected void endActivity(){
        activityStarted = false;
    }

    protected void recordTime(){
        totalTime = SystemClock.elapsedRealtime() - startTime;
    }

    protected void closeLogger(){
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.d("DroneLogging", "Failed to close the file writer.");
            e.printStackTrace();
        }
    }
}
