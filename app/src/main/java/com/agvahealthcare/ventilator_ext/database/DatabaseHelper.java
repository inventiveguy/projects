package com.agvahealthcare.ventilator_ext.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.agvahealthcare.ventilator_ext.model.DataStoreModel;
import com.agvahealthcare.ventilator_ext.model.ErrorStoreModel;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by MOHIT MALHOTRA on 14-11-2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper implements DatabaseUtils {

    final static long MAX_RECORDS_LIMIT = 50000;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TBL_DATA_STORE);
        db.execSQL(CREATE_TBL_ERROR_STORE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion && newVersion == 3){
            shrink(db);
        }
    }

    private void shrink() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            shrink(db);
        } finally {
            if(db != null) db.close();
        }

    }
    private void shrink(SQLiteDatabase db){
        final long dbSize = readLogsCount(db);

        if(dbSize > MAX_RECORDS_LIMIT) {

            final String shrinkQuery = "DELETE FROM " + TABLE_VENT_DATA_STORE
                    + " WHERE " + DataStoreTable.ID
                    + " IN (SELECT " + DataStoreTable.ID + " FROM " + TABLE_VENT_DATA_STORE + " ORDER BY " + DataStoreTable.ID + " ASC LIMIT " + (dbSize - MAX_RECORDS_LIMIT + ")");

            Log.i("QUERY_SHRINK", shrinkQuery);

            db.execSQL(shrinkQuery);


            Log.i("DB_ACTION", "Shrinked the database");
        }
    }

    public long readStartDateOfErrorLogs(){
        SQLiteDatabase db = this.getReadableDatabase();
        long startTimeInMills = 0;

        Cursor cursor = db.rawQuery("SELECT MIN(" + ErrorStoreTable.TIME + ") FROM "+TABLE_VENT_ERROR_STORE, null);

        if(cursor != null && cursor.moveToFirst()){
            try {
                startTimeInMills = Long.parseLong(cursor.getString(cursor.getColumnIndex(ErrorStoreTable.TIME)));
            } catch (Exception e){
                e.printStackTrace();
                Log.i("DatabaseHelper CHECK", "Unable to parse startdate");
            } finally {
                cursor.close();
                db.close();
            }
        }

        return startTimeInMills;
    }

    public long readStartDateOfDataLogs(){
        SQLiteDatabase db = this.getReadableDatabase();
        long startTimeInMills = 0;

        Cursor cursor = db.rawQuery("SELECT MIN(" + DataStoreTable.TIME + ") FROM "+TABLE_VENT_DATA_STORE,null);

        if(cursor != null && cursor.moveToFirst()){
            try {
                startTimeInMills = Long.parseLong(cursor.getString(cursor.getColumnIndex(DataStoreTable.TIME)));
            } catch (Exception e){
                e.printStackTrace();
                Log.i("DatabaseHelper CHECK", "Unable to parse startdate");
            }  finally {
                cursor.close();
                db.close();
            }
        }

        return startTimeInMills;
    }

    public long readLogsCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        long size = readLogsCount(db);
        db.close();

        return size;
    }

    public long readLogsCount(SQLiteDatabase db){
        long count = 0;
        try {
            count = android.database.DatabaseUtils.queryNumEntries(db, TABLE_VENT_DATA_STORE);
        } catch (Exception e){
            e.printStackTrace();
            Log.i("DatabaseHelper CHECK", "Unable to parse startdate");
        }

        return count;
    }

    public boolean insert(DataStoreModel data){
         shrink();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataStoreTable.PIP, data.getPressure());
        values.put(DataStoreTable.VINSP, data.getVolume());
        values.put(DataStoreTable.RR, data.getRr());
        values.put(DataStoreTable.FIO2, data.getFiO2());
        values.put(DataStoreTable.MVE, data.getMve());
        values.put(DataStoreTable.VTE, data.getVte());
        values.put(DataStoreTable.PEEP, data.getPeep());
        values.put(DataStoreTable.LEAK, data.getLeak());
        values.put(DataStoreTable.IE_RATIO, data.getIeRatio());
        values.put(DataStoreTable.TIME, data.getTime());

        values.put(DataStoreTable.TINSP, data.getTinsp());
        values.put(DataStoreTable.TEXP, data.getTexp());
        values.put(DataStoreTable.MVI, data.getMvi());
        values.put(DataStoreTable.TRIGGER, data.getTrigger());
        values.put(DataStoreTable.MEANAIRWAYPRESS, data.getMeanAirwayPressure());
        boolean ack = db.insert(TABLE_VENT_DATA_STORE, null, values) != -1;
        db.close();

        return ack;

    }

    public ErrorStoreModel readLastErrorRecord(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VENT_ERROR_STORE, ErrorStoreTable.COLUMNS, null, null, null, null, null, "1" );

        ErrorStoreModel errorData = null;

        if(cursor != null && cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndex(ErrorStoreTable.ID));
            String errorCode = cursor.getString(cursor.getColumnIndex(ErrorStoreTable.ERROR_CODE));
            String time = cursor.getString(cursor.getColumnIndex(ErrorStoreTable.TIME));

            errorData = new ErrorStoreModel(id);
            errorData.setErrorCode(errorCode);
            errorData.setTime(time);


            cursor.close();
        }

        db.close();

        return errorData;

    }
    //ToDo:- the major iteration that is f(t) value
    public ArrayList<DataStoreModel> readLogs(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<DataStoreModel> dataList = new ArrayList<>();
        String query="SELECT * FROM " + TABLE_VENT_DATA_STORE +" LIMIT 9";

        // Cursor cursor = db.query(TABLE_VENT_DATA_STORE, DataStoreTable.COLUMNS, null, null, null, null, null);
        Cursor cursor=db.rawQuery(query,null);
        Log.i("LOG_CURSOR_COUNT", cursor.getCount() + "");
        if(cursor != null && cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex(DataStoreTable.ID));
                float pip = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.PIP));
                float vinsp = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.VINSP));
                float rr = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.RR));
                float fio2 = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.FIO2));
                float mve = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.MVE));
                float vte = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.VTE));
                float peep = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.PEEP));
                float leak = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.LEAK));
                float ieRatio = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.IE_RATIO));
                String time = cursor.getString(cursor.getColumnIndex(DataStoreTable.TIME));

                DataStoreModel data = new DataStoreModel(id);
                data.setPressure(pip);
                data.setVolume(vinsp);
                data.setRR(rr);
                data.setFiO2(fio2);
                data.setMve(mve);
                data.setVte(vte);
                data.setPeep(peep);
                data.setLeak(leak);
                data.setIeRatio(ieRatio);
                data.setTime(time);

                dataList.add(data);

            }while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return dataList;
    }
    public ArrayList<DataStoreModel> readNewLogs(int offset){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataStoreModel> dataList=new ArrayList<>();
        //String query = "SELECT * FROM " + TABLE_VENT_DATA_STORE + " LIMIT " + offset+","+limit;
        String query="SELECT * FROM " + TABLE_VENT_DATA_STORE +" limit 20"+" offset "+offset*20;
        //String query="SELECT * FROM " + TABLE_VENT_DATA_STORE+ " limit 40";

        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null && cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex(DataStoreTable.ID));
                float pip = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.PIP));
                float vinsp = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.VINSP));
                float rr = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.RR));
                float fio2 = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.FIO2));
                float mve = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.MVE));
                float vte = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.VTE));
                float peep = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.PEEP));
                float leak = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.LEAK));
                float ieRatio = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.IE_RATIO));
                String time = cursor.getString(cursor.getColumnIndex(DataStoreTable.TIME));
                float tinsp = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.TINSP));
                float texp = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.TEXP));
                float mvi = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.MVI));
                float trigger = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.TRIGGER));
                float meanAirwayPressure = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.MEANAIRWAYPRESS));

                DataStoreModel data = new DataStoreModel(id);
                data.setId(id);
                data.setPressure(pip);
                data.setVolume(vinsp);
                data.setRR(rr);
                data.setFiO2(fio2);
                data.setMve(mve);
                data.setVte(vte);
                data.setPeep(peep);
                data.setLeak(leak);
                data.setIeRatio(ieRatio);
                data.setTime(time);
                data.setTexp(texp);
                data.setMvi(mvi);
                data.setTrigger(trigger);
                data.setMeanAirwayPressure(meanAirwayPressure);
                data.setTinsp(tinsp);

                dataList.add(data);
            }while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return dataList;
    }
    public void databaseUpdate(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "DELETE FROM "+TABLE_VENT_DATA_STORE +" WHERE log_time <= date('now','-7 day')";
        db.execSQL(sql);
        db.close();
    }
    public ArrayList<DataStoreModel> readLogs(int offset){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataStoreModel> dataList=new ArrayList<>();
        String query="SELECT * FROM " + TABLE_VENT_DATA_STORE +" limit 10"+" offset "+offset*10;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null && cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex(DataStoreTable.ID));
                float pip = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.PIP));
                float vinsp = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.VINSP));
                float rr = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.RR));
                float fio2 = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.FIO2));
                float mve = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.MVE));
                float vte = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.VTE));
                float peep = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.PEEP));
                float leak = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.LEAK));
                float ieRatio = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.IE_RATIO));
                String time = cursor.getString(cursor.getColumnIndex(DataStoreTable.TIME));

                DataStoreModel data = new DataStoreModel(id);
                data.setPressure(pip);
                data.setVolume(vinsp);
                data.setRR(rr);
                data.setFiO2(fio2);
                data.setMve(mve);
                data.setVte(vte);
                data.setPeep(peep);
                data.setLeak(leak);
                data.setIeRatio(ieRatio);
                data.setTime(time);

                dataList.add(data);
            }while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return dataList;
    }

    public ArrayList<DataStoreModel> readLogs(Date startDate, Date endDate){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataStoreModel> dataList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_VENT_DATA_STORE, DataStoreTable.COLUMNS, null, null, null, null, null,String.valueOf(9));

        if(cursor != null && cursor.moveToFirst()){
            do{

                int id = cursor.getInt(cursor.getColumnIndex(DataStoreTable.ID));
                float pip = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.PIP));
                float vinsp = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.VINSP));
                float rr = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.RR));
                float fio2 = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.FIO2));
                float mve = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.MVE));
                float vte = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.VTE));
                float peep = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.PEEP));
                float leak = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.LEAK));
                float ieRatio = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.IE_RATIO));
                String time = cursor.getString(cursor.getColumnIndex(DataStoreTable.TIME));

                DataStoreModel data = new DataStoreModel(id);
                data.setPressure(pip);
                data.setVolume(vinsp);
                data.setRR(rr);
                data.setFiO2(fio2);
                data.setMve(mve);
                data.setVte(vte);
                data.setPeep(peep);
                data.setLeak(leak);
                data.setIeRatio(ieRatio);
                data.setTime(time);

                dataList.add(data);
            }while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return dataList;
    }



    public boolean insertError(ErrorStoreModel err){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ErrorStoreTable.ERROR_CODE, err.getErrorCode());
        values.put(ErrorStoreTable.TIME, err.getTime());

        boolean ack = db.insert(TABLE_VENT_ERROR_STORE, null, values) != -1;
        db.close();

        return ack;

    }

    public ArrayList<ErrorStoreModel> readError(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ErrorStoreModel> errorList =new ArrayList<>();

        Cursor cursor = db.query(TABLE_VENT_ERROR_STORE, ErrorStoreTable.COLUMNS, null, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){


            do{
                int id = cursor.getInt(cursor.getColumnIndex(ErrorStoreTable.ID));
                String errorCode = cursor.getString(cursor.getColumnIndex(ErrorStoreTable.ERROR_CODE));
                String time = cursor.getString(cursor.getColumnIndex(ErrorStoreTable.TIME));

                ErrorStoreModel data = new ErrorStoreModel(id);
                data.setErrorCode(errorCode);
                data.setTime(time);

                errorList.add(data);

            }while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return errorList;
    }


    public int getNumberOfEntriesOfDataBase(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id FROM tbl_data_store", null).getCount();
    }
    public ArrayList<DataStoreModel> getLogsInRangeByLimit(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataStoreModel> dataList = new ArrayList<>();
        String query="SELECT * FROM " + TABLE_VENT_DATA_STORE +" LIMIT 10";
        //String query="SELECT * FROM " + TABLE_VENT_DATA_STORE +" limit 9"+" offset 9";
        //+"offset 9"
        Cursor cursor=db.rawQuery(query,null);
        if(cursor != null && cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex(DataStoreTable.ID));
                float pip = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.PIP));
                float vinsp = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.VINSP));
                float rr = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.RR));
                float fio2 = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.FIO2));
                float mve = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.MVE));
                float vte = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.VTE));
                float peep = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.PEEP));
                float leak = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.LEAK));
                float ieRatio = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.IE_RATIO));
                String time = cursor.getString(cursor.getColumnIndex(DataStoreTable.TIME));
                float tinsp = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.TINSP));
                float texp = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.TEXP));
                float mvi = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.MVI));
                float trigger = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.TRIGGER));
                float meanAirwayPressure = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.MEANAIRWAYPRESS));

                DataStoreModel data = new DataStoreModel(id);
                data.setPressure(pip);
                data.setVolume(vinsp);
                data.setRR(rr);
                data.setFiO2(fio2);
                data.setMve(mve);
                data.setVte(vte);
                data.setPeep(peep);
                data.setLeak(leak);
                data.setIeRatio(ieRatio);
                data.setTime(time);
                data.setTinsp(tinsp);
                data.setTexp(texp);
                data.setMvi(mvi);
                data.setTrigger(trigger);
                data.setMeanAirwayPressure(meanAirwayPressure);
                dataList.add(data);
            }while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return dataList;
    }

    public DataStoreModel readByTime(String logTime){
        SQLiteDatabase db = this.getReadableDatabase();
        DataStoreModel data = null;
        Cursor cursor = db.query(TABLE_VENT_DATA_STORE, DataStoreTable.COLUMNS, "where " + DataStoreTable.TIME + "=?", new String[]{ logTime }, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {

            int id = cursor.getInt(cursor.getColumnIndex(DataStoreTable.ID));
            float pip = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.PIP));
            float vinsp = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.VINSP));
            float rr = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.RR));
            float fio2 = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.FIO2));
            float mve = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.MVE));
            float vte = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.VTE));
            float peep = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.PEEP));
            float leak = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.LEAK));
            float ieRatio = cursor.getFloat(cursor.getColumnIndex(DataStoreTable.IE_RATIO));
            String time = cursor.getString(cursor.getColumnIndex(DataStoreTable.TIME));

            data = new DataStoreModel(id);
            data.setPressure(pip);
            data.setVolume(vinsp);
            data.setRR(rr);
            data.setFiO2(fio2);
            data.setMve(mve);
            data.setVte(vte);
            data.setPeep(peep);
            data.setLeak(leak);
            data.setIeRatio(ieRatio);
            data.setTime(time);




            cursor.close();

        }

        db.close();
        return data;
    }


    public boolean deleteDataLogs(){
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_VENT_DATA_STORE, "1", null);
        db.close();

        return rowsDeleted > 0;
    }


    public boolean deleteErrorLogs(){
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_VENT_ERROR_STORE, "1", null);
        db.close();

        return rowsDeleted > 0;
    }

}
