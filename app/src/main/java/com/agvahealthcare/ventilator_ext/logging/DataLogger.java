package com.agvahealthcare.ventilator_ext.logging;

import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.DATA_LOG_THRESHOLD_INTERVAL;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.ERROR_LOG_THRESHOLD_INTERVAL;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.SIMILAR_ERROR_LOG_INTERVAL;

import android.content.Context;
import android.util.Log;

import com.agvahealthcare.ventilator_ext.database.DatabaseHelper;
import com.agvahealthcare.ventilator_ext.model.DataStoreModel;
import com.agvahealthcare.ventilator_ext.model.ErrorStoreModel;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by MOHIT MALHOTRA on 14-11-2018.
 */

public class DataLogger {

    private static long lastErrorLogTime; // in ms
    private static String lastErrorCode;

    private Context context;
    private DatabaseHelper database;

    public DataLogger(Context context) {
        this.context = context;
        this.database = new DatabaseHelper(context);
        lastErrorLogTime = System.currentTimeMillis();
    }

    public boolean addLog(DataStoreModel data){
        String timestamp = getCurrentTimeStamp();
        //data.setTime(timestamp);
        Log.i("DATA_LOGS", "Log registered at " + timestamp);
        return database.insert(data);
    }

    public ArrayList<DataStoreModel> getLogs(){
        return database.readLogs();
    }

    public ArrayList<DataStoreModel> getLogsInRange(Date startDate, Date endDate){
        return database.readLogs(startDate, endDate);
    }
    public long getTotalLogsCount(){
        return database.readLogsCount();
    }
    public int getNumberOFEntries(){
        return database.getNumberOfEntriesOfDataBase();
    }
    public ArrayList<DataStoreModel> getLogsInrangeByLimit(){
        // return database.getFurtherLogsInRange(limit,0,starDate,endDate);
        return database.getLogsInRangeByLimit();
    }
    public void deleteDataFromSqlite(){
        database.databaseUpdate();
    }
    public ArrayList<DataStoreModel> getLogsInRangeForPagination(int offset){
        return database.readLogs(offset);
    }
    public ArrayList<DataStoreModel> getNewLogs(int offset){
        return database.readNewLogs(offset);
    }
    public boolean addErrorLog(ErrorStoreModel err){
        String timestamp = getCurrentTimeStamp();
        long currentMillis = System.currentTimeMillis();

        if(lastErrorCode != null && lastErrorCode.equalsIgnoreCase(err.getErrorCode()) && (currentMillis - lastErrorLogTime < SIMILAR_ERROR_LOG_INTERVAL)) return false;


        err.setTime(timestamp);
        Log.i("DATA_LOGS", "Error Log registered at " + timestamp);
        lastErrorLogTime = currentMillis;   // update millis
        lastErrorCode = err.getErrorCode();

        return database.insertError(err);
    }

    public boolean isDataLogThresholdTimeReached(){
        long currentTimeInMillis = System.currentTimeMillis();
        long startTimeInMillis = database.readStartDateOfDataLogs();
        Log.i("THRESHOLD CHECK", "start time of DATA log = " + startTimeInMillis);

        return ((startTimeInMillis >0) && (currentTimeInMillis - startTimeInMillis > DATA_LOG_THRESHOLD_INTERVAL));
    }

    public boolean isErrorLogThresholdTimeReached(){
        long currentTimeInMillis = System.currentTimeMillis();
        long startTimeInMillis = database.readStartDateOfErrorLogs();
        Log.i("THRESHOLD CHECK", "start time of ERROR log = " + startTimeInMillis);


        return ((startTimeInMillis >0) && (currentTimeInMillis - startTimeInMillis > ERROR_LOG_THRESHOLD_INTERVAL));
    }

    public boolean clearDataLogs(){
        return database.deleteDataLogs();
    }

    public boolean clearErrorLogs(){
        return database.deleteErrorLogs();
    }

    public long getDataLogsStartTime() {
        return database.readStartDateOfDataLogs();
    }

    public long getErrorLogsStartTime() {
        return database.readStartDateOfErrorLogs();
    }

    public ArrayList<ErrorStoreModel> getErrorLogs(){
        return database.readError();
    }

    private String getCurrentTimeStamp() {
        return new Date().toString();
    }
}
