package com.agvahealthcare.ventilator_ext.connection.parser;

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by MOHIT MALHOTRA on 13-09-2018.
 */

public class RaspiParser extends ParserUtils {

    public static final String TYPE_INHALATION = "A";
    public static final String TYPE_END_OF_INHALATION = "B";
    public static final String TYPE_EXHALATION = "C";
    public static final String TYPE_END_OF_EXHALATION = "D";


    public static final String DATA_PRESSURE = "Pressure";
    public static final String DATA_PIP = "PIP";
    public static final String DATA_PMEAN = "Pmean";
    public static final String DATA_VTI = "VTi";
    public static final String DATA_VTE = "VTe";
    public static final String DATA_FLOW = "Flow";
    public static final String DATA_TRIGGER = "Trigger";
    public static final String DATA_MVI = "MVI";
    public static final String DATA_VOLUME = "Vol";
    public static final String DATA_VPEAK_I = "VPeakI";
    public static final String DATA_VPEAK_E = "VPeakE";
    public static final String DATA_TITOT = "Ti/ToT";
    public static final String DATA_PEEP = "PEEP";
    public static final String DATA_MVE = "MVe";
    public static final String DATA_RR = "RR";
    public static final String DATA_FIO2 = "FiO2";
    public static final String DATA_LEAK = "Leak";
    public static final String DATA_LEAK_FLOW = "Leak Flow";
    public static final String DATA_VOLUME_LEAK = "VLeak";
    public static final String DATA_TRIGFLOW = "TrigFlow";
    public static final String DATA_INSPIRE_TIME = "Inspiratory Time";
    public static final String DATA_EXPIRE_TIME = "Expiratory Time";
    public static final String DATA_PLATEAU_PRESSURE = "Plateau Pressure";
    public static final String DATA_MEAN_AIRWAY_PRESSURE = "Mean Airway Pressure";
    public static final String DATA_RISE_TIME = "Rise Time";


    private Map<String, Map<String, String>> dataMap;


    

    {
        dataMap = new LinkedHashMap<>();
        dataMap.put(TYPE_INHALATION, getConfigMap(DATA_PRESSURE, DATA_FLOW, DATA_VOLUME, DATA_TRIGGER));
        dataMap.put(TYPE_END_OF_INHALATION, getConfigMap(DATA_PIP, DATA_VTI, DATA_VPEAK_I, DATA_PMEAN, DATA_MVI, DATA_TRIGFLOW, DATA_INSPIRE_TIME, DATA_PLATEAU_PRESSURE, DATA_RISE_TIME));
        dataMap.put(TYPE_EXHALATION, getConfigMap(DATA_PRESSURE, DATA_FLOW, DATA_VOLUME, DATA_TITOT));
//        dataMap.put(TYPE_END_OF_EXHALATION, getConfigMap(DATA_PEEP, DATA_RR, DATA_FIO2, DATA_VPEAK_E));
        dataMap.put(TYPE_END_OF_EXHALATION, getConfigMap(DATA_PEEP, DATA_RR, DATA_FIO2, DATA_VPEAK_E, DATA_MVE, DATA_LEAK, DATA_MEAN_AIRWAY_PRESSURE, DATA_VTE, DATA_EXPIRE_TIME));
//        dataMap.put(TYPE_END_OF_EXHALATION, getConfigMap(DATA_PEEP, DATA_RR, DATA_FIO2, DATA_VPEAK_E, DATA_MVE, DATA_LEAK, DATA_LEAK_FLOW, DATA_VTE, DATA_EXPIRE_TIME));
    }

    public RaspiParser addExtension(Class<? extends ParserExtension> extClass){
        if(dataMap != null){
            try {
                ParserExtension ext = extClass.newInstance();
                dataMap.putAll(ext.getDataMap());
                Log.i("PARSE_EXT", "Extension added successfully");
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        return this;
    }


    public final Map<String, Map<String, String>> parser(String msg){

        if(!msg.contains("@")){
            Log.i("PARSER EXCEPTION", "Type not defined");
            return null;
        }

        if(!msg.contains(",")){
            Log.i("PARSER EXCEPTION", "No data present");
            return null;
        }

        if(!msg.contains("#")){
            Log.i("PARSER EXCEPTION", "Delimiter not found");
            return null;
        }

        String type = msg.substring(0, msg.indexOf("@"));
        String rawData = msg.substring(msg.indexOf("@") + 1, msg.indexOf("#"));

        String[] data = rawData.split(",");

        Map<String, String> selectedMap = dataMap.get(type);

        // NPE safety check return
        if(selectedMap == null) return null;

        Iterator<String> iterator = selectedMap.keySet().iterator();
        for(String datum : data){
            if(iterator.hasNext()){
                selectedMap.put(iterator.next(), datum);
            }
        }
        Map<String, Map<String, String>> retMap = new LinkedHashMap<>();
        retMap.put(type, selectedMap);

        return retMap;

    }




}

