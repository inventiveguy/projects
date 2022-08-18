package com.agvahealthcare.ventilator_ext.manager;


import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.GRAPH_POINTS_MAX;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.Gender;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.Gender.TYPE_MALE;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_APNEA_RR;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_APNEA_TRIG_FLOW;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_APNEA_VT;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_FIO2;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_PEAK_FLOW;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_PEEP;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_PIP;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_PPLAT;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_RR;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_SLOPE;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_SUPPORT_PRESSURE;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_TAPNEA;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_TEXP;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_TINSP;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_TLOW;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_TRIG_FLOW;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.LBL_VTI;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.MODE_NIV;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.PatientProfile;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.PatientProfile.TYPE_ADULT;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.SENSOR_CALIBRATION_SUCCESS;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.SENSOR_MISSING;
import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.getModeCategory;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.StringRes;

import com.agvahealthcare.ventilator_ext.R;
import com.agvahealthcare.ventilator_ext.model.SensorCalibration;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


/**
 * Created by MOHIT MALHOTRA on 28-09-2018.
 */

public class PreferenceManager {

    private static final String PREF_CONTROL_PARAMS = "pref_control_params";

    private static final String LIMIT_SEPARATOR = ",";
    private static final String PREF_OXYGEN_LEVEL = "pref_oxygen_level";

    private static final String CONFIGURATION_SHARED_PREFERENCES = "ventilator.settings";

    private static final String BLE_SERVICE_STATUS = "service_status.ble";

    private static final String PREF_CURRENT_UID = "pref_userid";
    private static final String PREF_VENTILATOR_SOFTWARE_VERSION = "pref_vent_sw_version";

    private static final String PREF_GRAPH_PRESSURE = "pref_graph_pressure";
    private static final String PREF_GRAPH_VOLUME = "pref_graph_volume";
    private static final String PREF_GRAPH_FLOW = "pref_graph_flow";
    private static final String PREF_GRAPH_PRESSURE_FLOW = "pref_graph_pressure_flow";

    private static final String PREF_SUPPORT_INSP_PRESSURE = "pref_support_pinsp";
    private static final String PREF_SUPPORT_EXP_PRESSURE = "pref_support_pexp";
    private static final String PREF_SUPPORT_PRESSURE = "pref_support_pressure";
    private static final String PREF_SLOPE = "pref_slope";
    private static final String PREF_TEXP = "pref_texp";

    private static final String PREF_PIP = "pref_pip";
    private static final String PREF_PEAK_FLOW = "pref_peak_flow";
    private static final String PREF_PEEP = "pref_peep";
    private static final String PREF_RR = "pref_rr";
    private static final String PREF_TIDAL_VOLUME = "pref_vti";
    private static final String PREF_TRIG_FLOW = "pref_trig_flow";
    private static final String PREF_TLOW = "pref_tlow";
    private static final String PREF_PLATEAU_PRESSURE = "pref_pplat";
    private static final String PREF_INSP_TIME = "pref_tisnp";
    private static final String PREF_FIO2 = "pref_fio2";
    private static final String PREF_IS_DEEP_SLEEP = "pref_deep_sleep_state";
    private static final String PREF_PATIENT_PROFILE = "pref_patient_profile";
    private static final String PREF_PBOUND_BLE_MAC = "pref_bound_ble_address";
    private static final String PREF_PBOUND_BLE_NAME = "pref_bound_ble_identifier";

    // APNEA PARAMETERS
    private static final String PREF_APNEA_SETTINGS_STATUS = "pref_apnea_status";
    private static final String PREF_APNEA_RR = "pref_apnea_rr";
    private static final String PREF_APNEA_TIME = "pref_apnea_time";
    private static final String PREF_APNEA_TIDAL_VOLUME = "pref_apnea_vti";
    private static final String PREF_APNEA_TRIG_FLOW = "pref_apnea_trig_flow";


    private static final String PREF_BODY_WEIGHT = "pref_body_weight";
    private static final String PREF_NEO_BODY_WEIGHT = "pref_Neo_body_weight";
    private static final String PREF_PED_BODY_WEIGHT = "pref_Ped_body_weight";
    private static final String PREF_AGE = "pref_age";
    private static final String PREF_NEO_AGE = "pref_Neo_age";
    private static final String PREF_PED_AGE = "pref_Ped_age";

    private static final String PREF_BODY_HEIGHT = "pref_body_height";

    private static final String PREF_NEO_BODY_HEIGHT = "pref_Neo_body_height";

    private static final String PREF_PED_BODY_HEIGHT = "pref_Ped_body_height";

    private static final String PREF_IS_LOGGED_IN = "pref_login";
    private static final String PREF_GENDER = "pref_gender";


    private static final String PREF_STANDBY_STATUS = "pref_standby_status";
    private static final String PREF_SHUTDOWN_STATUS = "pref_shutdown_Status";
    private static final String PREF_VENTILATION_MODE = "pref_ventilation_mode";
    private static final String PREF_EMERGENCY_CONTACT = "pref_emergency_contact";
    private static final String PREF_GRAPH_AUTOSCALE = "pref_graph_autoscaling";
    private static final String PREF_LEAK_COMPENSATE = "pref_leak_compensate";
    private static final String PREF_TUBE_BLOCKAGE_ALARM = "pref_tube_blockage_alarm";
    private static final String PREF_CUFF_LEAKAGE_ALARM = "pref_cuff_leakage_alarm";
    private static final String PREF_ALARM_SUGGESTION = "pref_alarm_suggestion";
    private static final String PREF_LEAK_BASED_DISCONNECT = "pref_leak_based_disconnect";
    private static final String PREF_VOLUME_LEVEL = "pref_volume_level";
    private static final String PREF_GRAPH_POINTS = "pref_graph_points";
    private static final String PREF_IS_PEDIATRIC_ACTIVE = "pref_is_pediatric_active";
    private static final String PREF_IS_OXYGEN_HOLD_ACTIVE = "pref_is_oxygen_hold_active";
    private static final String PREF_IS_EXPANDED_ALARM_VISIBLE = "pref_expand_alarm_visibility";
    private static final String PREF_SCREEN_LOCK = "pref_screen_lock";
    private static final String PREF_VOLUME = "pref_volume";


    // MANEUVERS LIMITS
    private static final String PREF_MANEUVERS_MIN_MAX = "pref_manuvers_limits";
    private static final String PREF_MANEUVERS_PEEP = "pref_manuvers_peep";
    private static final String PREF_MANEUVERS_PLATEAU = "pref_manuvers_plateau";
    private static final String PREF_MANEUVERS_STATIC_COMPLINES = "pref_manuvers_static_complines";
    private static final String PREF_EXPIRATORY_DATE = "pref_expiratory_date";
    private static final String PREF_INSPIRATORY_DATE = "pref_inspiratory_date";
    private static final String PREF_MEASURE_TIME = "pref_measure_time";


    // Test & Calibration
    private static final String PREF_TURBINE_CALIBRATION = "pref_turbine_calibration";
    private static final String PREF_FLOW_SENSOR_CALIBRATION = "pref_flow_sensor_calibration";
    private static final String PREF_OXYGEN_CALIBRATION = "pref_oxygen_calibration";
    private static final String PREF_PRESSER_CALIBRATION = "pref_presser_calibration";


    // VENTILATOR PARAM LIMITS
    private static final String PREF_PIP_MIN_MAX = "pref_pip_limits";
    private static final String PREF_VTI_MIN_MAX = "pref_vti_limits";
    private static final String PREF_VTE_MIN_MAX = "pref_vte_limits";
    private static final String PREF_PEEP_MIN_MAX = "pref_peep_limits";
    private static final String PREF_RR_MIN_MAX = "pref_rr_limits";
    private static final String PREF_MVI_MIN_MAX = "pref_mvi_limits";
    private static final String PREF_MVE_MIN_MAX = "pref_mve_limits";
    private static final String PREF_FIO2_MIN_MAX = "pref_fio2_limits";
    private static final String PREF_SPO2_MIN_MAX = "pref_spo2_limits";
    private static final String PREF_TITOT_MIN_MAX = "pref_titot_limits";
    private static final String PREF_LEAK_MIN_MAX = "pref_leak_limits";


    // VENTILATOR ALARM STATESS
    private static final String PREF_PIP_ALARM_STATE = PREF_PIP_MIN_MAX + "_state";
    private static final String PREF_VTI_ALARM_STATE = PREF_VTI_MIN_MAX + "_state";
    private static final String PREF_VTE_ALARM_STATE = PREF_VTE_MIN_MAX + "_state";
    private static final String PREF_PEEP_ALARM_STATE = PREF_PEEP_MIN_MAX + "_state";
    private static final String PREF_RR_ALARM_STATE = PREF_RR_MIN_MAX + "_state";
    private static final String PREF_MVI_ALARM_STATE = PREF_MVI_MIN_MAX + "_state";
    private static final String PREF_MVE_ALARM_STATE = PREF_MVE_MIN_MAX + "_state";
    private static final String PREF_FIO2_ALARM_STATE = PREF_FIO2_MIN_MAX + "_state";
    private static final String PREF_SPO2_ALARM_STATE = PREF_SPO2_MIN_MAX + "_state";
    private static final String PREF_TITOT_ALARM_STATE = PREF_TITOT_MIN_MAX + "_state";
    private static final String PREF_LEAK_ALARM_STATE = PREF_LEAK_MIN_MAX + "_state";

    // LIMITING ALARMS
    private static final String PREF_O2_LIMIT = "pref_o2_limit";
    //log start time and end time
    private static final String PREF_LOG_START_DATETIME = "pref_logstarttime";
    private static final String PREF_LOG_END_DATETIME = "pref_logendtime";
    private static final String PREF_IS_LOGTIME_SAVED = "pref_isTimeSaved";

    //sensor Analysis
    private static final String PREF_SENSOR_LOW_PRESSURE_O2 = "pref_sens_low_o2";
    private static final String PREF_SENSOR_HIGH_PRESSURE_O2 = "pref_sens_high_o2";
    private static final String PREF_SENSOR_CO2 = "pref_sens_co2";
    private static final String PREF_SENSOR_SPO2 = "pref_sens_spo2";
    private static final String PREF_SENSOR_TEMP = "pref_sens_temp";
    private static final String PREF_SENSOR_INAHLE_FLOW = "pref_sens_insp_flow";
    private static final String PREF_SENSOR_EXHALE_FLOW = "pref_sens_exp_flow";


    private Context context;
    private SharedPreferences sp;
    private Gson gson;

    public PreferenceManager(Context context) {
        this.context = context;
        this.sp = context.getSharedPreferences(CONFIGURATION_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }


    //================= Log StartEnd DateTime ==============
    public void setLogStartTime(String startTime) {
        updateData(PREF_LOG_START_DATETIME, startTime);
    }

    public String readStartTime() {
        return sp.getString(PREF_LOG_START_DATETIME, null);
    }

    public void setLogEndTime(String endTime) {
        updateData(PREF_LOG_END_DATETIME, endTime);
    }

    public String readEndTime() {
        return sp.getString(PREF_LOG_END_DATETIME, null);
    }

    public void setIsLogTimeSaved(boolean isTimeSaved) {
        updateData(PREF_IS_LOGTIME_SAVED, isTimeSaved);
    }

    public Boolean readIsLogTimeSaved() {
        return sp.getBoolean(PREF_IS_LOGTIME_SAVED, false);
    }

    public void clearLogsFilterValues() {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(PREF_LOG_START_DATETIME);
        editor.remove(PREF_LOG_END_DATETIME);
        editor.remove(PREF_IS_LOGTIME_SAVED);
        editor.apply();
    }
    //==========================================================

    public BluetoothDevice readBoundDevice() {
        return gson.fromJson(sp.getString(PREF_PBOUND_BLE_MAC, null), BluetoothDevice.class);
    }

    public String readBoundDeviceName() {
        return sp.getString(PREF_PBOUND_BLE_NAME, null);
    }

    public void setBoundDevice(BluetoothDevice device, String name) {
        updateData(PREF_PBOUND_BLE_MAC, gson.toJson(device));
        updateData(PREF_PBOUND_BLE_NAME, name);
    }


    public boolean readExpandedAlarmVisility() {
        return sp.getBoolean(PREF_IS_EXPANDED_ALARM_VISIBLE, false);
    }

    public void setExpandedAlarmVisility(boolean status) {
        updateData(PREF_IS_EXPANDED_ALARM_VISIBLE, status);
    }

    public boolean readPressureGraphvisility() {
        return sp.getBoolean(readCurrentUid() + "." + PREF_GRAPH_PRESSURE, true);
    }

    public void setPressureGraphvisility(boolean status) {
        updateData(readCurrentUid() + "." + PREF_GRAPH_PRESSURE, status);
    }

    public boolean readVolumeGraphvisility() {
        return sp.getBoolean(readCurrentUid() + "." + PREF_GRAPH_VOLUME, false);
    }

    public void setVolumeGraphvisility(boolean status) {
        updateData(readCurrentUid() + "." + PREF_GRAPH_VOLUME, status);
    }

    public boolean readFlowGraphvisility() {
        return sp.getBoolean(readCurrentUid() + "." + PREF_GRAPH_FLOW, false);
    }

    public void setFlowGraphvisility(boolean status) {
        updateData(readCurrentUid() + "." + PREF_GRAPH_FLOW, status);
    }


    public boolean readPresureFlowGraphvisility() {
        return sp.getBoolean(readCurrentUid() + "." + PREF_GRAPH_PRESSURE_FLOW, true);
    }

    public void setPresureFlowGraphvisility(boolean status) {
        updateData(readCurrentUid() + "." + PREF_GRAPH_PRESSURE_FLOW, status);
    }


    public boolean readServiceStatus() {
        return sp.getBoolean(BLE_SERVICE_STATUS, false);
    }

    public void setServiceStatus(boolean status) {
        updateData(BLE_SERVICE_STATUS, status);
    }

    public boolean readOxygenLevelStatus() {
        return sp.getBoolean(PREF_OXYGEN_LEVEL, false);
//        return (readFiO2().intValue() > Configs.THRESHOLD_OXYGEN_VARIATION_VALUE);
    }

    private void setOxygenLevelStatus(boolean isHigh) {
        updateData(PREF_OXYGEN_LEVEL, isHigh);
    }

    public void setCurrentUid(PatientProfile uid) {
        updateData(PREF_CURRENT_UID, uid.toString());
    }

    public PatientProfile readCurrentUid() {
        try {
            return PatientProfile.valueOf(sp.getString(PREF_CURRENT_UID, TYPE_ADULT.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return TYPE_ADULT;
        }
    }

    public String readVentilatorSoftwareVersion() {
        return sp.getString(PREF_VENTILATOR_SOFTWARE_VERSION, "");
    }

    public void setVentilatorSoftwareVersion(String version) {
        updateData(PREF_VENTILATOR_SOFTWARE_VERSION, version);
    }

    public int readVentilationMode() {

        return sp.getInt(readCurrentUid() + "." + PREF_VENTILATION_MODE, -1);
    }

    public void setVentilationMode(int val) {
        updateData(readCurrentUid() + "." + PREF_VENTILATION_MODE, val);
    }

    public void setControlParams(String dataList) {
        updateData(PREF_CONTROL_PARAMS,dataList);
    }

    public String readControlParams(){
        return sp.getString(PREF_CONTROL_PARAMS,"default value");
    }

    public Float readSupportPinsp() {
        return sp.getFloat(readCurrentUid() + "." + PREF_SUPPORT_INSP_PRESSURE, Float.parseFloat(context.getString(R.string.default_ipap)));
    }

    public void setSupportPinsp(Float val) {
        updateData(readCurrentUid() + "." + PREF_SUPPORT_INSP_PRESSURE, val);
    }

    public Float readSupportPexp() {
        return sp.getFloat(readCurrentUid() + "." + PREF_SUPPORT_EXP_PRESSURE, Float.parseFloat(context.getString(R.string.default_epap)));
    }

    public void setSupportPexp(Float val) {
        updateData(readCurrentUid() + "." + PREF_SUPPORT_EXP_PRESSURE, val);
    }

   /* public int readFlowLimits() {
        int min;
        switch (readCurrentUid()) {
            case TYPE_ADULT:
                min = 20;
                break;
            case TYPE_PED:
                min = 40;
                break;
            case TYPE_NEONAT:
                min = 20;
                break;
            default :
                min = 20;
        }
        return sp.getInt(readCurrentUid() + "." + min);
    }
*/
    public Float readBodyWeight() {
        @StringRes int defaultvalue;
        switch (readCurrentUid()) {
            case TYPE_ADULT:
                defaultvalue = R.string.default_adult_body_weight;
                break;

            case TYPE_PED:
                defaultvalue = R.string.default_ped_body_weight;
                break;

            case TYPE_NEONAT:
                defaultvalue = R.string.default_neo_body_weight;
                break;

            default:
                defaultvalue = R.string.default_adult_body_weight;
                break;
        }
        return sp.getFloat(readCurrentUid() + "." + PREF_BODY_WEIGHT, Float.parseFloat(context.getString(defaultvalue)));
    }

    public void setBodyWeight(Float val) {
        updateData(readCurrentUid() + "." + PREF_BODY_WEIGHT, val);
    }

    public Float readBodyHeight() {
        @StringRes int defaultvalue;
        switch (readCurrentUid()) {
            case TYPE_ADULT:
                defaultvalue = R.string.default_adult_body_height;
                break;

            case TYPE_PED:
                defaultvalue = R.string.default_ped_body_height;
                break;

            case TYPE_NEONAT:
                defaultvalue = R.string.default_neo_body_height;
                break;

            default:
                defaultvalue = R.string.default_adult_body_height;
                break;
        }
        return sp.getFloat(readCurrentUid() + "." + PREF_BODY_HEIGHT, Float.parseFloat(context.getString(defaultvalue)));
    }

    public void setBodyHeight(Float val) {
        updateData(readCurrentUid() + "." + PREF_BODY_HEIGHT, val);
    }

    public Float readAge() {
        @StringRes int defaultvalue;
        switch (readCurrentUid()) {
            case TYPE_ADULT:
                defaultvalue = R.string.default_adult_age;
                break;

            case TYPE_PED:
                defaultvalue = R.string.default_ped_age;
                break;

            case TYPE_NEONAT:
                defaultvalue = R.string.default_neo_age;
                break;

            default:
                defaultvalue = R.string.default_adult_age;
                break;
        }
        return sp.getFloat(readCurrentUid() + "." + PREF_AGE, Float.parseFloat(context.getString(defaultvalue)));
    }

    public void setAge(Float val) {
        updateData(readCurrentUid() + "." + PREF_AGE, val);
    }


    public void setVolume(Float val) {
        updateData(PREF_VOLUME, val);
    }

    public Float readVolume() {
        return sp.getFloat(PREF_VOLUME, Float.parseFloat(context.getString(R.string.default_volume)));
    }


    public Gender readGender() {
        try {
            return Gender.valueOf(sp.getString(readCurrentUid() + "." + PREF_GENDER, TYPE_MALE.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return TYPE_MALE;
        }
    }

    public void setGender(Gender gender) {
        updateData(readCurrentUid() + "." + PREF_GENDER, gender.toString());
    }


    public Float readPip() {
        return sp.getFloat(readCurrentUid() + "." + PREF_PIP, Float.parseFloat(context.getString(R.string.default_pip)));
    }

    public void setPip(Float val) {
        updateData(readCurrentUid() + "." + PREF_PIP, val);
        setPipLimits(readPipLimits()[0], val);   // set the upper limit of the alarm
    }



    public Float readPeakFlow() {
        return sp.getFloat(readCurrentUid() + "." + PREF_PEAK_FLOW, Float.parseFloat(context.getString(R.string.default_peakflow)));
    }

    public void setPeakFlow(Float val) {
        updateData(readCurrentUid() + "." + PREF_PEAK_FLOW, val);
    }

    public Float readPEEP() {
        return sp.getFloat(readCurrentUid() + "." + PREF_PEEP, Float.parseFloat(context.getString(R.string.default_peep)));
    }

    public void setPEEP(Float val) {
        updateData(readCurrentUid() + "." + PREF_PEEP, val);
    }

    public Float readRR() {
        return sp.getFloat(readCurrentUid() + "." + PREF_RR, Float.parseFloat(context.getString(R.string.default_rr)));
    }

    public void setRR(Float val) {
        updateData(readCurrentUid() + "." + PREF_RR, val);
    }

    public Float readVti() {
        int resVti = readPediatricStatus() ? R.string.default_vti_ped : R.string.default_vti;
        return sp.getFloat(readCurrentUid() + "." + PREF_TIDAL_VOLUME, Float.parseFloat(context.getString(resVti)));
    }

    public void setVti(Float val) {
        updateData(readCurrentUid() + "." + PREF_TIDAL_VOLUME, val);
    }

    public Float readTrigFlow() {
        return sp.getFloat(readCurrentUid() + "." + PREF_TRIG_FLOW, Float.parseFloat(context.getString(R.string.default_trigflow)));
    }

    public void setTrigFlow(Float val) {
        updateData(readCurrentUid() + "." + PREF_TRIG_FLOW, val);
    }

    public Float readTlow() {
        return sp.getFloat(readCurrentUid() + "." + PREF_TLOW, Float.parseFloat(context.getString(R.string.default_tlow)));
    }

    public void setTlow(Float val) {
        updateData(readCurrentUid() + "." + PREF_TLOW, val);
    }

    public Float readTexp() {
        return sp.getFloat(readCurrentUid() + "." + PREF_TEXP, Float.parseFloat(context.getString(R.string.default_texp)));
    }

    public void setTexp(Float val) {
        updateData(readCurrentUid() + "." + PREF_TEXP, val);
    }


    public Float readPplat() {
        final boolean isPediatric = readPediatricStatus();
        return sp.getFloat(readCurrentUid() + "." + PREF_PLATEAU_PRESSURE, Float.parseFloat(context.getString(isPediatric ? R.string.default_pplat_ped : R.string.default_pplat)));
    }

    public void setPplat(Float val) {
        updateData(readCurrentUid() + "." + PREF_PLATEAU_PRESSURE, val);
    }

    public Float readTinsp() {
        return sp.getFloat(readCurrentUid() + "." + PREF_INSP_TIME, Float.parseFloat(context.getString(R.string.default_inhale_time)));
    }

    public void setTinsp(Float val) {
        updateData(readCurrentUid() + "." + PREF_INSP_TIME, val);
    }


    public Float readSupportPressure() {
        return sp.getFloat(readCurrentUid() + "." + PREF_SUPPORT_PRESSURE, Float.parseFloat(context.getString(R.string.default_support_pressure)));
    }

    public void setSupportPressure(Float val) {
        updateData(readCurrentUid() + "." + PREF_SUPPORT_PRESSURE, val);
    }

    public Float readSlope() {
        return sp.getFloat(readCurrentUid() + "." + PREF_SLOPE, Float.parseFloat(context.getString(R.string.default_slope)));
    }

    public void setSlope(Float val) {
        updateData(readCurrentUid() + "." + PREF_SLOPE, val);
    }


    public Float readFiO2() {
        final boolean isMidRangeFiO2REquired = getModeCategory(readVentilationMode()) == MODE_NIV;  // For NIV modes and SPONT
        return sp.getFloat(readCurrentUid() + "." + PREF_FIO2, Float.parseFloat(context.getString(isMidRangeFiO2REquired ? R.string.default_fio2_niv : R.string.default_fio2)));
    }

    public void setFiO2(Float val) {
        updateData(readCurrentUid() + "." + PREF_FIO2, val);
    }


    public Boolean readApneaSettingsStatus() {
        return sp.getBoolean(readCurrentUid() + "." + PREF_APNEA_SETTINGS_STATUS, false);
    }

    public void setApneaSettingsStatus(boolean isActive) {
        updateData(readCurrentUid() + "." + PREF_APNEA_SETTINGS_STATUS, isActive);
    }


    public Float readRRApnea() {
        return sp.getFloat(readCurrentUid() + "." + PREF_APNEA_RR, Float.parseFloat(context.getString(R.string.default_apnea_rr)));
    }

    public void setRRApnea(Float val) {
        updateData(readCurrentUid() + "." + PREF_APNEA_RR, val);
    }

    public Float readTApnea() {
        return sp.getFloat(readCurrentUid() + "." + PREF_APNEA_TIME, Float.parseFloat(context.getString(R.string.default_apnea_time)));
    }

    public void setTApnea(Float val) {
        updateData(readCurrentUid() + "." + PREF_APNEA_TIME, val);
    }


    public Float readVtApnea() {
        int resVti = R.string.default_apnea_vt;
        return sp.getFloat(readCurrentUid() + "." + PREF_APNEA_TIDAL_VOLUME, Float.parseFloat(context.getString(resVti)));
    }

    public void setVtApnea(Float val) {
        updateData(readCurrentUid() + "." + PREF_APNEA_TIDAL_VOLUME, val);
    }

    public Float readTrigFlowApnea() {
        return sp.getFloat(readCurrentUid() + "." + PREF_APNEA_TRIG_FLOW, Float.parseFloat(context.getString(R.string.default_apnea_trigflow)));
    }

    public void setTrigFlowApnea(Float val) {
        updateData(readCurrentUid() + "." + PREF_APNEA_TRIG_FLOW, val);
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        updateData(PREF_IS_LOGGED_IN, isLoggedIn);
    }

    public boolean readIsLoggedIn() {
        return sp.getBoolean(PREF_IS_LOGGED_IN, false);
    }

    public void setDeepSleeped(boolean isDeepSleep) {
        Log.i("SLEEP_CHECK", "set deep sleep status : " + String.valueOf(isDeepSleep));
        updateData(PREF_IS_DEEP_SLEEP, isDeepSleep);
    }

    public boolean readIsDeepSleeped() {
        return sp.getBoolean(PREF_IS_DEEP_SLEEP, false);
    }

    public void setPatientProfile(String json) {
        updateData(PREF_PATIENT_PROFILE, json);
    }

    public String readPatientProfile() {
        return sp.getString(PREF_PATIENT_PROFILE, null);
    }


    //maneuvers limits

    public void setManeuversPplatLimits(Float min, Float max) {
        updateLimits(readCurrentUid() + "." + PREF_MANEUVERS_MIN_MAX, min, max);
    }

    public Float[] readManeuversPplatLimits() {
        return readLimits(readCurrentUid() + "." + PREF_MANEUVERS_MIN_MAX, Float.valueOf(context.getString(R.string.default_min_manuvers_limit)), Float.valueOf(context.getString(R.string.default_max_manuvers_limit)));
    }

    public Float readManeuversPplatValue() {
        return sp.getFloat(readCurrentUid() + "." + PREF_MANEUVERS_PLATEAU, Float.valueOf(context.getString(R.string.default_maneuvers)));
    }

    public void setManeuversPplatValue(Float plateau) {
        updateData(readCurrentUid() + "." + PREF_MANEUVERS_PLATEAU, plateau);
    }

    public Float readManeuversStaticComplianceValue() {
        return sp.getFloat(readCurrentUid() + "." + PREF_MANEUVERS_STATIC_COMPLINES, Float.valueOf(context.getString(R.string.default_maneuvers)));
    }

    public void setManeuversStaticComplianceValue(Float complines) {
        updateData(readCurrentUid() + "." + PREF_MANEUVERS_STATIC_COMPLINES, complines);
    }


    public Float readManeuversAutoPeepValue() {
        return sp.getFloat(readCurrentUid() + "." + PREF_MANEUVERS_PEEP, Float.valueOf(context.getString(R.string.default_maneuvers)));
    }

    public void setManeuversAutoPeepValue(Float peepManeuversValue) {
        updateData(readCurrentUid() + "." + PREF_MANEUVERS_PEEP, peepManeuversValue);
    }

    public String readExpiratoryDate() {
        return sp.getString(readCurrentUid() + "." + PREF_EXPIRATORY_DATE, context.getString(R.string.default_date_time));
    }

    public void setExpiratoryDate(String expiratoryDate) {
        updateData(readCurrentUid() + "." + PREF_EXPIRATORY_DATE, expiratoryDate);
    }


    public String readInspiratoryDate() {
        return sp.getString(readCurrentUid() + "." + PREF_INSPIRATORY_DATE, context.getString(R.string.default_date_time));
    }

    public void setInspiratoryDate(String inspiratoryDate) {
        updateData(readCurrentUid() + "." + PREF_INSPIRATORY_DATE, inspiratoryDate);
    }


    public String readMeasureTime() {
        return sp.getString(readCurrentUid() + "." + PREF_MEASURE_TIME, "");
    }

    public void setMeasureTime(String inspiratoryDate) {
        updateData(readCurrentUid() + "." + PREF_MEASURE_TIME, inspiratoryDate);
    }


    // Calibration of presser,flow , o2 , Turbine , flow


    public void setTurbineCalibration(SensorCalibration sensorCalibration) {
        // updateData(readCurrentUid() + "." + PREF_TURBINE_CALIBRATION, tightnessDate);

        if (sensorCalibration != null) {
            SharedPreferences.Editor editor = sp.edit();
            Gson gson = new Gson();
            String json = gson.toJson(sensorCalibration);
            editor.putString(readCurrentUid() + "." + PREF_TURBINE_CALIBRATION, json);
            editor.apply();
        }

    }

    public SensorCalibration readTurbineCalibration() throws JsonSyntaxException {
        Gson gson = new Gson();
        String json = sp.getString(readCurrentUid() + "." + PREF_TURBINE_CALIBRATION, "");
        return gson.fromJson(json, SensorCalibration.class);
    }

    public boolean readTurbineCalibrationStatus() {
        try {
            return readTurbineCalibration().getStatus() == SENSOR_CALIBRATION_SUCCESS;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String readTurbineCalibrationDate() {
        try {
            return readTurbineCalibration().getDate();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setFlowCalibration(SensorCalibration sensorCalibration) {
        //updateData(readCurrentUid() + "." + PREF_FLOW_SENSOR_CALIBRATION, sensorCalibrationDate);


        if (sensorCalibration != null) {
            SharedPreferences.Editor editor = sp.edit();
            Gson gson = new Gson();
            String json = gson.toJson(sensorCalibration);
            editor.putString(readCurrentUid() + "." + PREF_FLOW_SENSOR_CALIBRATION, json);
            editor.apply();
        }

    }

    public SensorCalibration readFlowSensorCalibration() {
        //return sp.getString(readCurrentUid() + "." + PREF_FLOW_SENSOR_CALIBRATION, "");

        Gson gson = new Gson();
        String json = sp.getString(readCurrentUid() + "." + PREF_FLOW_SENSOR_CALIBRATION, "");
        return gson.fromJson(json, SensorCalibration.class);

    }

    public boolean readFlowCalibrationStatus() {
        try {
            return readFlowSensorCalibration().getStatus() == SENSOR_CALIBRATION_SUCCESS;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String readFlowCalibrationDate() {
        try {
            return readFlowSensorCalibration().getDate();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setOxygenCalibration(SensorCalibration sensorCalibration) {
        //updateData(readCurrentUid() + "." + PREF_OXYGEN_CALIBRATION, oxygenCalibrationDate);


        if (sensorCalibration != null) {
            SharedPreferences.Editor editor = sp.edit();
            Gson gson = new Gson();
            String json = gson.toJson(sensorCalibration);
            editor.putString(readCurrentUid() + "." + PREF_OXYGEN_CALIBRATION, json);
            editor.apply();
        }

    }

    public SensorCalibration readOxygenCalibration() {

        Gson gson = new Gson();
        String json = sp.getString(readCurrentUid() + "." + PREF_OXYGEN_CALIBRATION, "");
        return gson.fromJson(json, SensorCalibration.class);

    }

    public boolean readOxygenCalibrationStatus() {
        try {
            return readOxygenCalibration().getStatus() == SENSOR_CALIBRATION_SUCCESS;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String readOxygenCalibrationDate() {
        try {
            return readOxygenCalibration().getDate();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setPresserCalibration(SensorCalibration sensorCalibration) {
        // updateData(readCurrentUid() + "." + PREF_PRESSER_CALIBRATION, oxygenCalibrationDate);

        if (sensorCalibration != null) {
            SharedPreferences.Editor editor = sp.edit();
            Gson gson = new Gson();
            String json = gson.toJson(sensorCalibration);
            editor.putString(readCurrentUid() + "." + PREF_PRESSER_CALIBRATION, json);
            editor.apply();
        }


    }

    public SensorCalibration readPressureCalibration() {
        // return sp.getString(readCurrentUid() + "." + PREF_PRESSER_CALIBRATION, "");

        Gson gson = new Gson();
        String json = sp.getString(readCurrentUid() + "." + PREF_PRESSER_CALIBRATION, "");
        return gson.fromJson(json, SensorCalibration.class);

    }

    public boolean readPressureCalibrationStatus() {
        try {
            return readPressureCalibration().getStatus() == SENSOR_CALIBRATION_SUCCESS;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String readPressureCalibrationDate() {
        try {
            return readPressureCalibration().getDate();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


    //    VENTILATOR PARAMETER USER LIMITS
    public void setPipLimits(Float min, Float max) {
        updateLimits(readCurrentUid() + "." + PREF_PIP_MIN_MAX, min, max);
    }

    public Float[] readPipLimits() {
        return readLimits(readCurrentUid() + "." + PREF_PIP_MIN_MAX, Float.valueOf(context.getString(R.string.default_min_pip_limit)), Float.valueOf(context.getString(R.string.default_max_pip_limit)));
    }

    public void setVtiLimits(Float min, Float max) {
        updateLimits(readCurrentUid() + "." + PREF_VTI_MIN_MAX, min, max);
    }

    public Float[] readVtiLimits() {
        return readLimits(readCurrentUid() + "." + PREF_VTI_MIN_MAX, Float.valueOf(context.getString(R.string.default_min_vti_limit)), Float.valueOf(context.getString(R.string.default_max_vti_limit)));
    }

    public void setVteLimits(Float min, Float max) {
        updateLimits(readCurrentUid() + "." + PREF_VTE_MIN_MAX, min, max);
    }

    public Float[] readVteLimits() {
        return readLimits(readCurrentUid() + "." + PREF_VTE_MIN_MAX, Float.valueOf(context.getString(R.string.default_min_vte_limit)), Float.valueOf(context.getString(R.string.default_max_vte_limit)));
    }

    public void setRRLimits(Float min, Float max) {
        updateLimits(readCurrentUid() + "." + PREF_RR_MIN_MAX, min, max);
    }

    public Float[] readRRLimits() {
        return readLimits(readCurrentUid() + "." + PREF_RR_MIN_MAX, Float.valueOf(context.getString(R.string.default_min_rr_limit)), Float.valueOf(context.getString(R.string.default_max_rr_limit)));
    }

    public void setPEEPLimits(Float min, Float max) {
        updateLimits(readCurrentUid() + "." + PREF_PEEP_MIN_MAX, min, max);
    }

    public Float[] readPeepLimits() {
        return readLimits(readCurrentUid() + "." + PREF_PEEP_MIN_MAX, Float.valueOf(context.getString(R.string.default_min_peep_limit)), Float.valueOf(context.getString(R.string.default_max_peep_limit)));
    }

    public void setMviLimits(Float min, Float max) {
        updateLimits(readCurrentUid() + "." + PREF_MVI_MIN_MAX, min, max);
    }

    public Float[] readMviLimits() {
        return readLimits(readCurrentUid() + "." + PREF_MVI_MIN_MAX, Float.valueOf(context.getString(R.string.default_min_mvi_limit)), Float.valueOf(context.getString(R.string.default_max_mvi_limit)));
    }

    public void setFiO2Limits(Float min, Float max) {
        updateLimits(readCurrentUid() + "." + PREF_FIO2_MIN_MAX, min, max);
    }

    public Float[] readFiO2Limits() {
        return readLimits(readCurrentUid() + "." + PREF_FIO2_MIN_MAX, Float.valueOf(context.getString(R.string.default_min_fio2_limit)), Float.valueOf(context.getString(R.string.default_max_fio2_limit)));
    }

    public void setSpO2Limits(Float min, Float max) {
        updateLimits(readCurrentUid() + "." + PREF_SPO2_MIN_MAX, min, max);
    }

    public Float[] readSpO2Limits() {
        return readLimits(readCurrentUid() + "." + PREF_SPO2_MIN_MAX, Float.valueOf(context.getString(R.string.default_min_spo2_limit)), Float.valueOf(context.getString(R.string.default_max_spo2_limit)));
    }


    public void setTiTotLimits(Float min, Float max) {
        updateLimits(readCurrentUid() + "." + PREF_TITOT_MIN_MAX, min, max);
    }

    public Float[] readTiTotLimits() {
        return readLimits(readCurrentUid() + "." + PREF_TITOT_MIN_MAX, Float.valueOf(context.getString(R.string.default_min_titot_limit)), Float.valueOf(context.getString(R.string.default_max_titot_limit)));
    }

    public void setLeakLimits(Float min, Float max) {
        updateLimits(readCurrentUid() + "." + PREF_LEAK_MIN_MAX, min, max);
    }

    public Float[] readLeakLimits() {
        return readLimits(readCurrentUid() + "." + PREF_LEAK_MIN_MAX, Float.valueOf(context.getString(R.string.default_min_leak_limit)), Float.valueOf(context.getString(R.string.default_max_leak_limit)));
    }

    // VENTILATOR PARAMETER USER LIMITS STATE
    public void setPipLimitState(boolean isActive) {
        updateLimitState(readCurrentUid() + "." + PREF_PIP_ALARM_STATE, isActive);
    }

    public boolean readPipLimitState() {
        return readLimitState(readCurrentUid() + "." + PREF_PIP_ALARM_STATE);
    }

    public void setVtiLimitState(boolean isActive) {
        updateLimitState(readCurrentUid() + "." + PREF_VTI_ALARM_STATE, isActive);
    }

    public boolean readVtiLimitState() {
        return readLimitState(readCurrentUid() + "." + PREF_VTI_ALARM_STATE);
    }

    public void setVteLimitState(boolean isActive) {
        updateLimitState(readCurrentUid() + "." + PREF_VTE_ALARM_STATE, isActive);
    }

    public boolean readVteLimitState() {
        return readLimitState(readCurrentUid() + "." + PREF_VTE_ALARM_STATE);
    }

    public void setRRLimitState(boolean isActive) {
        updateLimitState(readCurrentUid() + "." + PREF_RR_ALARM_STATE, isActive);
    }

    public boolean readRRLimitState() {
        return readLimitState(readCurrentUid() + "." + PREF_RR_ALARM_STATE);
    }

    public void setPeepLimitState(boolean isActive) {
        updateLimitState(readCurrentUid() + "." + PREF_PEEP_ALARM_STATE, isActive);
    }

    public boolean readPeepLimitState() {
        return readLimitState(readCurrentUid() + "." + PREF_PEEP_ALARM_STATE);
    }

    public void setMviLimitState(boolean isActive) {
        updateLimitState(readCurrentUid() + "." + PREF_MVI_ALARM_STATE, isActive);
    }

    public boolean readMviLimitState() {
        return readLimitState(readCurrentUid() + "." + PREF_MVI_ALARM_STATE);
    }

    public void setFio2LimitState(boolean isActive) {
        updateLimitState(readCurrentUid() + "." + PREF_FIO2_ALARM_STATE, isActive);
    }

    public boolean readFio2LimitState() {
        return readLimitState(readCurrentUid() + "." + PREF_FIO2_ALARM_STATE);
    }

    public void setSpO2LimitState(boolean isActive) {
        updateLimitState(readCurrentUid() + "." + PREF_SPO2_ALARM_STATE, isActive);
    }

    public boolean readSpO2LimitState() {
        return readLimitState(readCurrentUid() + "." + PREF_SPO2_ALARM_STATE);
    }

    public void setTiTotLimitState(boolean isActive) {
        updateLimitState(readCurrentUid() + "." + PREF_TITOT_ALARM_STATE, isActive);
    }

    public boolean readTiTotLimitState() {
        return readLimitState(readCurrentUid() + "." + PREF_TITOT_ALARM_STATE);
    }


    public void setLeakLimitState(boolean isActive) {
        updateLimitState(readCurrentUid() + "." + PREF_LEAK_ALARM_STATE, isActive);
    }

    public boolean readLeakLimitState() {
        return readLimitState(readCurrentUid() + "." + PREF_LEAK_ALARM_STATE);
    }


    public void setGraphAutoScaling(boolean state) {
        updateData(PREF_GRAPH_AUTOSCALE, state);
    }

    public boolean readGraphAutoScaling() {
        return sp.getBoolean(PREF_GRAPH_AUTOSCALE, true);
    }

    public void setLeakCompensationStatus(boolean state) {
        updateData(PREF_LEAK_COMPENSATE, state);
    }

    public boolean readLeakCompensationStatus() {
        return sp.getBoolean(PREF_LEAK_COMPENSATE, true);
    }

    public void setTubeBlockageAlarmStatus(boolean state) {
        updateData(PREF_TUBE_BLOCKAGE_ALARM, state);
    }

    public boolean readTubeBlockageAlarmStatus() {
        return sp.getBoolean(PREF_TUBE_BLOCKAGE_ALARM, true);
    }

    public void setCuffLeakageAlarmStatus(boolean state) {
        updateData(PREF_CUFF_LEAKAGE_ALARM, state);
    }

    public boolean readCuffLeakageAlarmStatus() {
        return sp.getBoolean(PREF_CUFF_LEAKAGE_ALARM, false);
    }

    public void setAlarmSuggestionStatus(boolean state) {
        updateData(PREF_ALARM_SUGGESTION, state);
    }

    public boolean readAlarmSuggestionStatus() {
        return sp.getBoolean(PREF_ALARM_SUGGESTION, false);
    }

    public void setLeakBasedDisconnectionStatus(boolean state) {
        updateData(PREF_LEAK_BASED_DISCONNECT, state);
    }

    public boolean readLeakBasedAlarmStatus() {
        return sp.getBoolean(PREF_LEAK_BASED_DISCONNECT, true);
    }


    public boolean readOxygenHoldStatus() {
        return sp.getBoolean(PREF_IS_OXYGEN_HOLD_ACTIVE, false);
    }

    public void setOxygenHoldStatus(boolean status) {
        updateData(PREF_IS_OXYGEN_HOLD_ACTIVE, status);
    }


    public boolean readPediatricStatus() {
        return sp.getBoolean(PREF_IS_PEDIATRIC_ACTIVE, false);
    }

    public void setPediatricStatus(boolean status) {
        Log.i("PEDIATRICCHECK", status ? "CHILD" : "ADULT");
        updateData(PREF_IS_PEDIATRIC_ACTIVE, status);
    }

    public void setGraphPoints(int graphPoints) {
        updateData(PREF_GRAPH_POINTS, graphPoints);
    }

    public int readGraphPoints() {
        return sp.getInt(PREF_GRAPH_POINTS, GRAPH_POINTS_MAX);
    }

    public void setEmergencyContact(String contact) {
        updateData(PREF_EMERGENCY_CONTACT, contact);
    }

    public String readEmergencyContact() {
        return sp.getString(PREF_EMERGENCY_CONTACT, null);
    }

    public void setStandbyStatus(boolean isStandby) {
        updateData(PREF_STANDBY_STATUS, isStandby);
    }
    public void setShutDownStatus(boolean isShutDown) {
        updateData(PREF_SHUTDOWN_STATUS,isShutDown);
    }
    public boolean readShutDownStatus() {return sp.getBoolean(PREF_SHUTDOWN_STATUS,false); }

    public boolean readStandbyStatus() {
        return sp.getBoolean(PREF_STANDBY_STATUS, false);
    }

    // Lock Screen Check
    public void setLockScreenStatus(boolean state) {
        updateData(PREF_SCREEN_LOCK, state);
    }

    public boolean readLockScreenStatus() {
        return sp.getBoolean(PREF_SCREEN_LOCK, true);
    }


    //sensor Analysis

    public int readSensorLowO2() {
        return sp.getInt(readCurrentUid() + "." + PREF_SENSOR_LOW_PRESSURE_O2, SENSOR_MISSING);
    }

    public void setSensorLowO2(int val) {
        updateData(readCurrentUid() + "." + PREF_SENSOR_LOW_PRESSURE_O2, val);
    }

    public int readSensorHighO2() {
        return sp.getInt(readCurrentUid() + "." + PREF_SENSOR_HIGH_PRESSURE_O2, SENSOR_MISSING);
    }

    public void setSensorHighO2(int val) {
        updateData(readCurrentUid() + "." + PREF_SENSOR_HIGH_PRESSURE_O2, val);
    }

    public int readSensorCO2() {
        return sp.getInt(readCurrentUid() + "." + PREF_SENSOR_CO2, SENSOR_MISSING);
    }

    public void setSensorCO2(int val) {
        updateData(readCurrentUid() + "." + PREF_SENSOR_CO2, val);
    }

    public int readSensorSPO2() {
        return sp.getInt(readCurrentUid() + "." + PREF_SENSOR_SPO2, SENSOR_MISSING);
    }

    public void setSensorSPO2(int val) {
        updateData(readCurrentUid() + "." + PREF_SENSOR_SPO2, val);
    }

    public int readSensorTemp() {
        return sp.getInt(readCurrentUid() + "." + PREF_SENSOR_TEMP, SENSOR_MISSING);
    }

    public void setSensorTemp(int val) {
        updateData(readCurrentUid() + "." + PREF_SENSOR_TEMP, val);
    }

    public int readSensorInhaleFlow() {
        return sp.getInt(readCurrentUid() + "." + PREF_SENSOR_INAHLE_FLOW, SENSOR_MISSING);
    }

    public void setSensorInhaleFlow(int val) {
        updateData(readCurrentUid() + "." + PREF_SENSOR_INAHLE_FLOW, val);
    }

    public int readSensorExhaleFlow() {
        return sp.getInt(readCurrentUid() + "." + PREF_SENSOR_EXHALE_FLOW, SENSOR_MISSING);
    }

    public void setSensorExhaleFlow(int val) {
        updateData(readCurrentUid() + "." + PREF_SENSOR_EXHALE_FLOW, val);
    }


    public void clearProfilePreferences(PatientProfile userid) {
        SharedPreferences.Editor editor = sp.edit();

        editor.remove(userid + "." + PREF_VENTILATION_MODE);
        editor.remove(userid + "." + PREF_PIP);
        editor.remove(userid + "." + PREF_TIDAL_VOLUME);
        editor.remove(userid + "." + PREF_RR);
        editor.remove(userid + "." + PREF_TRIG_FLOW);
        editor.remove(userid + "." + PREF_PLATEAU_PRESSURE);
        editor.remove(userid + "." + PREF_PEEP);
        editor.remove(userid + "." + PREF_INSP_TIME);
        editor.remove(userid + "." + PREF_SUPPORT_PRESSURE);
        editor.remove(userid + "." + PREF_PEAK_FLOW);
        editor.remove(userid + "." + PREF_FIO2);
        editor.remove(userid + "." + PREF_SLOPE);
        editor.remove(userid + "." + PREF_TLOW);
        editor.remove(userid + "." + PREF_TEXP);

        // APNEA PARAMETERS
        editor.remove(userid + "." + PREF_APNEA_SETTINGS_STATUS);
        editor.remove(userid + "." + PREF_APNEA_TIDAL_VOLUME);
        editor.remove(userid + "." + PREF_APNEA_RR);
        editor.remove(userid + "." + PREF_APNEA_TRIG_FLOW);

        // APNEA PARAMETERS
        editor.remove(userid + "." + PREF_APNEA_TIDAL_VOLUME);
        editor.remove(userid + "." + PREF_APNEA_RR);
        editor.remove(userid + "." + PREF_APNEA_TRIG_FLOW);

        editor.remove(userid + "." + PREF_PIP_MIN_MAX);
        editor.remove(userid + "." + PREF_VTI_MIN_MAX);
        editor.remove(userid + "." + PREF_RR_MIN_MAX);
        editor.remove(userid + "." + PREF_PEEP_MIN_MAX);
        editor.remove(userid + "." + PREF_MVI_MIN_MAX);
        editor.remove(userid + "." + PREF_MVE_MIN_MAX);
        editor.remove(userid + "." + PREF_TITOT_MIN_MAX);

        editor.remove(PREF_EMERGENCY_CONTACT);
        editor.remove(PREF_BODY_WEIGHT);
        editor.remove(PREF_IS_OXYGEN_HOLD_ACTIVE);
//        editor.remove(PREF_IS_PEDIATRIC_ACTIVE);

        editor.apply();
    }

    //Need to first check the data flowing in the preference manager and the flow of control towards the main activity
    //The data in the flow is the main cause of the control dialog fragment and the points of the settings of the data.
    @Deprecated
    public void createUserProfile(PatientProfile profile, Gender gender, Float height, Float age, Float weight) {
        Log.i("USER_PROFILE", profile + " " + gender + " " + " " + String.valueOf(age) + " " + String.valueOf(height) + " " + String.valueOf(weight));

        setCurrentUid(profile);
        setIsLoggedIn(true);
        setGender(gender);
        setBodyHeight(height);
        setAge(age);
        setBodyWeight(weight);
    }


    public void updateParameterViaName(final String name, Float val) {
        if (name != null && !name.trim().isEmpty() && val != null) {
            switch (name) {
                case LBL_PIP:
                    setPip(val);
                    break;


                case LBL_PEEP:
                    setPEEP(val);
                    break;

                case LBL_FIO2:
                    setFiO2(val);
                    break;

                case LBL_VTI:
                    setVti(val);
                    break;

                case LBL_RR:
                    setRR(val);

                    // syncronize IE Ratio with RR
//                    Float tinsp = Configs.calculateCorrectedInspTimeFromIERatio(readRR().intValue(), readTinsp());
//                    setTinsp(tinsp);
                    break;

                case LBL_TRIG_FLOW:
                    setTrigFlow(val);
                    break;

                case LBL_PPLAT:
                    setPplat(val);
                    break;

                case LBL_TINSP:
                    setTinsp(val);
                    break;

                case LBL_PEAK_FLOW:
                    setPeakFlow(val);
                    break;

                case LBL_SUPPORT_PRESSURE:
                    setSupportPressure(val);
                    break;

                case LBL_SLOPE:
                    setSlope(val);
                    break;

                case LBL_TLOW:
                    setTlow(val);
                    break;

                case LBL_TEXP:
                    setTexp(val);
                    break;

            // APNEA PARAMETERS
                case LBL_APNEA_VT:
                    setVtApnea(val);
                    break;

                case LBL_APNEA_RR:
                    setRRApnea(val);
                    break;

                case LBL_TAPNEA:
                    setTApnea(val);
                    break;

                case LBL_APNEA_TRIG_FLOW:
                    setTrigFlowApnea(val);
                    break;

            }
        }
    }

    //    UPDATING SHARED PREFERENCES
    private void updateData(String key, Float val) {
        if (val != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat(key, val);
            editor.apply();
        }
    }

    private void updateData(String key, String val) {
        if (key != null && val != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, val);
            editor.apply();
        }
    }

    private void updateData(String key, Boolean val) {
        if (key != null && val != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(key, val);
            editor.apply();
        }
    }

    private void updateData(String key, Integer val) {
        if (key != null && val != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(key, val);
            editor.apply();
        }
    }

    private void updateLimits(String key, Float min, Float max) {
        if (key != null && min != null && max != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, String.valueOf(min) + LIMIT_SEPARATOR + String.valueOf(max));
            editor.apply();
        }
    }

    private void updateLimitState(String key, boolean state) {
        if (key != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(key, state);
            editor.apply();
        }
    }

    private boolean readLimitState(String key) {
        if (key != null) return sp.getBoolean(key, true);
        return false;
    }

    private Float[] readLimits(String key, Float defaultMin, Float defaultMax) {

        Float[] limits = null;

        if (key != null && defaultMin != null && defaultMax != null) {
            limits = new Float[2];
            limits[0] = defaultMin;
            limits[1] = defaultMax;

            String limit = sp.getString(key, null);
            if (limit != null) {
                String[] vals = limit.split(LIMIT_SEPARATOR);
                limits[0] = Float.valueOf(vals[0]);
                limits[1] = Float.valueOf(vals[1]);
            }

        }

        return limits;
    }

    public void clearLimitOnePreferences() {
        SharedPreferences.Editor editor = sp.edit();


        editor.remove(readCurrentUid() + "." + PREF_PIP_MIN_MAX);
        editor.remove(readCurrentUid() + "." + PREF_VTE_MIN_MAX);
        editor.remove(readCurrentUid() + "." + PREF_RR_MIN_MAX);
        editor.remove(readCurrentUid() + "." + PREF_PEEP_MIN_MAX);
        editor.remove(readCurrentUid() + "." + PREF_MVI_MIN_MAX);
        //  editor.remove(userid + "." + PREF_MVE_MIN_MAX);
        //  editor.remove(userid + "." + PREF_TITOT_MIN_MAX);

        //  editor.remove(PREF_PINSP_MIN_MAX);
        //  editor.remove(PREF_BODY_WEIGHT);
        //  editor.remove(PREF_IS_OXYGEN_HOLD_ACTIVE);
//        editor.remove(PREF_IS_PEDIATRIC_ACTIVE);

        editor.apply();
    }

    public void clearLimitOneStatePreferences() {
        SharedPreferences.Editor editor = sp.edit();


        editor.remove(readCurrentUid() + "." + PREF_PIP_ALARM_STATE);
        editor.remove(readCurrentUid() + "." + PREF_VTE_ALARM_STATE);
        editor.remove(readCurrentUid() + "." + PREF_RR_ALARM_STATE);
        editor.remove(readCurrentUid() + "." + PREF_PEEP_ALARM_STATE);
        editor.remove(readCurrentUid() + "." + PREF_MVI_ALARM_STATE);
        //  editor.remove(userid + "." + PREF_MVE_MIN_MAX);
        //  editor.remove(userid + "." + PREF_TITOT_MIN_MAX);

        //  editor.remove(PREF_PINSP_MIN_MAX);
        //  editor.remove(PREF_BODY_WEIGHT);
        //  editor.remove(PREF_IS_OXYGEN_HOLD_ACTIVE);
//        editor.remove(PREF_IS_PEDIATRIC_ACTIVE);

        editor.apply();
    }


    public void clearLimitTwoPreferences() {
        SharedPreferences.Editor editor = sp.edit();


        editor.remove(readCurrentUid() + "." + PREF_FIO2_MIN_MAX);
        // editor.remove(readCurrentUid() + "." + PREF_VTI_MIN_MAX);
        // editor.remove(readCurrentUid() + "." + PREF_RR_MIN_MAX);
        //  editor.remove(readCurrentUid() + "." + PREF_PEEP_MIN_MAX);
        // editor.remove(readCurrentUid() + "." + PREF_MVI_MIN_MAX);
        //  editor.remove(userid + "." + PREF_MVE_MIN_MAX);
        //  editor.remove(userid + "." + PREF_TITOT_MIN_MAX);

        //  editor.remove(PREF_PINSP_MIN_MAX);
        //  editor.remove(PREF_BODY_WEIGHT);
        //  editor.remove(PREF_IS_OXYGEN_HOLD_ACTIVE);
//        editor.remove(PREF_IS_PEDIATRIC_ACTIVE);

        editor.apply();
    }

    public void clearLimitTwoStatePreferences() {
        SharedPreferences.Editor editor = sp.edit();


        editor.remove(readCurrentUid() + "." + PREF_FIO2_ALARM_STATE);
        // editor.remove(readCurrentUid() + "." + PREF_VTI_MIN_MAX);
        // editor.remove(readCurrentUid() + "." + PREF_RR_MIN_MAX);
        //  editor.remove(readCurrentUid() + "." + PREF_PEEP_MIN_MAX);
        // editor.remove(readCurrentUid() + "." + PREF_MVI_MIN_MAX);
        //  editor.remove(userid + "." + PREF_MVE_MIN_MAX);
        //  editor.remove(userid + "." + PREF_TITOT_MIN_MAX);

        //  editor.remove(PREF_PINSP_MIN_MAX);
        //  editor.remove(PREF_BODY_WEIGHT);
        //  editor.remove(PREF_IS_OXYGEN_HOLD_ACTIVE);
//        editor.remove(PREF_IS_PEDIATRIC_ACTIVE);

        editor.apply();
    }


}
