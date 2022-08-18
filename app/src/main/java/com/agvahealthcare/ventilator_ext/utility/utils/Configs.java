package com.agvahealthcare.ventilator_ext.utility.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.agvahealthcare.ventilator_ext.R;
import com.agvahealthcare.ventilator_ext.VentilatorApp;
import com.agvahealthcare.ventilator_ext.exceptions.InvalidModeException;
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager;
import com.agvahealthcare.ventilator_ext.model.ControlParameterLimit;
import com.agvahealthcare.ventilator_ext.model.ControlParameterModel;
import com.agvahealthcare.ventilator_ext.model.VentMode;
import com.agvahealthcare.ventilator_ext.utility.ConstantKt;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by MOHIT MALHOTRA on 12-09-2018.
 */

public interface Configs {

    enum Direction {
        UP,
        DOWN
    }


    enum AckType{
        INVALID_ACK,
        ACK, NACK,OP_ACK
    }
    enum ControlSettingType{
        BASIC, BACKUP, ADVANCED
    }


    enum RespiratoryHoldType{
        INSPIRATORY_HOLD,
        EXPIRATORY_HOLD
    }

    int BLUETOOTH = 0;
    int WIRED = 1;
    int CONNECTION_MODE = WIRED;
    String TILE_NO_SET_VALUE = "N/A";


    enum PatientProfile{
        TYPE_ADULT,
        TYPE_PED,
        TYPE_NEONAT
    }

    enum Gender{
        TYPE_MALE, TYPE_FEMALE
    }

    enum BatteryLevelType{
        FULLY_CHARGED,
        CRITICALLY_LOW,
        CHARGING,
        ON_BATTERY
    }

    // SENSOR AVAILABILITY
    int SENSOR_AVAILABLE = 1;
    int SENSOR_MISSING = 0;

    int SENSOR_CALIBRATION_SUCCESS = 1;
    int SENSOR_CALIBRATION_FAILURE = 0;


    int ONSCREEN_GRAPH_LIMIT = 3;

    int GRAPH_POINTS_MAX = 12;
    int GRAPH_POINTS_DEFAULT = 180;

    // To toggle the configuration for Oxygen level support {3 VALUE MODE}
    // low = 2 value mode ; high = 3 value mode
    boolean isOxygenLevelsAvailable = true;

    // Fio2 setting structure supported
    // Related to backed functionality and doesnot govern any UI changes
    boolean isFio2SettingAvailable = true;

    String PREFIX_ACK = "ACK";
    int ACK_CODE_LENGTH = 4;

    String PREFIX_BATTERY = "BTRY";
    int BATTERY_CODE_LENGTH = 9;

    String PREFIX_HEATSENSE = "HEAT";
    int HEATSENSE_CODE_LENGTH = 3 * 4; // 4 sensors with 3 digit value

    String PREFIX_MOTOR_LIFE = "MTRLF";
    int MOTOR_LIFE_CODE_LENGTH = 4;

    String PREFIX_HARDWARE_VERSION = "VER:";
    String SUFIX_HARDWARE_VERSION = "$";

    String PREFIX_SELFTEST = "STP";
    int SELFTEST_RESPONSE_LENGTH = 3;

    String PREFIX_STANDBY = "STND";
    int STANDBY_RESPONSE_LENGTH = 2;

    String PREFIX_WIFI_CONN = "WIFI";
    int WIFI_CONN_RESPONSE_LENGTH = 1;


    String PREFIX_HANDSHAKE_CALIBRATE = "CALIB";
    int HANDSHAKE_CALIBRATE_LENGTH = 5;

    String PREFIX_DEVICE_NAME_REQUEST = "RQDN";
    int DEVICE_NAME_REQUEST_LENGTH = 1;

    int THRESHOLD_BATTERY_LEVEL = 20;
    int THRESHOLD_OXYGEN_VARIATION_VALUE = 40; // in percentage

    int CONFIGURATION_MODULE_DELAY = 250; // 150ms

    String PREFIX_SENSOR_AVAILABILITY = "SA";
    String SUFIX_SENSOR_AVAILABILITY = "!";
    String PREFIX_SENSOR_CALIBRATION ="SC";
    int SENSOR_CALIBRATION_REQUEST_LENGTH = 2;

    int TAG_SENSOR_LENGTH = 1;
    String TAG_SENSOR_FLOW = "1";
    String TAG_SENSOR_PRESSURE = "2";
    String TAG_SENSOR_OXYGEN = "3";
    String TAG_SENSOR_TURBINE = "4";

    //Testing
    int DEVICE_DISCONNECTED = 0;
    int DEVICE_CONNECTED = 1;


    int WARNING_LEVEL_LOW = 0;
    int WARNING_LEVEL_HIGH = 1;
    int WARNING_LEVEL_UNMUTABLE = 2;
    String  PREFIX_PLUS="+";
    String  PREFIX_MINUS="-";
    String  PREFIX_AND="&";
    String QB_ALARM_MUTE_UNMUTE ="I1";
    String QB_NEBULISER ="I2";
    String QB_OXYGEN ="I3";
    String QB_INSPIRATORY_HOLD ="I4";
    String QB_EXPIRATORY_HOLD ="I5";
    String QB_MANUAL_BREATH ="I6";
    String QB_HOME ="I7";
    String QB_LOCK ="I8";
    String QB_POWER_SWITCH ="I0";
    int KNOB_LENGTH=1;


    Uri URI_ALARM_HIGH_LEVEL = Uri.parse("android.resource://" + VentilatorApp.Companion.getInstance().getPackageName() + "/raw/warning_high_level");
    Uri URI_ALARM_LOW_LEVEL = Uri.parse("android.resource://" + VentilatorApp.Companion.getInstance().getPackageName() + "/raw/warning_low_level");
    Uri URI_ALARM_BATTERY_LOW = Uri.parse("android.resource://" + VentilatorApp.Companion.getInstance().getPackageName() + "/raw/warning_battery_low");

    // parameters
    String LBL_AVERAGE_LEAK = "Average Leak";
    String LBL_LEAK = "Leak";
    String LBL_PRESSURE = "P";
    String LBL_VOLUME = "V";
    String LBL_RAW_VOLUME = "Raw Volume";
    String LBL_RR = "Respiratory Rate";
    String LBL_PEEP = "PEEP";
    String LBL_MV = "MV";
    String LBL_TRIGGER = "Respiratory Type";
    String LBL_RESPIRATORY_PHASE = "Respiratory Phase";
    String LBL_FLOW = "Flow";


    String LBL_VPEAK = "PFR";
    String LBL_TITOT = "Ti/ToT";
    String LBL_IE_RATIO = "I:E";
    String LBL_TIME = "T";
    String LBL_FIO = "FiO";
    String LBL_SPO = "SpO";
    String LBL_PULSE = "Pulse";
    String LBL_TEMPERATURE = "Temp";
    String LBL_DYNAMIC_COMPLIANCE = "Dyn Comp.";
    String LBL_STATIC_COMPLIANCE = "Static Comp.";
    String LBL_LEAK_FLOW = "Leak Flow";
    String LBL_OBSERVED_PLATEAU_PRESSURE = "Support Pressure";
    String LBL_VPLAT = "Vplat";
    String LBL_MEAN_AIRWAY_PRESSURE = "Mean Airway Pressure";
    String LBL_TRISE = LBL_TIME + "rise";

    // Derived paramter names

    String LBL_PIP = "PIP";
    String LBL_PPLAT = "Pplat";
    String LBL_PMEAN = LBL_PRESSURE + "mean";
    String LBL_MVI = LBL_MV + "i";

    String LBL_MVE = LBL_MV + "e";
    String LBL_FIO2 = LBL_FIO + "2";
    String LBL_SPO2 = LBL_SPO + "2";
    String LBL_HR = "HR";
    String LBL_VT = "VT";
    String LBL_VTI = LBL_VT + "i";
    String LBL_VTE = LBL_VT + "e";
    String LBL_VPEAK_I = LBL_VPEAK + "i";
    String LBL_VPEAK_E = LBL_VPEAK + "e";
    String LBL_TRIG_FLOW = "Trigger";
    String LBL_PEAK_FLOW = "Flow Limit";
    String LBL_TINSP = LBL_TIME + "insp";
    String LBL_TEXPR = LBL_TIME + "exp";  // observed param for expire time or insp termination
    String LBL_VLEAK = LBL_VOLUME + LBL_LEAK;
    String LBL_SUPPORT_PRESSURE = "Support Pressure";
    String LBL_SLOPE = "Slope";
    String LBL_TLOW = LBL_TIME + "low";
    String LBL_TEXP = LBL_TEXPR;  // control param for expire time or insp termination
    String LBL_VOID_TILE = "void";
    String LBL_Volume_KEY ="Alarm Key";
    String LBL_HEIGHT_KEY="Height Key";
    String LBL_AGE_KEY = "Age Key";
    String LBL_WEIGHT_KEY="Weigh tKey";

    String LBL_APNEA = "apnea";
    String LBL_APNEA_VT = LBL_VT + LBL_APNEA;
    String LBL_APNEA_RR = LBL_RR + LBL_APNEA;
    String LBL_APNEA_TRIG_FLOW = LBL_TRIG_FLOW + LBL_APNEA;
    String LBL_TAPNEA = LBL_TIME + LBL_APNEA;




    int MODE_PCV = 1;
    int MODE_PC_CMV = 11;
    int MODE_PC_SIMV = 12;
    int MODE_PC_PSV = 13;
    int MODE_PC_SPONTANEOUS = 14;
    int MODE_NEONAT_PC_SIMV = 15;
    int MODE_PC_SPONT_DUMMY = 16;
    int MODE_PC_AC = 17;
    int MODE_PC_APRV = 18;


    int MODE_VCV = 2;
    int MODE_VCV_CMV = 21;
    int MODE_VCV_SIMV = 22;
    int MODE_VCV_PRVC = 23;
    int MODE_AUTO_VENTILATION = 24;
    int MODE_VCV_ACV = 25;
    int MODE_VCV_VCV = 26;
    int MODE_NEONAT_VC_SIMV = 27;




    int MODE_NIV = 3;
    int MODE_NIV_CPAP = 31;
    int MODE_NIV_BPAP = 32;
    int MODE_NIV_APRV = 33;
    int MODE_NIV_AV_CPAP = 34;
    int MODE_NIV_AV_BPAP = 35;

    int PARAMETER_TILE_COUNT = 9;


    UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    String SERV_ID_PREFIX = "0000ffe0";
    String CHARACT_ID_PREFIX = "0000ffe1";

    String REGISTERED_ACCOUNT = "1";
    String UNREGISTERED_ACCOUNT = "0";
    String DEACTIVATED_ACCOUNT = "-1";


    String PROFILE_NAME = "profile_name";
    String PROFILE_AGE = "profile_age";
    String PROFILE_GENDER = "profile_gender";
    String PROFILE_HEIGHT = "profile_height";
    String PROFILE_WEIGHT = "profile_weight";
    String PROFILE_EMAIL = "profile_email";
    String PROFILE_PHONE = "profile_phone";
    String PROFILE_HOSPITAL_ID = "profile_hospital_id";

    String INSPIRATORY_HOLD="INSPIRATORY HOLD";
    String EXPIRATORY_HOLD="EXPIRATORY HOLD";

    //acknowlegement priority

    enum AlarmType{
        ALARM_NO_LEVEL,
        ALARM_LOW_LEVEL,
        ALARM_MEDIUM_LEVEL,
        ALARM_HIGH_LEVEL
    }

    int ALARM_NO_LEVEL =0;
    int ALARM_LOW_LEVEL =1;
    int ALARM_MEDIUM_LEVEL =2;
    int ALARM_HIGH_LEVEL =3;




    // TRIGGERS
    int TRIGGER_MACHINE = 0;
    int TRIGGER_PATIENT = 1;

//    int TRIGGER_MACHINE = 1;
//    int TRIGGER_PATIENT = 2;
//    int TRIGGER_APNEA = 0;


    //Alarm Lable
    String ALARM_PIP = "Alarm PIP";
    String ALARM_VTE = "VTE";
    String ALARM_VOLUME = "V";
    String ALARM_RAW_VOLUME = "Raw Volume";
    String ALARM_RR = "Respiratory Rate";
    String ALARM_PEEP = "PEEP";
    String ALARM_MVI = "MVI";
    String ALARM_TRIGGER = "Respiratory Type";
    String ALARM_RESPIRATORY_PHASE = "Respiratory Phase";
    String ALARM_TITOT = "Ti/ToT";
    String ALARM_AVERAGE_LEAK = "Average Leak";


    //Low priority Alarms
    // ACKNOWLEDGEMENT CODES
    String ACK_CODE_0 = "ACK0000";
    String ACK_CODE_1 = "ACK0001";
    String ACK_CODE_2 = "ACK0002";
    String ACK_CODE_3 = "ACK0003";
    String ACK_CODE_4 = "ACK0004";
    String ACK_CODE_5 = "ACK0005";
    String ACK_CODE_6 = "ACK0006";
    String ACK_CODE_7 = "ACK0007";
    String ACK_CODE_8 = "ACK0008";
    String ACK_CODE_9 = "ACK0009";
    String ACK_CODE_10 = "ACK0010";
    String ACK_CODE_11 = "ACK0011";
    String ACK_CODE_12 = "ACK0012";
    String ACK_CODE_13 = "ACK0013";
    String ACK_CODE_14 = "ACK0014";
    String ACK_CODE_15 = "ACK0015";
    String ACK_CODE_16 = "ACK0016";
    String ACK_CODE_17 = "ACK0017";
    String ACK_CODE_18 = "ACK0018";
    String ACK_CODE_19 = "ACK0019";
    String ACK_CODE_20 = "ACK0020";
    String ACK_CODE_21 = "ACK0021";
    String ACK_CODE_22 = "ACK0022";
    String ACK_CODE_23 = "ACK0023";
    String ACK_CODE_24 = "ACK0024";
    String ACK_CODE_25 = "ACK0025";
    String ACK_CODE_26 = "ACK0026";
    String ACK_CODE_27 = "ACK0027";
    String ACK_CODE_28 = "ACK0028";
    String ACK_CODE_29 = "ACK0029";
    String ACK_CODE_30 = "ACK0030";
    String ACK_CODE_31 = "ACK0031";
    String ACK_CODE_32 = "ACK0032";
    String ACK_CODE_33 = "ACK0033";
    String ACK_CODE_34 = "ACK0034";
    String ACK_CODE_35 = "ACK0035";
    String ACK_CODE_36 = "ACK0036";
    String ACK_CODE_37 = "ACK0037";
    String ACK_CODE_38 = "ACK0038";
    String ACK_CODE_39 = "ACK0039";
    String ACK_CODE_40 = "ACK0040";
    String ACK_CODE_41 = "ACK0041";
    String ACK_CODE_42 = "ACK0042";
    String ACK_CODE_43 = "ACK0043";
    String ACK_CODE_44 = "ACK0044";
    String ACK_CODE_45 = "ACK0045";
    String ACK_CODE_46 = "ACK0046";
    String ACK_CODE_47 = "ACK0047";
    String ACK_CODE_48 = "ACK0048";
    String ACK_CODE_49 = "ACK0049";
    String ACK_CODE_50 = "ACK0050";
    String ACK_CODE_51 = "ACK0051";
    String ACK_CODE_52 = "ACK0052";
    String ACK_CODE_53 = "ACK0053";
    String ACK_CODE_54 = "ACK0054";
    String ACK_CODE_55 = "ACK0055";
    String ACK_CODE_56 = "ACK0056";
    String ACK_CODE_57 = "ACK0057";
    String ACK_CODE_58 = "ACK0058";
    String ACK_CODE_59 = "ACK0059";
    String ACK_CODE_60 = "ACK0060";
    String ACK_CODE_61 = "ACK0061";
    String ACK_CODE_62 = "ACK0062";
    String ACK_CODE_63 = "ACK0063";
    String ACK_CODE_64 = "ACK0064";
    String ACK_CODE_65 = "ACK0065";
    String ACK_CODE_66 = "ACK0066";
    String ACK_CODE_67 = "ACK0067";
    String ACK_CODE_68 = "ACK0068";
    String ACK_CODE_69 = "ACK0069";
    String ACK_CODE_70 = "ACK0070";
    String ACK_CODE_71 = "ACK0071";
    String ACK_CODE_72 = "ACK0072";
    String ACK_CODE_73 = "ACK0073";
    String ACK_CODE_74 = "ACK0074";
    String ACK_CODE_75 = "ACK0075";
    String ACK_CODE_76 = "ACK0076";
    String ACK_CODE_77 = "ACK0077";
    String ACK_CODE_78 = "ACK0078";
    String ACK_CODE_79 = "ACK0079";
    String ACK_CODE_80 = "ACK0080";
    String ACK_CODE_81 = "ACK0081";
    String ACK_CODE_82 = "ACK0082";
    String ACK_CODE_83 = "ACK0083";
    String ACK_CODE_84 = "ACK0084";
    String ACK_CODE_90 = "ACK0090";
    String ACK_CODE_91 = "ACK0091";
    String ACK_CODE_92 = "ACK0092";
    String ACK_CODE_93 = "ACK0093";
    String ACK_CODE_94 = "ACK0094";


    //Medium priority alarms
    String ACK_CODE_320 = "ACK0320";
    String ACK_CODE_321 = "ACK0321";
    String ACK_CODE_322 = "ACK0322";
    String ACK_CODE_323 = "ACK0323";
    String ACK_CODE_324 = "ACK0324";
    String ACK_CODE_325 = "ACK0325";
    String ACK_CODE_326 = "ACK0326";
    String ACK_CODE_327 = "ACK0327";
    String ACK_CODE_328 = "ACK0328";
    String ACK_CODE_329 = "ACK0329";
    String ACK_CODE_330 = "ACK0330";
    String ACK_CODE_331 = "ACK0331";
    String ACK_CODE_332 = "ACK0332";
    String ACK_CODE_333 = "ACK0333";
    String ACK_CODE_334 = "ACK0334";
    String ACK_CODE_335 = "ACK0335";
    String ACK_CODE_336 = "ACK0336";
    String ACK_CODE_337 = "ACK0337";
    String ACK_CODE_338 = "ACK0338";
    String ACK_CODE_339 = "ACK0339";
    String ACK_CODE_340 = "ACK0340";
    String ACK_CODE_341 = "ACK0341";
    String ACK_CODE_342 = "ACK0342";
    String ACK_CODE_343 = "ACK0343";
    String ACK_CODE_344 = "ACK0344";
    String ACK_CODE_345 = "ACK0345";
    String ACK_CODE_346 = "ACK0346";
    String ACK_CODE_347 = "ACK0347";
    String ACK_CODE_348 = "ACK0348";
    String ACK_CODE_349 = "ACK0349";
    String ACK_CODE_350 = "ACK0350";
    String ACK_CODE_351 = "ACK0351";
    String ACK_CODE_352 = "ACK0352";
    String ACK_CODE_353 = "ACK0353";
    String ACK_CODE_354 = "ACK0354";
    String ACK_CODE_355 = "ACK0355";
    String ACK_CODE_356 = "ACK0356";
    String ACK_CODE_357 = "ACK0357";
    String ACK_CODE_358 = "ACK0358";
    String ACK_CODE_359 = "ACK0359";
    String ACK_CODE_360 = "ACK0360";
    String ACK_CODE_361 = "ACK0361";
    String ACK_CODE_362 = "ACK0362";
    String ACK_CODE_363 = "ACK0363";
    String ACK_CODE_364 = "ACK0364";
    String ACK_CODE_365 = "ACK0365";
    String ACK_CODE_366 = "ACK0366";
    String ACK_CODE_367 = "ACK0367";
    String ACK_CODE_368 = "ACK0368";
    String ACK_CODE_369 = "ACK0369";
    String ACK_CODE_370 = "ACK0370";
    String ACK_CODE_371 = "ACK0371";
    String ACK_CODE_372 = "ACK0372";
    String ACK_CODE_373 = "ACK0373";
    String ACK_CODE_374 = "ACK0374";
    String ACK_CODE_375 = "ACK0375";
    String ACK_CODE_376 = "ACK0376";
    String ACK_CODE_377 = "ACK0377";
    String ACK_CODE_378 = "ACK0378";
    String ACK_CODE_379 = "ACK0379";
    String ACK_CODE_380 = "ACK0380";
    String ACK_CODE_381 = "ACK0381";
    String ACK_CODE_382 = "ACK0382";
    String ACK_CODE_383 = "ACK0383";
    String ACK_CODE_384 = "ACK0384";
    String ACK_CODE_385 = "ACK0385";
    String ACK_CODE_386 = "ACK0386";
    String ACK_CODE_387 = "ACK0387";
    String ACK_CODE_388 = "ACK0388";
    String ACK_CODE_389 = "ACK0389";
    String ACK_CODE_390 = "ACK0390";
    String ACK_CODE_391 = "ACK0391";
    String ACK_CODE_392 = "ACK0392";
    String ACK_CODE_393 = "ACK0393";
    String ACK_CODE_394 = "ACK0394";
    String ACK_CODE_395 = "ACK0395";
    String ACK_CODE_396 = "ACK0396";
    String ACK_CODE_397 = "ACK0397";
    String ACK_CODE_398 = "ACK0398";
    String ACK_CODE_399 = "ACK0399";
    String ACK_CODE_400 = "ACK0400";
    String ACK_CODE_401 = "ACK0401";
    String ACK_CODE_402 = "ACK0402";
    String ACK_CODE_403 = "ACK0403";
    String ACK_CODE_404 = "ACK0404";
    String ACK_CODE_405 = "ACK0405";
    String ACK_CODE_406 = "ACK0406";
    String ACK_CODE_407 = "ACK0407";
    String ACK_CODE_408 = "ACK0408";
    String ACK_CODE_409 = "ACK0409";
    String ACK_CODE_410 = "ACK0410";
    String ACK_CODE_411 = "ACK0411";
    String ACK_CODE_412 = "ACK0412";
    String ACK_CODE_413 = "ACK0413";
    String ACK_CODE_414 = "ACK0414";
    String ACK_CODE_415 = "ACK0415";
    String ACK_CODE_416 = "ACK0416";
    String ACK_CODE_417 = "ACK0417";
    String ACK_CODE_418 = "ACK0418";
    String ACK_CODE_419 = "ACK0419";
    String ACK_CODE_420 = "ACK0420";
    String ACK_CODE_430 = "ACK0430";

    //high priority alarm
    String ACK_CODE_640 = "ACK0640";
    String ACK_CODE_641 = "ACK0641";
    String ACK_CODE_642 = "ACK0642";
    String ACK_CODE_643 = "ACK0643";
    String ACK_CODE_644 = "ACK0644";
    String ACK_CODE_645 = "ACK0645";
    String ACK_CODE_646 = "ACK0646";
    String ACK_CODE_647 = "ACK0647";
    String ACK_CODE_648 = "ACK0648";
    String ACK_CODE_649 = "ACK0649";
    String ACK_CODE_650 = "ACK0650";
    String ACK_CODE_651 = "ACK0651";
    String ACK_CODE_652 = "ACK0652";
    String ACK_CODE_653 = "ACK0653";
    String ACK_CODE_654 = "ACK0654";
    String ACK_CODE_655 = "ACK0655";
    String ACK_CODE_656 = "ACK0656";
    String ACK_CODE_657 = "ACK0657";
    String ACK_CODE_658 = "ACK0658";
    String ACK_CODE_659 = "ACK0659";
    String ACK_CODE_660 = "ACK0660";
    String ACK_CODE_661 = "ACK0661";
    String ACK_CODE_662 = "ACK0662";
    String ACK_CODE_663 = "ACK0663";
    String ACK_CODE_664 = "ACK0664";
    String ACK_CODE_665 = "ACK0665";
    String ACK_CODE_666 = "ACK0666";
    String ACK_CODE_667 = "ACK0667";
    String ACK_CODE_668 = "ACK0668";
    String ACK_CODE_669 = "ACK0669";
    String ACK_CODE_670 = "ACK0670";
    String ACK_CODE_671 = "ACK0671";
    String ACK_CODE_672 = "ACK0672";
    String ACK_CODE_673 = "ACK0673";
    String ACK_CODE_674 = "ACK0674";
    String ACK_CODE_675 = "ACK0675";
    String ACK_CODE_676 = "ACK0676";
    String ACK_CODE_677 = "ACK0677";
    String ACK_CODE_678 = "ACK0678";
    String ACK_CODE_679 = "ACK0679";
    String ACK_CODE_680 = "ACK0680";
    String ACK_CODE_681 = "ACK0681";
    String ACK_CODE_682 = "ACK0682";
    String ACK_CODE_683 = "ACK0683";
    String ACK_CODE_684 = "ACK0684";
    String ACK_CODE_685 = "ACK0685";
    String ACK_CODE_686 = "ACK0686";
    String ACK_CODE_687 = "ACK0687";
    String ACK_CODE_688 = "ACK0688";
    String ACK_CODE_689 = "ACK0689";
    String ACK_CODE_690 = "ACK0690";
    String ACK_CODE_691 = "ACK0691";
    String ACK_CODE_692 = "ACK0692";
    String ACK_CODE_693 = "ACK0693";
    String ACK_CODE_694 = "ACK0694";
    String ACK_CODE_695 = "ACK0695";
    String ACK_CODE_696 = "ACK0696";
    String ACK_CODE_697 = "ACK0697";
    String ACK_CODE_698 = "ACK0698";
    String ACK_CODE_699 = "ACK0699";
    String ACK_CODE_700 = "ACK0700";
    String ACK_CODE_701 = "ACK0701";
    String ACK_CODE_702 = "ACK0702";
    String ACK_CODE_703 = "ACK0703";
    String ACK_CODE_704 = "ACK0704";
    String ACK_CODE_705 = "ACK0705";
    String ACK_CODE_706 = "ACK0706";
    String ACK_CODE_707 = "ACK0707";
    String ACK_CODE_708 = "ACK0708";
    String ACK_CODE_709 = "ACK0709";
    String ACK_CODE_710 = "ACK0710";
    String ACK_CODE_711 = "ACK0711";
    String ACK_CODE_712 = "ACK0712";
    String ACK_CODE_713 = "ACK0713";
    String ACK_CODE_714 = "ACK0714";
    String ACK_CODE_715 = "ACK0715";
    String ACK_CODE_716 = "ACK0716";
    String ACK_CODE_717 = "ACK0717";
    String ACK_CODE_718 = "ACK0718";
    String ACK_CODE_719 = "ACK0719";
    String ACK_CODE_720 = "ACK0720";
    String ACK_CODE_721 = "ACK0721";
    String ACK_CODE_722 = "ACK0722";
    String ACK_CODE_723 = "ACK0723";
    String ACK_CODE_724 = "ACK0724";
    String ACK_CODE_725 = "ACK0725";
    String ACK_CODE_726 = "ACK0726";
    String ACK_CODE_727 = "ACK0727";
    String ACK_CODE_728 = "ACK0728";
    String ACK_CODE_729 = "ACK0729";
    String ACK_CODE_730 = "ACK0730";
    String ACK_CODE_731 = "ACK0731";
    String ACK_CODE_732 = "ACK0732";
    String ACK_CODE_733 = "ACK0733";
    String ACK_CODE_734 = "ACK0734";
    String ACK_CODE_735 = "ACK0735";
    String ACK_CODE_736 = "ACK0736";
    String ACK_CODE_737 = "ACK0737";
    String ACK_CODE_738 = "ACK0738";
    String ACK_CODE_739 = "ACK0739";
    String ACK_CODE_740 = "ACK0740";
    String ACK_CODE_741 = "ACK0741";
    String ACK_CODE_742 = "ACK0742";
    String ACK_CODE_743 = "ACK0743";
    String ACK_CODE_744 = "ACK0744";
    String ACK_CODE_745 = "ACK0745";
    String ACK_CODE_746 = "ACK0746";
    String ACK_CODE_747 = "ACK0747";
    String ACK_CODE_748 = "ACK0748";
    String ACK_CODE_749 = "ACK0749";
    String ACK_CODE_750 = "ACK0750";
    String ACK_CODE_751 = "ACK0751";
    String ACK_CODE_752 = "ACK0752";
    String ACK_CODE_753 = "ACK0753";
    String ACK_CODE_754 = "ACK0754";
    String ACK_CODE_755 = "ACK0755";
    String ACK_CODE_756 = "ACK0756";
    String ACK_CODE_757 = "ACK0757";
    String ACK_CODE_758 = "ACK0758";
    String ACK_CODE_759 = "ACK0759";
    String ACK_CODE_760 = "ACK0760";
    String ACK_CODE_761 = "ACK0761";
    String ACK_CODE_762 = "ACK0762";
    String ACK_CODE_763 = "ACK0763";
    String ACK_CODE_764 = "ACK0764";
    String ACK_CODE_765 = "ACK0765";
    String ACK_CODE_766 = "ACK0766";
    String ACK_CODE_767 = "ACK0767";
    String ACK_CODE_768 = "ACK0768";
    String ACK_CODE_769 = "ACK0769";
    String ACK_CODE_770 = "ACK0770";
    String ACK_CODE_771 = "ACK0771";
    String ACK_CODE_772 = "ACK0772";
    String ACK_CODE_773 = "ACK0773";
    String ACK_CODE_774 = "ACK0774";
    String ACK_CODE_775 = "ACK0775";
    String ACK_CODE_776 = "ACK0776";
    String ACK_CODE_777 = "ACK0777";
    String ACK_CODE_778 = "ACK0778";
    String ACK_CODE_779 = "ACK0779";
    String ACK_CODE_780 = "ACK0780";
    String ACK_CODE_781 = "ACK0781";
    String ACK_CODE_782 = "ACK0782";
    String ACK_CODE_783 = "ACK0783";
    String ACK_CODE_784 = "ACK0784";
    String ACK_CODE_785 = "ACK0785";
    String ACK_CODE_786 = "ACK0786";
    String ACK_CODE_790 = "ACK0790";
    String ACK_CODE_791 = "ACK0791";
    String ACK_CODE_792 = "ACK0792";
    String ACK_CODE_793 = "ACK0793";
    String ACK_CODE_794 = "ACK0794";
    String ACK_CODE_795 = "ACK0795";
    String ACK_CODE_796 = "ACK0796";


    String ACK_CODE_5001 = "ACK5001";
    String ACK_CODE_5002 = "ACK5002";
    String ACK_CODE_5003 = "ACK5003";
    String ACK_CODE_5004 = "ACK5004";
    String ACK_CODE_5005 = "ACK5005";
    String ACK_CODE_5006 = "ACK5006";
    String ACK_CODE_5007 = "ACK5007";
    /* static List<String> getDeviceTypes(Context ctx) {
         return Arrays.asList(
                 ctx.getResources().getString(R.string.device_name_advanced),
                 ctx.getResources().getString(R.string.device_name_intelli),
                 ctx.getResources().getString(R.string.device_name_icu)
         );
     }*/
    //added ack99 here
    class Mapping{
        // key: -ve \\ value: +ve
        static Map<String, String> ackMapping = new LinkedHashMap<>();
        static {
            ackMapping.put(ACK_CODE_0, ACK_CODE_1);
            ackMapping.put(ACK_CODE_32, ACK_CODE_1);
            ackMapping.put(ACK_CODE_33, ACK_CODE_1);
            ackMapping.put(ACK_CODE_5, ACK_CODE_6);
            ackMapping.put(ACK_CODE_7, ACK_CODE_57);
            ackMapping.put(ACK_CODE_8, ACK_CODE_58);
            ackMapping.put(ACK_CODE_9, ACK_CODE_59);
            ackMapping.put(ACK_CODE_10, ACK_CODE_20);
            ackMapping.put(ACK_CODE_11, ACK_CODE_21);
            ackMapping.put(ACK_CODE_12, ACK_CODE_22);
            ackMapping.put(ACK_CODE_13, ACK_CODE_23);
            ackMapping.put(ACK_CODE_14, ACK_CODE_24);
            ackMapping.put(ACK_CODE_15, ACK_CODE_25);
            ackMapping.put(ACK_CODE_16, ACK_CODE_26);
            ackMapping.put(ACK_CODE_17, ACK_CODE_27);
            ackMapping.put(ACK_CODE_18, ACK_CODE_28);
            ackMapping.put(ACK_CODE_19, ACK_CODE_29);
            ackMapping.put(ACK_CODE_30, ACK_CODE_40);
            ackMapping.put(ACK_CODE_31, ACK_CODE_41);
            ackMapping.put(ACK_CODE_34, ACK_CODE_44);
            ackMapping.put(ACK_CODE_35, ACK_CODE_45);
            ackMapping.put(ACK_CODE_36, ACK_CODE_46);
            ackMapping.put(ACK_CODE_37, ACK_CODE_47);
            ackMapping.put(ACK_CODE_38, ACK_CODE_48);
            ackMapping.put(ACK_CODE_39, ACK_CODE_49);
            ackMapping.put(ACK_CODE_60, ACK_CODE_70);
            ackMapping.put(ACK_CODE_61, ACK_CODE_71);
            ackMapping.put(ACK_CODE_62, ACK_CODE_72);
            ackMapping.put(ACK_CODE_80, ACK_CODE_90);
            ackMapping.put(ACK_CODE_81, ACK_CODE_91);


        }
    }


    // HANDSHAKE
    String INFORM_HANDSHAKE = "HS";
    // PING
    String INFORM_PING = "PING";

    List<String> o2CalibrationAck = Arrays.asList(ACK_CODE_0, ACK_CODE_1, ACK_CODE_5, ACK_CODE_6, ACK_CODE_52, ACK_CODE_53, ACK_CODE_54, ACK_CODE_55, ACK_CODE_56);

    // This is list of higher priority alarms but less than unmutable alarms
    List<String> highLevelAcks = Arrays.asList(
            ACK_CODE_12, ACK_CODE_13, ACK_CODE_16, ACK_CODE_17, ACK_CODE_18, ACK_CODE_19,
            ACK_CODE_30
    );

    // This list provides alarm color code scheme
    List<String> dangerColoredAcks = Arrays.asList(
            ACK_CODE_5,
            ACK_CODE_10, ACK_CODE_11, ACK_CODE_12, ACK_CODE_13, ACK_CODE_14,  ACK_CODE_15, ACK_CODE_16, ACK_CODE_17, ACK_CODE_18, ACK_CODE_19,
            ACK_CODE_30, ACK_CODE_31, ACK_CODE_35,
            ACK_CODE_50, ACK_CODE_62, ACK_CODE_68, ACK_CODE_80
    );

    // This is list of Highest priority alarms which cannot be muted
    List<String> unmutedLevelAcks = Arrays.asList(
            ACK_CODE_5,
            ACK_CODE_31
    );

    List<String> noSoundAcks = Arrays.asList(
            ACK_CODE_0,
            ACK_CODE_36
    );


/*     static void Limiters(int minLimit ,int maxLimit) {
        switch(minLimit){
            case
        }
     }*/


    /**
     *
     * @param ctx provides the context access
     * @param modeCode provides the current operational mode
     * @return map of encoder constraints with parameter labels
     */

    static Map<String, ControlParameterLimit> getParameterEncoderOptions(Context ctx, final int modeCode){

        final PreferenceManager prefManager = new PreferenceManager(ctx);

        final boolean isPediatric = prefManager!=null && prefManager.readPediatricStatus();

        float minInhaleTime = isPediatric ? Float.valueOf(ctx.getResources().getString(R.string.min_inhale_time_ped)) : Float.valueOf(ctx.getResources().getString(R.string.min_inhale_time));
        float maxInhaleTime = isPediatric ? Float.valueOf(ctx.getResources().getString(R.string.max_inhale_time_ped)) : Float.valueOf(ctx.getResources().getString(R.string.max_inhale_time));

        float minTLow = isPediatric ? Float.valueOf(ctx.getResources().getString(R.string.min_inhale_time_ped)) : Float.valueOf(ctx.getResources().getString(R.string.min_tlow));
        float maxTLow = isPediatric ? Float.valueOf(ctx.getResources().getString(R.string.max_inhale_time_ped)) : Float.valueOf(ctx.getResources().getString(R.string.max_tlow));

        float minIERatio = Float.valueOf(ctx.getResources().getString(R.string.min_ie_ratio));
        float maxIERatio = Float.valueOf(ctx.getResources().getString(R.string.max_ie_ratio));

        int minRR = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.min_rr_ped)) : Integer.valueOf(ctx.getResources().getString(R.string.min_rr));
        int maxRR = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.max_rr_ped)) : Integer.valueOf(ctx.getResources().getString(R.string.max_rr));

        int minPip = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.min_pip_ped)) : Integer.valueOf(ctx.getResources().getString(R.string.min_pip));
        int maxPip = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.max_pip_ped)) : Integer.valueOf(ctx.getResources().getString(R.string.max_pip));

        int minPeep = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.min_peep_ped)) : Integer.valueOf(ctx.getResources().getString(R.string.min_peep));
        int maxPeep = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.max_peep_ped)) : Integer.valueOf(ctx.getResources().getString(R.string.max_peep));

        int minPplat = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.min_pplat_ped)) : Integer.valueOf(ctx.getResources().getString(R.string.min_pplat));
        int maxPplat = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.max_pplat_ped)) : Integer.valueOf(ctx.getResources().getString(R.string.max_pplat));

        int minFio2 = Integer.parseInt(ctx.getResources().getString(R.string.min_fio2));
        int maxFio2 = Integer.parseInt(ctx.getResources().getString(R.string.max_fio2));

        int minSupportPressure = Integer.parseInt(ctx.getResources().getString(R.string.min_support_pressure));
        int maxSupportPressure = Integer.parseInt(ctx.getResources().getString(R.string.max_support_pressure));

        int minSlope = Integer.parseInt(ctx.getResources().getString(R.string.min_slope));
        int maxSlope = Integer.parseInt(ctx.getResources().getString(R.string.max_slope));

        int minTexp = Integer.parseInt(ctx.getResources().getString(R.string.min_texp));
        int maxTexp = Integer.parseInt(ctx.getResources().getString(R.string.max_texp));

        int minVti = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.min_vti_ped)) : Integer.valueOf(ctx.getResources().getString(R.string.min_vti));
        int maxVti = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.max_vti_ped)) : Integer.valueOf(ctx.getResources().getString(R.string.max_vti));

        int minPeakFlow = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.min_peakflow_ped)) : Integer.valueOf(ctx.getResources().getString(R.string.min_peakflow));
        int maxPeakFlow = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.max_peakflow_ped)) : Integer.valueOf(ctx.getResources().getString(R.string.max_peakflow));

        float minTrigFlow = isPediatric ? Float.valueOf(ctx.getResources().getString(R.string.min_trigflow_ped)) : Float.valueOf(ctx.getResources().getString(R.string.min_trigflow));
        float maxTrigFlow = isPediatric ? Float.valueOf(ctx.getResources().getString(R.string.max_trigflow_ped)) : Float.valueOf(ctx.getResources().getString(R.string.max_trigflow));


        // Mode wise validations
        if(isValidVentilatorMode(ctx, modeCode)) {
            if (Configs.getModeCategory(modeCode) == Configs.MODE_NIV) {
                final boolean isBpap = modeCode == Configs.MODE_NIV_BPAP;
                final int defaultMinPplatRes = isBpap ? R.string.min_ipap_bpap : R.string.min_ipap;
                final int defaultMaxPplatRes = isBpap ? R.string.max_ipap_bpap : R.string.max_ipap;

                minPplat =  Integer.parseInt(ctx.getResources().getString(isPediatric ? R.string.min_ipap_ped : defaultMinPplatRes));
                maxPplat = Integer.parseInt(ctx.getResources().getString(isPediatric ? R.string.max_ipap_ped : defaultMaxPplatRes)) ;

                minPeep = Float.valueOf(ctx.getResources().getString(isPediatric ? R.string.min_epap_ped : R.string.min_epap) ).intValue();
                maxPeep = Float.valueOf(ctx.getResources().getString(isPediatric ? R.string.max_epap_ped : R.string.max_epap) ).intValue();

                minFio2 = Integer.parseInt(ctx.getResources().getString(R.string.min_fio2_niv));
                maxFio2 = Integer.parseInt(ctx.getResources().getString(R.string.max_fio2_niv));
            }

            if (modeCode == Configs.MODE_PC_PSV) {
                minPplat = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.min_pplat_ped_psv)) : Integer.valueOf(ctx.getResources().getString(R.string.min_pplat_psv));
                maxPplat = isPediatric ? Integer.valueOf(ctx.getResources().getString(R.string.max_pplat_ped_psv)) : Integer.valueOf(ctx.getResources().getString(R.string.max_pplat_psv));

                minRR = Integer.parseInt(ctx.getResources().getString(isPediatric ? R.string.min_rr_ped : R.string.min_rr_psv )) ;
                maxRR =  Integer.parseInt(ctx.getResources().getString(isPediatric ? R.string.max_rr_ped : R.string.max_rr_psv ));
            }

            if (modeCode == Configs.MODE_PC_SPONT_DUMMY) {

                minRR = Integer.parseInt(ctx.getResources().getString(isPediatric ? R.string.min_rr_ped : R.string.min_rr_spont )) ;
                maxRR =  Integer.parseInt(ctx.getResources().getString(isPediatric ? R.string.max_rr_ped : R.string.max_rr_spont ));

                minFio2 = Integer.parseInt(ctx.getResources().getString(R.string.min_fio2_niv));
                maxFio2 = Integer.parseInt(ctx.getResources().getString(R.string.max_fio2_niv));
            }

            if (isVentilatorModeNeoNatal(ctx, modeCode)) {
                minRR = Integer.parseInt(ctx.getResources().getString(R.string.min_rr_neonat));
                maxRR = Integer.parseInt(ctx.getResources().getString(R.string.max_rr_neonat));

                minVti = Integer.parseInt(ctx.getResources().getString(R.string.min_vti_neonat));
                maxVti = Integer.parseInt(ctx.getResources().getString(R.string.max_vti_neonat));

                minInhaleTime = Float.parseFloat(ctx.getResources().getString(R.string.min_inhale_time_neonat));
                maxInhaleTime = Float.parseFloat(ctx.getResources().getString(R.string.max_inhale_time_neonat));

                minIERatio = Float.parseFloat(ctx.getResources().getString(R.string.min_ie_ratio));
                maxIERatio = Float.parseFloat(ctx.getResources().getString(R.string.max_ie_ratio));
            }
        }

        int minRRApnea = Integer.parseInt(ctx.getResources().getString(R.string.min_apnea_rr));
        int maxRRApnea = Integer.parseInt(ctx.getResources().getString(R.string.max_apnea_rr));

        int minTApnea = Integer.parseInt(ctx.getResources().getString(R.string.min_apnea_time));
        int maxTApnea = Integer.parseInt(ctx.getResources().getString(R.string.max_apnea_time));

        int minVTApnea = Integer.parseInt(ctx.getResources().getString(R.string.min_apnea_vt));
        int maxVTApnea = Integer.parseInt(ctx.getResources().getString(R.string.max_apnea_vt));

        float minTrigFlowApnea = Float.parseFloat(ctx.getResources().getString(R.string.min_apnea_trigflow));
        float maxTrigFlowApnea = Float.parseFloat(ctx.getResources().getString(R.string.max_apnea_trigflow));



        Map<String, ControlParameterLimit> paramsScaleBindingMap = new HashMap<>();

        paramsScaleBindingMap.put(LBL_PIP, new ControlParameterLimit(minPip, maxPip, 1));
        paramsScaleBindingMap.put(LBL_VTI, new ControlParameterLimit(minVti, maxVti,10));
        paramsScaleBindingMap.put(LBL_PEEP, new ControlParameterLimit(minPeep, maxPeep,1));
        paramsScaleBindingMap.put(LBL_PPLAT, new ControlParameterLimit(minPplat, maxPplat,1));
        paramsScaleBindingMap.put(LBL_RR, new ControlParameterLimit(minRR, maxRR,1));
        paramsScaleBindingMap.put(LBL_TINSP, new ControlParameterLimit(minInhaleTime, maxInhaleTime,0.1f));
        paramsScaleBindingMap.put(LBL_IE_RATIO, new ControlParameterLimit(minIERatio, maxIERatio,1));  // Inhale time mapped to IE Ratio
        paramsScaleBindingMap.put(LBL_PEAK_FLOW, new ControlParameterLimit(minPeakFlow, maxPeakFlow,1));
        paramsScaleBindingMap.put(LBL_TRIG_FLOW, new ControlParameterLimit(minTrigFlow, maxTrigFlow,0.5f));
        paramsScaleBindingMap.put(LBL_FIO2, new ControlParameterLimit(minFio2, maxFio2,1));
        paramsScaleBindingMap.put(LBL_SUPPORT_PRESSURE, new ControlParameterLimit(minSupportPressure, maxSupportPressure,1));
        paramsScaleBindingMap.put(LBL_SLOPE, new ControlParameterLimit(minSlope, maxSlope,5));
        paramsScaleBindingMap.put(LBL_TLOW, new ControlParameterLimit(minTLow, maxTLow,0.1f));
        paramsScaleBindingMap.put(LBL_TEXP, new ControlParameterLimit(minTexp, maxTexp,5));

        paramsScaleBindingMap.put(LBL_APNEA_RR, new ControlParameterLimit(minRRApnea, maxRRApnea,1));
        paramsScaleBindingMap.put(LBL_TAPNEA, new ControlParameterLimit(minTApnea, maxTApnea,1));
        paramsScaleBindingMap.put(LBL_APNEA_VT, new ControlParameterLimit(minVTApnea, maxVTApnea,10));
        paramsScaleBindingMap.put(LBL_APNEA_TRIG_FLOW, new ControlParameterLimit(minTrigFlowApnea, maxTrigFlowApnea,0.5f));


        return paramsScaleBindingMap;
    }

 /*   static String getPositiveAckOf(String antiAckCode){
        if(antiAckCode != null && Mapping.ackMapping.containsKey(antiAckCode)) {
            return Mapping.ackMapping.get(antiAckCode);
        }
        return null;
    }*/



    static List<List<ControlParameterModel>> getAllControlParameterLists(Context ctx, final int modeCode) throws InvalidModeException{

        if(!isValidVentilatorMode(ctx, modeCode)) throw new InvalidModeException();
        PreferenceManager prefs = new PreferenceManager(ctx);


        List<ControlParameterModel> basicParameters = new ArrayList<>();
        List<ControlParameterModel> advancedParameters = new ArrayList<>();
        List<ControlParameterModel> apneaParameters = new ArrayList<>();



        //getting value from the preference with the modecode
        Map<String, ControlParameterLimit> map = getParameterEncoderOptions(ctx, modeCode);

        Double vtiUpperLimit = null;
        Double vtiLowerLimit= null;
        Double vtiStep = null;

        Double peepUpperLimit= null;
        Double peepLowerLimit= null;
        Double peepStep = null;

        Double trigUpperLimit= null;
        Double trigLowerLimit= null;
        Double trigStep= null;

        Double pipUpperLimit= null;
        Double pipLowerLimit= null;
        Double pipStep = null;

        Double rrUpperLimit= null;
        Double rrLowerLimit= null;
        Double rrStep = null;

        Double tinspUpperLimit= null;
        Double tinspLowerLimit= null;
        Double tinspStep = null;

        Double fioUpperLimit= null;
        Double fioLowerLimit= null;
        Double fioStep = null;

        Double peakFlowUpperLimit= null;
        Double peakFlowLowerLimit= null;
        Double peakFlowStep= null;

        Double platUpperLimit= null;
        Double platLowerLimit= null;
        Double platStep= null;

        Double supportPressureUpperLimit= null;
        Double supportPressureLowerLimit= null;
        Double supportPressureStep= null;

        Double slopeUpperLimit= null;
        Double slopeLowerLimit= null;
        Double slopeStep= null;

        Double tLowUpperLimit= null;
        Double tLowLowerLimit= null;
        Double tLowStep= null;

        Double tExpUpperLimit= null;
        Double tExpLowerLimit= null;
        Double tExpStep= null;

        // APNEA PARAMETERS
        Double rrApneaUpperLimit= null;
        Double rrApneaLowerLimit= null;
        Double rrApneaStep= null;

        Double tApneaUpperLimit= null;
        Double tApneaLowerLimit= null;
        Double tApneaStep= null;

        Double vtApneaUpperLimit= null;
        Double vtApneaLowerLimit= null;
        Double vtApneaStep= null;

        Double trigApneaUpperLimit= null;
        Double trigApneaLowerLimit= null;
        Double trigApneaStep= null;


        for(Map.Entry<String, ControlParameterLimit> valueTile : map.entrySet()) {
            Log.i("limits", valueTile.getKey());

            switch (valueTile.getKey()) {


                case LBL_PEEP :
                    peepUpperLimit = (double) valueTile.getValue().getMaxValue();
                    peepLowerLimit = (double) valueTile.getValue().getMinValue();
                    peepStep = (double) valueTile.getValue().getValuePerRotation();
                    break;


                case LBL_PPLAT :
                    platUpperLimit = (double) valueTile.getValue().getMaxValue();
                    platLowerLimit = (double) valueTile.getValue().getMinValue();
                    platStep = (double) valueTile.getValue().getValuePerRotation();
                    break;

                case LBL_RR :
                    rrUpperLimit = (double) valueTile.getValue().getMaxValue();
                    rrLowerLimit = (double) valueTile.getValue().getMinValue();
                    rrStep = (double) valueTile.getValue().getValuePerRotation();
                    break;

                case LBL_VTI :
                    vtiUpperLimit = (double) valueTile.getValue().getMaxValue();
                    vtiLowerLimit = (double) valueTile.getValue().getMinValue();
                    vtiStep = (double) valueTile.getValue().getValuePerRotation();
                    break;

                case LBL_TRIG_FLOW :
                    trigUpperLimit = (double) valueTile.getValue().getMaxValue();
                    trigLowerLimit = (double) valueTile.getValue().getMinValue();
                    trigStep = (double) valueTile.getValue().getValuePerRotation();
                    break;

                case LBL_PIP :
                    pipUpperLimit = (double) valueTile.getValue().getMaxValue();
                    pipLowerLimit = (double) valueTile.getValue().getMinValue();
                    pipStep = (double) valueTile.getValue().getValuePerRotation();
                    break;

                case LBL_TINSP :
                    tinspUpperLimit = (double) valueTile.getValue().getMaxValue();
                    tinspLowerLimit = (double) valueTile.getValue().getMinValue();
                    tinspStep = (double) valueTile.getValue().getValuePerRotation();
                    break;

                case LBL_FIO2 :
                    fioUpperLimit = (double) valueTile.getValue().getMaxValue();
                    fioLowerLimit = (double) valueTile.getValue().getMinValue();
                    fioStep = (double) valueTile.getValue().getValuePerRotation();
                    break;

                case LBL_PEAK_FLOW :
                    peakFlowUpperLimit = (double) valueTile.getValue().getMaxValue();
                    peakFlowLowerLimit = (double) valueTile.getValue().getMinValue();
                    peakFlowStep = (double) valueTile.getValue().getValuePerRotation();
                    break;

                case LBL_SUPPORT_PRESSURE :
                    supportPressureUpperLimit = (double) valueTile.getValue().getMaxValue();
                    supportPressureLowerLimit = (double) valueTile.getValue().getMinValue();
                    supportPressureStep = (double) valueTile.getValue().getValuePerRotation();
                    break;

                case LBL_SLOPE :
                    slopeUpperLimit = (double) valueTile.getValue().getMaxValue();
                    slopeLowerLimit = (double) valueTile.getValue().getMinValue();
                    slopeStep = (double) valueTile.getValue().getValuePerRotation();
                    break;

                case LBL_TLOW :
                    tLowUpperLimit = (double) valueTile.getValue().getMaxValue();
                    tLowLowerLimit = (double) valueTile.getValue().getMinValue();
                    tLowStep = (double) valueTile.getValue().getValuePerRotation();
                    break;

                case LBL_TEXP :
                    tExpUpperLimit = (double) valueTile.getValue().getMaxValue();
                    tExpLowerLimit = (double) valueTile.getValue().getMinValue();
                    tExpStep = (double) valueTile.getValue().getValuePerRotation();
                    break;

                case LBL_APNEA_RR :
                    rrApneaUpperLimit = (double) valueTile.getValue().getMaxValue();
                    rrApneaLowerLimit = (double) valueTile.getValue().getMinValue();
                    rrApneaStep = (double) valueTile.getValue().getValuePerRotation();
                    Log.i("BACKUPCHECK", "Step = " + rrApneaStep + " | Max = " + rrApneaUpperLimit + " | Min = " + rrApneaLowerLimit);

                    break;

                case LBL_TAPNEA :
                    tApneaUpperLimit = (double) valueTile.getValue().getMaxValue();
                    tApneaLowerLimit = (double) valueTile.getValue().getMinValue();
                    tApneaStep = (double) valueTile.getValue().getValuePerRotation();
                    Log.i("BACKUPCHECK", "Step = " + tApneaStep + " | Max = " + tApneaUpperLimit + " | Min = " + tApneaLowerLimit);

                    break;

                case LBL_APNEA_VT :
                    vtApneaUpperLimit = (double) valueTile.getValue().getMaxValue();
                    vtApneaLowerLimit = (double) valueTile.getValue().getMinValue();
                    vtApneaStep = (double) valueTile.getValue().getValuePerRotation();
                    Log.i("BACKUPCHECK", "Step = " + vtApneaStep + " | Max = " + vtApneaUpperLimit + " | Min = " + vtApneaLowerLimit);

                    break;

                case LBL_APNEA_TRIG_FLOW :
                    trigApneaUpperLimit = (double) valueTile.getValue().getMaxValue();
                    trigApneaLowerLimit = (double) valueTile.getValue().getMinValue();
                    trigApneaStep = (double) valueTile.getValue().getValuePerRotation();
                    Log.i("BACKUPCHECK", "Step = " + trigApneaStep + " | Max = " + trigApneaUpperLimit + " | Min = " + trigApneaLowerLimit);

                    break;


            }

        }

        ControlParameterModel peep = new ControlParameterModel(
                LBL_PEEP,
                ctx.getResources().getString(R.string.peep),
                supportPrecision(LBL_PEEP,prefs.readPEEP()),
                ctx.getResources().getString(R.string.hint_cmH2o),
                peepUpperLimit,
                peepLowerLimit,
                peepStep
        );
        basicParameters.add(peep);


        ControlParameterModel pPlat = new ControlParameterModel(
                LBL_PPLAT,
                ctx.getResources().getString(R.string.pinsp),
                supportPrecision(LBL_PPLAT,prefs.readPplat()),
                ctx.getResources().getString(R.string.hint_cmH2o),
                platUpperLimit,
                platLowerLimit,
                platStep
        );
        basicParameters.add(pPlat);

        ControlParameterModel rr = new ControlParameterModel(
                LBL_RR,
                ctx.getResources().getString(R.string.respiratory_rate),
                supportPrecision(LBL_RR,prefs.readRR()),
                ctx.getResources().getString(R.string.hint_bpm),
                rrUpperLimit,
                rrLowerLimit,
                rrStep
        );
        basicParameters.add(rr);

        ControlParameterModel vti = new ControlParameterModel(
                LBL_VTI,
                ctx.getResources().getString(R.string.vti),
                supportPrecision(LBL_VTI,prefs.readVti()),
                ctx.getResources().getString(R.string.hint_ml),
                vtiUpperLimit,
                vtiLowerLimit,
                vtiStep
        );
        basicParameters.add(vti);

        ControlParameterModel triggerFlow = new ControlParameterModel(
                LBL_TRIG_FLOW,
                ctx.getResources().getString(R.string.trigger_flow),
                String.valueOf(prefs.readTrigFlow()),
                ctx.getResources().getString(R.string.hint_l_min),
                trigUpperLimit,
                trigLowerLimit,
                trigStep
        );
        basicParameters.add(triggerFlow);
//        advancedParameters.add(triggerFlow);



        ControlParameterModel pip = new ControlParameterModel(
                LBL_PIP,
                ctx.getResources().getString(R.string.plimit),
                supportPrecision(LBL_PIP,prefs.readPip()),
                ctx.getResources().getString(R.string.hint_cmH2o),
                pipUpperLimit,
                pipLowerLimit,
                pipStep
        );
//        basicParameters.add(pip);
        advancedParameters.add(pip);


        ControlParameterModel tInsp = new ControlParameterModel(
                LBL_TINSP,
                ctx.getResources().getString(R.string.inhale_time),
                String.valueOf(prefs.readTinsp()),
                ctx.getResources().getString(R.string.hint_sec),
                tinspUpperLimit,
                tinspLowerLimit,
                tinspStep
        );
        basicParameters.add(tInsp);


        ControlParameterModel fio2 = new ControlParameterModel(
                LBL_FIO2,
                ctx.getResources().getString(R.string.fio2),
                supportPrecision(LBL_FIO2,prefs.readFiO2()),
                ctx.getResources().getString(R.string.hint_percentage),
                fioUpperLimit,
                fioLowerLimit,
                fioStep
        );
        basicParameters.add(fio2);

        ControlParameterModel supportPressure = new ControlParameterModel(
                LBL_SUPPORT_PRESSURE,
                ctx.getResources().getString(R.string.support_pressure),
                supportPrecision(LBL_SUPPORT_PRESSURE,prefs.readSupportPressure()),
                ctx.getResources().getString(R.string.hint_cmH2o),
                supportPressureUpperLimit,
                supportPressureLowerLimit,
                supportPressureStep
        );
        basicParameters.add(supportPressure);


        ControlParameterModel slope = new ControlParameterModel(
                LBL_SLOPE,
                ctx.getResources().getString(R.string.slope),
                supportPrecision(LBL_SLOPE, prefs.readSlope()),
                ctx.getResources().getString(R.string.hint_percentage),
                slopeUpperLimit,
                slopeLowerLimit,
                slopeStep
        );
        basicParameters.add(slope);


        ControlParameterModel tLow = new ControlParameterModel(
                LBL_TLOW,
                ctx.getResources().getString(R.string.tLow),
                String.valueOf(prefs.readTlow()),
                ctx.getResources().getString(R.string.hint_sec),
                tLowUpperLimit,
                tLowLowerLimit,
                tLowStep
        );
        basicParameters.add(tLow);

        ControlParameterModel tExp = new ControlParameterModel(
                LBL_TEXP,
                ctx.getResources().getString(R.string.tExp),
                supportPrecision(LBL_TEXP,prefs.readTexp()),
                ctx.getResources().getString(R.string.hint_percentage),
                tExpUpperLimit,
                tExpLowerLimit,
                tExpStep
        );
//        basicParameters.add(tExp);
        advancedParameters.add(tExp);



        // APNEA PARAMETERS
        ControlParameterModel rrApnea = new ControlParameterModel(
                LBL_APNEA_RR,
                ctx.getResources().getString(R.string.apnea_respiratory_rate),
                supportPrecision(LBL_APNEA_RR,prefs.readRRApnea()),
                ctx.getResources().getString(R.string.hint_bpm),
                rrApneaUpperLimit,
                rrApneaLowerLimit,
                rrApneaStep
        );
        apneaParameters.add(rrApnea);

        ControlParameterModel tApnea = new ControlParameterModel(
                LBL_TAPNEA,
                ctx.getResources().getString(R.string.apnea_time),
                String.valueOf(prefs.readTApnea().intValue()),
                ctx.getResources().getString(R.string.hint_sec),
                tApneaUpperLimit,
                tApneaLowerLimit,
                tApneaStep
        );
        apneaParameters.add(tApnea);

        ControlParameterModel vtApnea = new ControlParameterModel(
                LBL_APNEA_VT,
                ctx.getResources().getString(R.string.apnea_vt),
                supportPrecision(LBL_APNEA_VT,prefs.readVtApnea()),
                ctx.getResources().getString(R.string.hint_cmH2o),
                vtApneaUpperLimit,
                vtApneaLowerLimit,
                vtApneaStep
        );
        apneaParameters.add(vtApnea);

        ControlParameterModel triggerFlowApnea = new ControlParameterModel(
                LBL_APNEA_TRIG_FLOW,
                ctx.getResources().getString(R.string.apnea_trigger_flow),
                String.valueOf(prefs.readTrigFlowApnea()),
                ctx.getResources().getString(R.string.hint_l_min),
                trigApneaUpperLimit,
                trigApneaLowerLimit,
                trigApneaStep
        );
        apneaParameters.add(triggerFlowApnea);

        boolean isCmvMode = (modeCode == MODE_VCV_CMV || modeCode == MODE_PC_CMV);
        List<List<ControlParameterModel>> parameters = new ArrayList<>();
        parameters.add(basicParameters);
        parameters.add(advancedParameters);
        if(!isCmvMode) parameters.add(apneaParameters);

        return parameters;

    }


    static String supportPrecision(String lbl, String value){
        try {
            return supportPrecision(lbl, Float.parseFloat(value));
        } catch (Exception e) {
            return value;
        }
    }

    static String supportPrecision(String lbl, Float value){
        if(isDecimalSupported(lbl)) return String.format("%.1f", value);
        else return String.valueOf(value.intValue());
    }

    static List<List<ControlParameterModel>> filterControlParameterViaMode(Context ctx, final int modeCode, List<ControlParameterModel> allParameters) throws InvalidModeException {

        if(!isValidVentilatorMode(ctx, modeCode)) throw new InvalidModeException();

        ControlParameterModel peep = null;
        ControlParameterModel triggerFlow = null;
        ControlParameterModel triggerFlowApnea = null;
        ControlParameterModel pPlat = null;
        ControlParameterModel vti = null;
        ControlParameterModel vtApnea = null;
        ControlParameterModel pip = null;
        ControlParameterModel rr = null;
        ControlParameterModel rrApnea = null;
        ControlParameterModel tInsp = null;
        ControlParameterModel tApnea = null;
        ControlParameterModel fio2 = null;
        ControlParameterModel supportPressure = null;
        ControlParameterModel slope = null;
        ControlParameterModel tLow = null;
        ControlParameterModel tExp = null;

        for (ControlParameterModel param : allParameters) {
            switch (param.getVentKey()){

                case LBL_PEEP:
                    peep = param;
                    break;

                case LBL_TRIG_FLOW:
                    triggerFlow = param;
                    break;

                case LBL_PPLAT:
                    pPlat = param;
                    break;

                case LBL_VTI:
                    vti = param;
                    break;

                case LBL_PIP:
                    pip = param;
                    break;

                case LBL_RR:
                    rr = param;
                    break;

                case LBL_TINSP:
                    tInsp = param;
                    break;

                case LBL_FIO2:
                    fio2 = param;
                    break;

                case LBL_SUPPORT_PRESSURE:
                    supportPressure = param;
                    break;

                case LBL_SLOPE:
                    slope = param;
                    break;

                case LBL_TLOW:
                    tLow = param;
                    break;

                case LBL_TEXP:
                    tExp = param;
                    break;

                case LBL_APNEA_RR:
                    rrApnea = param;
                    break;

                case LBL_TAPNEA:
                    tApnea = param;
                    break;

                case LBL_APNEA_VT:
                    vtApnea = param;
                    break;

                case LBL_APNEA_TRIG_FLOW:
                    triggerFlowApnea = param;
                    break;

            }
        }

        List<ControlParameterModel> basicParameters = new ArrayList<>();

        List<ControlParameterModel> advancedParameters = new ArrayList<>();

        List<ControlParameterModel> apneaParameters = new ArrayList<>();



        apneaParameters.add(rrApnea);
        apneaParameters.add(tApnea);
        apneaParameters.add(vtApnea);
        apneaParameters.add(triggerFlowApnea);

        switch (modeCode) {
            case MODE_VCV_CMV :

                if(fio2 != null) basicParameters.add(fio2);
                if(vti != null) basicParameters.add(vti);
                if(tInsp != null) basicParameters.add(tInsp);
                if(pip != null) advancedParameters.add(pip);
                //if(pip != null) basicParameters.add(pip);
                if(rr != null) basicParameters.add(rr);
                if(peep != null) basicParameters.add(peep);

                break;


            case MODE_VCV_ACV :

                if(fio2 != null) basicParameters.add(fio2);
                if(vti != null) basicParameters.add(vti);
                if(tInsp != null) basicParameters.add(tInsp);
               // if(pip != null) basicParameters.add(pip);
                if(pip != null) advancedParameters.add(pip);
                if(rr != null) basicParameters.add(rr);
               // if(triggerFlow != null) basicParameters.add(triggerFlow);

                if(triggerFlow != null) advancedParameters.add(triggerFlow);
                if(peep != null) basicParameters.add(peep);
                break;


            case MODE_VCV_SIMV :

                if(fio2 != null) basicParameters.add(fio2);
                if(vti != null) basicParameters.add(vti);
                if(tInsp != null) basicParameters.add(tInsp);
                if(rr != null) basicParameters.add(rr);
                if(pip != null) advancedParameters.add(pip);
                if(triggerFlow != null) basicParameters.add(triggerFlow);
                //if(triggerFlow != null) advancedParameters.add(triggerFlow);
                if(peep != null) basicParameters.add(peep);
                if(supportPressure != null) basicParameters.add(supportPressure);
                if(slope != null) basicParameters.add(slope);
                //if(tExp != null) basicParameters.add(tExp);

                if(tExp != null) advancedParameters.add(tExp);
                break;


            case MODE_PC_CMV :

                if(fio2 != null) basicParameters.add(fio2);
                if(tInsp != null) basicParameters.add(tInsp);
                if(rr != null) basicParameters.add(rr);
                if(pip != null) advancedParameters.add(pip);
                if(peep != null) basicParameters.add(peep);
                if(slope != null) basicParameters.add(slope);
                //if(pip != null) basicParameters.add(pip);
                if(pPlat != null) basicParameters.add(pPlat);
                break;


            case MODE_PC_SIMV :

                if(fio2 != null) basicParameters.add(fio2);
                if(tInsp != null) basicParameters.add(tInsp);
                if(rr != null) basicParameters.add(rr);
                if(triggerFlow != null) basicParameters.add(triggerFlow);
               // if(triggerFlow != null) advancedParameters.add(triggerFlow);
                if(peep != null) basicParameters.add(peep);
                if(supportPressure != null) basicParameters.add(supportPressure);
                if(slope != null) basicParameters.add(slope);
               // if(pip != null) basicParameters.add(pip);
                if(pip != null) advancedParameters.add(pip);
                if(pPlat != null) basicParameters.add(pPlat);
               // if(tExp != null) basicParameters.add(tExp);
                if(tExp != null) advancedParameters.add(tExp);
                break;



            case MODE_PC_AC :

                if(fio2 != null) basicParameters.add(fio2);
                if(tInsp != null) basicParameters.add(tInsp);
                if(rr != null) basicParameters.add(rr);
                if(triggerFlow != null) basicParameters.add(triggerFlow);
               // if(triggerFlow != null) advancedParameters.add(triggerFlow);
                if(peep != null) basicParameters.add(peep);
                if(supportPressure != null) basicParameters.add(supportPressure);
                if(slope != null) basicParameters.add(slope);
                if(pPlat != null) basicParameters.add(pPlat);
                break;


            case MODE_PC_PSV :

                if(fio2 != null) basicParameters.add(fio2);
                if(tInsp != null) basicParameters.add(tInsp);
                if(rr != null) basicParameters.add(rr);
                if(triggerFlow != null) basicParameters.add(triggerFlow);
               // if(triggerFlow != null) advancedParameters.add(triggerFlow);
                if(peep != null) basicParameters.add(peep);
                if(slope != null) basicParameters.add(slope);
               // if(pip != null) basicParameters.add(pip);
                if(pip != null) advancedParameters.add(pip);
                if(pPlat != null) basicParameters.add(pPlat);
               // if(tExp != null) basicParameters.add(tExp);
                if(tExp != null) advancedParameters.add(tExp);
                break;


            case MODE_PC_APRV :

                if(fio2 != null) basicParameters.add(fio2);
                if(slope != null) basicParameters.add(slope);
                if(pPlat != null) basicParameters.add(pPlat);
                if(tInsp != null) basicParameters.add(tInsp);
                if(peep != null) basicParameters.add(peep);
                if(tLow != null) basicParameters.add(tLow);
                break;



            case MODE_AUTO_VENTILATION :

                if(fio2 != null) basicParameters.add(fio2);
                if(vti != null) basicParameters.add(vti);
                if(tInsp != null) basicParameters.add(tInsp);
                if(rr != null) basicParameters.add(rr);
               // if(triggerFlow != null) advancedParameters.add(triggerFlow);
                if(triggerFlow != null) basicParameters.add(triggerFlow);
                if(peep != null) basicParameters.add(peep);
                if(slope != null) basicParameters.add(slope);
                //if(pip != null) basicParameters.add(pip);
                if(pip != null) advancedParameters.add(pip);

                break;


            case MODE_NIV_BPAP :

                if(fio2 != null) basicParameters.add(fio2);
                if(tInsp != null) basicParameters.add(tInsp);
                if(rr != null) basicParameters.add(rr);
               // if(triggerFlow != null) basicParameters.add(triggerFlow);
                if(triggerFlow != null) advancedParameters.add(triggerFlow);
                if(pip != null) advancedParameters.add(pip);
                if(peep != null) basicParameters.add(peep);
                if(supportPressure != null) basicParameters.add(supportPressure);
                if(slope != null) basicParameters.add(slope);
                if(pPlat != null) basicParameters.add(pPlat);
//                parameters.add(tExp);
                break;



            case MODE_NIV_CPAP :

                if(fio2 != null) basicParameters.add(fio2);
//                parameters.add(vti);
                if(tInsp != null) basicParameters.add(tInsp);
//                parameters.add(rr);
                if(triggerFlow != null) basicParameters.add(triggerFlow);
              //  if(triggerFlow != null) advancedParameters.add(triggerFlow);
                if(peep != null) basicParameters.add(peep);
               // if(tExp != null) basicParameters.add(tExp);
                if(tExp != null) advancedParameters.add(tExp);
//                parameters.add(pPlat);
                if(supportPressure != null) basicParameters.add(supportPressure);
                if(slope != null) basicParameters.add(slope);
                break;



        }

        boolean isCmvMode = (modeCode == MODE_VCV_CMV || modeCode == MODE_PC_CMV);

        List<List<ControlParameterModel>> parameters = new ArrayList<>();
        parameters.add(basicParameters);
        parameters.add(advancedParameters);

        if(!isCmvMode) parameters.add(apneaParameters);
        return parameters;
    }


    static String[] getAckFromAntiAck(String antiAckCode){


        String[] keyValue= new String[10];
        int index=0;
        if(antiAckCode != null ) {
            for (Map.Entry<String, String> i :Mapping.ackMapping.entrySet()) {
                if (i.getValue().equals(antiAckCode)) {
                    keyValue[index++]=i.getKey();
                    System.out.println(i.getKey());
                    // break;
                }

            }

            return keyValue;
        }
        return null;
    }


    /*
     * Returns the category / control type of ventilator mode
     */
    static int getModeCategory(VentMode mode) {
        return (mode != null)? getModeCategory(mode.getModeCode()) : 0;
    }

    static int getModeCategory(int modeCode){
        switch (modeCode / 10){
            case 1: return modeCode == Configs.MODE_PC_SPONT_DUMMY ? MODE_NIV : MODE_PCV;
            case 2: return MODE_VCV;
            case 3: return MODE_NIV;
            default: return 0;
        }
    }
    static List<VentMode> getModesByCategory(Context ctx, int category){
        List<VentMode> modes = new ArrayList<>();
        for(VentMode mode : getVentilatorModes(ctx)){
            if(mode.getModeCode()/10 == category && mode.getModeCode() != MODE_AUTO_VENTILATION) modes.add(mode);
        }

        return modes;
    }

    static ArrayList<VentMode> getVentilatorModes(Context ctx){
        ArrayList<VentMode> availableModes = new ArrayList<>();

        // PRESSURE CONTROLLED MODES
        availableModes.add(new VentMode(ctx.getString(R.string.hint_pressure_control), ctx.getString(R.string.hint_pc_cmv), MODE_PC_CMV));
        availableModes.add(new VentMode(ctx.getString(R.string.hint_pressure_control), ctx.getString(R.string.hint_pc_imv), MODE_PC_SIMV));
        availableModes.add(new VentMode(ctx.getString(R.string.hint_pressure_control), ctx.getString(R.string.hint_spont), MODE_PC_AC));
        availableModes.add(new VentMode(ctx.getString(R.string.hint_pressure_control), ctx.getString(R.string.hint_psv), MODE_PC_PSV));
        //availableModes.add(new VentMode(ctx.getString(R.string.hint_pressure_control), ctx.getString(R.string.hint_pc_aprv), MODE_PC_APRV));


        // VOLUME CONTROLLED MODES
        availableModes.add(new VentMode(ctx.getString(R.string.auto_control), ctx.getString(R.string.hint_ai_vent), MODE_AUTO_VENTILATION));
        availableModes.add(new VentMode(ctx.getString(R.string.hint_volume_control), ctx.getString(R.string.hint_vc_cmv), MODE_VCV_CMV));
        availableModes.add(new VentMode(ctx.getString(R.string.hint_volume_control), ctx.getString(R.string.hint_vc_simv), MODE_VCV_SIMV));
        availableModes.add(new VentMode(ctx.getString(R.string.hint_volume_control), ctx.getString(R.string.hint_vc_cv), MODE_VCV_ACV));

        // INO INVASIVE CONTROLLED MODES
        availableModes.add(new VentMode(ctx.getString(R.string.hint_noninvasive), ctx.getString(R.string.hint_cpap), MODE_NIV_CPAP));
        availableModes.add(new VentMode(ctx.getString(R.string.hint_noninvasive), ctx.getString(R.string.hint_bpap), MODE_NIV_BPAP));


        return availableModes;
    }

    static VentMode getVentilatorModeByCode(Context context, int modeCode){
        ArrayList<VentMode> modes = getVentilatorModes(context);
        if(modes != null && !modes.isEmpty()) {
            for (VentMode m : modes){
                if(m.getModeCode() == modeCode) return m;
            }
        }

        return null;
    }

    static boolean isValidVentilatorMode(Context ctx, int mode){
        for(VentMode ventMode : Configs.getVentilatorModes(ctx)) {
            if(ventMode.getModeCode() == mode) return true;
        }

        return false;
    }

    static boolean isVentilatorModeNeoNatal(Context ctx, int mode){
        return isValidVentilatorMode(ctx, mode) && (mode == MODE_NEONAT_PC_SIMV || mode == MODE_NEONAT_VC_SIMV);
    }

    static boolean isAutoScalableSupported(Context ctx, int mode){
        boolean isSupported = (mode != MODE_NIV_BPAP && mode != MODE_PC_SPONT_DUMMY && mode != MODE_NIV_CPAP);
        return isValidVentilatorMode(ctx, mode) && isSupported;
    }

    static boolean isValidOxygenSupportedMode(Context ctx, int mode){
//        boolean isOxygenMode = (getModeCategory(mode) != MODE_NIV && mode != MODE_PC_SPONTANEOUS && mode != MODE_PC_PSV );
        boolean isOxygenMode = true;
        return isValidVentilatorMode(ctx, mode) && isOxygenLevelsAvailable && isOxygenMode;
    }

    /**
     * This is a filter for acknowledgement validation with working mode
     * @param mode : current running ventilation mode
     * @param ack : Acknowledgement coming from ventilator
     * @return is acknowledgement valid
     */
    static boolean isAcknowledgementAcceptable(int mode, String ack){

        if(ack == null) return false;

        // TEMP0RARY BLOCKING
        if(Configs.ACK_CODE_14.equals(ack) || Configs.ACK_CODE_24.equals(ack)) return false;

        if(mode == MODE_NIV_CPAP){
            for(String prohibitAck : cpapProhibitedAcks()){
                if(prohibitAck.equals(ack)) return false;
            }
        }

        return true;
    }

    static int getAckPriorityLevel(String ack){
        return unmutedLevelAcks.contains(ack)? WARNING_LEVEL_UNMUTABLE : (highLevelAcks.contains(ack) ? WARNING_LEVEL_HIGH : WARNING_LEVEL_LOW);
    }

    static boolean isBackupVentilationAcceptable(int mode){
        return getModeCategory(mode) == MODE_NIV || mode == MODE_PC_SPONTANEOUS || mode == MODE_PC_SPONT_DUMMY;
    }

    static String getParameterUnit(final Context context, final String lbl){
        switch (lbl){
            case LBL_PIP: return context.getString(R.string.hint_cmH2o);
            case LBL_VTI: return context.getString(R.string.hint_ml);
            case LBL_PEEP: return context.getString(R.string.hint_cmH2o);
            case LBL_RR: return context.getString(R.string.hint_bpm);
            case LBL_FIO2: return context.getString(R.string.hint_percentage);
            case LBL_PPLAT: return context.getString(R.string.hint_cmH2o);
            case LBL_VLEAK: return context.getString(R.string.hint_ml);
            case LBL_AVERAGE_LEAK: return context.getString(R.string.hint_percentage);
            case LBL_MVI: return context.getString(R.string.hint_ml);
            case LBL_MVE: return context.getString(R.string.hint_ml);
            case LBL_PEAK_FLOW: return context.getString(R.string.hint_l_min);
            case LBL_TINSP: return context.getString(R.string.hint_sec);
            default: return "";
        }
    }

    static int getCompensateInputRR(int rawRR){
        final double rr = (-478.2953) + (44.29698 * rawRR) - (1.441746 * Math.pow(rawRR, 2)) + (0.02107701 * Math.pow(rawRR, 3)) - (0.000113417 * Math.pow(rawRR, 4));
        return (int)Math.round(rr);
    }

    static ArrayList<String> cpapProhibitedAcks(){
        ArrayList<String> prohibitedAcks = new ArrayList<>();
        prohibitedAcks.add(ACK_CODE_7);
        prohibitedAcks.add(ACK_CODE_8);
        prohibitedAcks.add(ACK_CODE_9);
        prohibitedAcks.add(ACK_CODE_12);
        prohibitedAcks.add(ACK_CODE_13);
        prohibitedAcks.add(ACK_CODE_14);
        prohibitedAcks.add(ACK_CODE_22);
        prohibitedAcks.add(ACK_CODE_24);

        return prohibitedAcks;
    }

    static Pair<Float, Float> calculateInspTimeConstraints(int rr){
        if(rr == 0) return null;

        final float cycleTime = 60f / rr;

        final Pair<Float, Float> limits = new Pair<>((float)(0.1 * cycleTime), (float)(0.5 * cycleTime));
        return limits;
    }


    static Pair<Float, Float> FlowGraphLimits(int min, int max){
        switch(min) {
            case 100 : {
                min = ConstantKt.NEO_GRAPH_FLOW_MIN;
            }
            break;
            case 200: {
                min = ConstantKt.PED_GRAPH_FLOW_MIN;
            }
            break;
            case 400 : {
                min = ConstantKt.ADULT_GRAPH_FLOW_MIN;
            }
            default : min = 100;
        }
        switch(max){

            case 100 : {
                max = ConstantKt.NEO_GRAPH_FLOW_MAX;
            }

            case 200: {
                max = ConstantKt.PED_GRAPH_FLOW_MAX;
            }
            case 400 : {
                max = ConstantKt.ADULT_GRAPH_FLOW_MAX;
            }
            default:
        }
        final Pair<Float, Float> limiters = new Pair<>((float)(min), (float)(max));
        return limiters;
    }

    @SuppressLint("DefaultLocale")
    static String calculateInspiratoryTimeLimit(int rr){
        if(rr == 0) return null;
        int cycleTime = 60 / rr;
        int maxExpTime = 300;
        int maxInspTime = (cycleTime * 1000) -maxExpTime ;

        return String.format("%.1f", maxInspTime);
    }

    static String calculateIERatio(int rr, Float tinsp){
        if(rr == 0 || tinsp == null) return null;

        float cycleTime = 60f / rr;
        Float texp = cycleTime - tinsp;
        float ratio = texp/tinsp;
        return 1 + " : " + String.format("%.1f", ratio);
    }

    static Float calculateInspTimeFromIERatio(int rr, String ratio){
        if(ratio != null && ratio.contains(":") && rr > 0){
            try{


                float cycleTime = 60f / rr;
                float eiratio = Float.valueOf(ratio.substring(ratio.indexOf(":") + 1));

                return cycleTime/(eiratio + 1);

            } catch (Exception e){
                Log.i("PARSE CHECK", "Unable to parse IE ratio. Not a number");
            }
        }

        return null;
    }

    static float calculatePIPForAutoVentilation(float vol){
        if(vol <= 200) return 30f;
        else if (vol < 300 && vol > 200) return 40f;
        else if (vol < 500 && vol >= 300) return 50f;
        else if (vol >= 500) return 60f;
        else return -1f;
    }

    static float calculateInspTimeForAutoVentilation(float vol){
        if(vol <= 100) return 0.8f;
        else if (vol <= 300 && vol > 100) return 1.1f;
        else if (vol <=500 && vol > 300) return 1.3f;
        else if(vol > 500 ) return 1.5f;
        else return -1f;
    }

    static float calculateVolumeForAutoVentilation(float weight){
        return weight * 7;
    }

    static float calculateIBW(float heightInCms){
        // 1 inch = 2.54 cms
        if(heightInCms > 0){
            return (float) (48 + (2.3 * ((heightInCms/2.54) - 60)));
        }

        return -1;
    }


    static String calculateTtot(int rr){
        if(rr == 0 ) return null;
        float cycleTime = 60f / rr;
        return String.format("%.1f", cycleTime);
    }

    static String calculateTexp( int rr, float ti ){
        float cycleTime = 60f / rr;
        float texp = cycleTime - ti;
        return String.format("%.1f", texp);
    }

    //decimal check with boolean values
    static boolean isDecimalSupported(String label){
        if (LBL_TRIG_FLOW.equals(label) || LBL_APNEA_TRIG_FLOW.equals(label) || LBL_TLOW.equals(label) || LBL_TINSP.equals(label) || LBL_MVE.equals(label) || LBL_MVI.equals(label)){
            return true;
        } else {
            return false;
        }
    }

    static


    //ack99 added here
    class MessageFactory{
       /* public static String getAckMess(Context ctx,String code){
            switch (code){
                case ACK_CODE_0 : return ctx.getResources().getString(R.string.ack_0);
                case ACK_CODE_1 : return ctx.getResources().getString(R.string.ack_1);
                case ACK_CODE_2 : return ctx.getResources().getString(R.string.ack_2);
                case ACK_CODE_3 : return ctx.getResources().getString(R.string.ack_3);
                case ACK_CODE_4 : return ctx.getResources().getString(R.string.ack_4);
                case ACK_CODE_5 : return ctx.getResources().getString(R.string.ack_5);
                case ACK_CODE_6 : return ctx.getResources().getString(R.string.ack_6);
                case ACK_CODE_7 : return ctx.getResources().getString(R.string.ack_7);
                case ACK_CODE_8 : return ctx.getResources().getString(R.string.ack_8);
                case ACK_CODE_9 : return ctx.getResources().getString(R.string.ack_9);
                case ACK_CODE_10 : return ctx.getResources().getString(R.string.ack_10);
                case ACK_CODE_11 : return ctx.getResources().getString(R.string.ack_11);
                case ACK_CODE_12 : return ctx.getResources().getString(R.string.ack_12);
                case ACK_CODE_13 : return ctx.getResources().getString(R.string.ack_13);
                case ACK_CODE_14 : return ctx.getResources().getString(R.string.ack_14);
                case ACK_CODE_15 : return ctx.getResources().getString(R.string.ack_15);
                case ACK_CODE_16 : return ctx.getResources().getString(R.string.ack_16);
                case ACK_CODE_17 : return ctx.getResources().getString(R.string.ack_17);
                case ACK_CODE_18 : return ctx.getResources().getString(R.string.ack_18);
                case ACK_CODE_19 : return ctx.getResources().getString(R.string.ack_19);
                case ACK_CODE_20 : return ctx.getResources().getString(R.string.ack_20);
                case ACK_CODE_21 : return ctx.getResources().getString(R.string.ack_21);
                case ACK_CODE_22 : return ctx.getResources().getString(R.string.ack_22);
                case ACK_CODE_23 : return ctx.getResources().getString(R.string.ack_23);
                case ACK_CODE_24 : return ctx.getResources().getString(R.string.ack_24);
                case ACK_CODE_25 : return ctx.getResources().getString(R.string.ack_25);
                case ACK_CODE_26 : return ctx.getResources().getString(R.string.ack_26);
                case ACK_CODE_27 : return ctx.getResources().getString(R.string.ack_27);
                case ACK_CODE_28 : return ctx.getResources().getString(R.string.ack_28);
                case ACK_CODE_29 : return ctx.getResources().getString(R.string.ack_29);
                case ACK_CODE_30 : return ctx.getResources().getString(R.string.ack_30);
                case ACK_CODE_31 : return ctx.getResources().getString(R.string.ack_31);
                case ACK_CODE_32 : return ctx.getResources().getString(R.string.ack_32);
                case ACK_CODE_33 : return ctx.getResources().getString(R.string.ack_33);
                case ACK_CODE_34 : return ctx.getResources().getString(R.string.ack_34);
                case ACK_CODE_35 : return ctx.getResources().getString(R.string.ack_35);
                case ACK_CODE_36 : return ctx.getResources().getString(R.string.ack_36);
                case ACK_CODE_37 : return ctx.getResources().getString(R.string.ack_37);
                case ACK_CODE_38 : return ctx.getResources().getString(R.string.ack_38);
                case ACK_CODE_39 : return ctx.getResources().getString(R.string.ack_39);
                case ACK_CODE_40 : return ctx.getResources().getString(R.string.ack_40);
                case ACK_CODE_41 : return ctx.getResources().getString(R.string.ack_41);
                case ACK_CODE_44 : return ctx.getResources().getString(R.string.ack_44);
                case ACK_CODE_45 : return ctx.getResources().getString(R.string.ack_45);
                case ACK_CODE_46 : return ctx.getResources().getString(R.string.ack_46);
                case ACK_CODE_47 : return ctx.getResources().getString(R.string.ack_47);
                case ACK_CODE_48 : return ctx.getResources().getString(R.string.ack_48);
                case ACK_CODE_49 : return ctx.getResources().getString(R.string.ack_49);
                case ACK_CODE_50 : return ctx.getResources().getString(R.string.ack_50);
                case ACK_CODE_51 : return ctx.getResources().getString(R.string.ack_51);
                case ACK_CODE_52 : return ctx.getResources().getString(R.string.ack_52);
                case ACK_CODE_53 : return ctx.getResources().getString(R.string.ack_53);
                case ACK_CODE_54 : return ctx.getResources().getString(R.string.ack_54);
                case ACK_CODE_55 : return ctx.getResources().getString(R.string.ack_55);
                case ACK_CODE_56 : return ctx.getResources().getString(R.string.ack_56);
                case ACK_CODE_57 : return ctx.getResources().getString(R.string.ack_57);
                case ACK_CODE_58 : return ctx.getResources().getString(R.string.ack_58);
                case ACK_CODE_59 : return ctx.getResources().getString(R.string.ack_59);
                case ACK_CODE_60 : return ctx.getResources().getString(R.string.ack_60);
                case ACK_CODE_61 : return ctx.getResources().getString(R.string.ack_61);
                case ACK_CODE_62 : return ctx.getResources().getString(R.string.ack_62);
                case ACK_CODE_63 : return ctx.getResources().getString(R.string.ack_63);
                case ACK_CODE_64 : return ctx.getResources().getString(R.string.ack_64);
                case ACK_CODE_65 : return ctx.getResources().getString(R.string.ack_65);
                case ACK_CODE_66 : return ctx.getResources().getString(R.string.ack_66);
                case ACK_CODE_67 : return ctx.getResources().getString(R.string.ack_67);
                case ACK_CODE_68 : return ctx.getResources().getString(R.string.ack_68);

                case ACK_CODE_70 : return ctx.getResources().getString(R.string.ack_70);
                case ACK_CODE_71 : return ctx.getResources().getString(R.string.ack_71);
                case ACK_CODE_72 : return ctx.getResources().getString(R.string.ack_72);
                case ACK_CODE_73 : return ctx.getResources().getString(R.string.ack_73);
                case ACK_CODE_74 : return ctx.getResources().getString(R.string.ack_74);
                case ACK_CODE_75 : return ctx.getResources().getString(R.string.ack_75);
                case ACK_CODE_76 : return ctx.getResources().getString(R.string.ack_76);
                case ACK_CODE_80 : return ctx.getResources().getString(R.string.ack_80);
                case ACK_CODE_81 : return ctx.getResources().getString(R.string.ack_81);
                case ACK_CODE_90 : return ctx.getResources().getString(R.string.ack_90);
                case ACK_CODE_91 : return ctx.getResources().getString(R.string.ack_91);

            }
*/
        public static String getAckMessage(Context ctx, String code){
            switch (code){
                case ACK_CODE_0 : return ctx.getResources().getString(R.string.ack_0);
                case ACK_CODE_1 : return ctx.getResources().getString(R.string.ack_1);
                case ACK_CODE_2 : return ctx.getResources().getString(R.string.ack_2);
                case ACK_CODE_3 : return ctx.getResources().getString(R.string.ack_3);
                case ACK_CODE_4 : return ctx.getResources().getString(R.string.ack_4);
                case ACK_CODE_5 : return ctx.getResources().getString(R.string.ack_5);
                case ACK_CODE_6 : return ctx.getResources().getString(R.string.ack_6);
                case ACK_CODE_7 : return ctx.getResources().getString(R.string.ack_7);
                case ACK_CODE_8 : return ctx.getResources().getString(R.string.ack_8);
                case ACK_CODE_9 : return ctx.getResources().getString(R.string.ack_9);
                case ACK_CODE_10 : return ctx.getResources().getString(R.string.ack_10);
                case ACK_CODE_11 : return ctx.getResources().getString(R.string.ack_11);
                case ACK_CODE_12 : return ctx.getResources().getString(R.string.ack_12);
                case ACK_CODE_13 : return ctx.getResources().getString(R.string.ack_13);
                case ACK_CODE_14 : return ctx.getResources().getString(R.string.ack_14);
                case ACK_CODE_15 : return ctx.getResources().getString(R.string.ack_15);
                case ACK_CODE_16 : return ctx.getResources().getString(R.string.ack_16);
                case ACK_CODE_17 : return ctx.getResources().getString(R.string.ack_17);
                case ACK_CODE_18 : return ctx.getResources().getString(R.string.ack_18);
                case ACK_CODE_19 : return ctx.getResources().getString(R.string.ack_19);
                case ACK_CODE_20 : return ctx.getResources().getString(R.string.ack_20);
                case ACK_CODE_21 : return ctx.getResources().getString(R.string.ack_21);
                case ACK_CODE_22 : return ctx.getResources().getString(R.string.ack_22);
                case ACK_CODE_23 : return ctx.getResources().getString(R.string.ack_23);
                case ACK_CODE_24 : return ctx.getResources().getString(R.string.ack_24);
                case ACK_CODE_25 : return ctx.getResources().getString(R.string.ack_25);
                case ACK_CODE_26 : return ctx.getResources().getString(R.string.ack_26);
                case ACK_CODE_27 : return ctx.getResources().getString(R.string.ack_27);
                case ACK_CODE_28 : return ctx.getResources().getString(R.string.ack_28);
                case ACK_CODE_29 : return ctx.getResources().getString(R.string.ack_29);
                case ACK_CODE_30 : return ctx.getResources().getString(R.string.ack_30);
                case ACK_CODE_31 : return ctx.getResources().getString(R.string.ack_31);
                case ACK_CODE_32 : return ctx.getResources().getString(R.string.ack_32);
                case ACK_CODE_33 : return ctx.getResources().getString(R.string.ack_33);
                case ACK_CODE_34 : return ctx.getResources().getString(R.string.ack_34);
                case ACK_CODE_35 : return ctx.getResources().getString(R.string.ack_35);
                case ACK_CODE_36 : return ctx.getResources().getString(R.string.ack_36);
                case ACK_CODE_37 : return ctx.getResources().getString(R.string.ack_37);
                case ACK_CODE_38 : return ctx.getResources().getString(R.string.ack_38);
                case ACK_CODE_39 : return ctx.getResources().getString(R.string.ack_39);
                case ACK_CODE_40 : return ctx.getResources().getString(R.string.ack_40);
                case ACK_CODE_41 : return ctx.getResources().getString(R.string.ack_41);
                case ACK_CODE_44 : return ctx.getResources().getString(R.string.ack_44);
                case ACK_CODE_45 : return ctx.getResources().getString(R.string.ack_45);
                case ACK_CODE_46 : return ctx.getResources().getString(R.string.ack_46);
                case ACK_CODE_47 : return ctx.getResources().getString(R.string.ack_47);
                case ACK_CODE_48 : return ctx.getResources().getString(R.string.ack_48);
                case ACK_CODE_49 : return ctx.getResources().getString(R.string.ack_49);
                case ACK_CODE_50 : return ctx.getResources().getString(R.string.ack_50);
                case ACK_CODE_51 : return ctx.getResources().getString(R.string.ack_51);
                case ACK_CODE_52 : return ctx.getResources().getString(R.string.ack_52);
                case ACK_CODE_53 : return ctx.getResources().getString(R.string.ack_53);
                case ACK_CODE_54 : return ctx.getResources().getString(R.string.ack_54);
                case ACK_CODE_55 : return ctx.getResources().getString(R.string.ack_55);
                case ACK_CODE_56 : return ctx.getResources().getString(R.string.ack_56);
                case ACK_CODE_57 : return ctx.getResources().getString(R.string.ack_57);
                case ACK_CODE_58 : return ctx.getResources().getString(R.string.ack_58);
                case ACK_CODE_59 : return ctx.getResources().getString(R.string.ack_59);
                case ACK_CODE_60 : return ctx.getResources().getString(R.string.ack_60);
                case ACK_CODE_61 : return ctx.getResources().getString(R.string.ack_61);
                case ACK_CODE_62 : return ctx.getResources().getString(R.string.ack_62);
                case ACK_CODE_63 : return ctx.getResources().getString(R.string.ack_63);
                case ACK_CODE_64 : return ctx.getResources().getString(R.string.ack_64);
                case ACK_CODE_65 : return ctx.getResources().getString(R.string.ack_65);
                case ACK_CODE_66 : return ctx.getResources().getString(R.string.ack_66);
                case ACK_CODE_67 : return ctx.getResources().getString(R.string.ack_67);
                case ACK_CODE_68 : return ctx.getResources().getString(R.string.ack_68);

                case ACK_CODE_70 : return ctx.getResources().getString(R.string.ack_70);
                case ACK_CODE_71 : return ctx.getResources().getString(R.string.ack_71);
                case ACK_CODE_72 : return ctx.getResources().getString(R.string.ack_72);
                case ACK_CODE_73 : return ctx.getResources().getString(R.string.ack_73);
                case ACK_CODE_74 : return ctx.getResources().getString(R.string.ack_74);
                case ACK_CODE_75 : return ctx.getResources().getString(R.string.ack_75);
                case ACK_CODE_76 : return ctx.getResources().getString(R.string.ack_76);
                case ACK_CODE_80 : return ctx.getResources().getString(R.string.ack_80);
                case ACK_CODE_81 : return ctx.getResources().getString(R.string.ack_81);
                case ACK_CODE_90 : return ctx.getResources().getString(R.string.ack_90);
                case ACK_CODE_91 : return ctx.getResources().getString(R.string.ack_91);

                case ACK_CODE_320 : return ctx.getResources().getString(R.string.ack_320);
                case ACK_CODE_321 : return ctx.getResources().getString(R.string.ack_321);
                case ACK_CODE_322 : return ctx.getResources().getString(R.string.ack_322);
                case ACK_CODE_323 : return ctx.getResources().getString(R.string.ack_323);
                case ACK_CODE_324 : return ctx.getResources().getString(R.string.ack_324);
                case ACK_CODE_325 : return ctx.getResources().getString(R.string.ack_325);
                case ACK_CODE_326 : return ctx.getResources().getString(R.string.ack_326);
                case ACK_CODE_327 : return ctx.getResources().getString(R.string.ack_327);
                case ACK_CODE_328 : return ctx.getResources().getString(R.string.ack_328);
                case ACK_CODE_329 : return ctx.getResources().getString(R.string.ack_329);
                case ACK_CODE_330 : return ctx.getResources().getString(R.string.ack_330);
                case ACK_CODE_331 : return ctx.getResources().getString(R.string.ack_331);
                case ACK_CODE_332 : return ctx.getResources().getString(R.string.ack_332);
                case ACK_CODE_333 : return ctx.getResources().getString(R.string.ack_333);
                case ACK_CODE_334 : return ctx.getResources().getString(R.string.ack_334);
                case ACK_CODE_335 : return ctx.getResources().getString(R.string.ack_335);
                case ACK_CODE_336 : return ctx.getResources().getString(R.string.ack_336);
                case ACK_CODE_337 : return ctx.getResources().getString(R.string.ack_337);
                case ACK_CODE_338 : return ctx.getResources().getString(R.string.ack_338);
                case ACK_CODE_339 : return ctx.getResources().getString(R.string.ack_339);
                case ACK_CODE_340 : return ctx.getResources().getString(R.string.ack_340);
                case ACK_CODE_341 : return ctx.getResources().getString(R.string.ack_341);
                case ACK_CODE_342 : return ctx.getResources().getString(R.string.ack_342);
                case ACK_CODE_343 : return ctx.getResources().getString(R.string.ack_343);
                case ACK_CODE_344 : return ctx.getResources().getString(R.string.ack_344);
                case ACK_CODE_345 : return ctx.getResources().getString(R.string.ack_345);
                case ACK_CODE_346 : return ctx.getResources().getString(R.string.ack_346);
                case ACK_CODE_347 : return ctx.getResources().getString(R.string.ack_347);
                case ACK_CODE_348 : return ctx.getResources().getString(R.string.ack_348);
                case ACK_CODE_349 : return ctx.getResources().getString(R.string.ack_349);
                case ACK_CODE_350 : return ctx.getResources().getString(R.string.ack_350);
                case ACK_CODE_351 : return ctx.getResources().getString(R.string.ack_351);
                case ACK_CODE_352 : return ctx.getResources().getString(R.string.ack_352);
                case ACK_CODE_353 : return ctx.getResources().getString(R.string.ack_353);
                case ACK_CODE_354 : return ctx.getResources().getString(R.string.ack_354);
                case ACK_CODE_355 : return ctx.getResources().getString(R.string.ack_355);
                case ACK_CODE_356 : return ctx.getResources().getString(R.string.ack_356);
                case ACK_CODE_357 : return ctx.getResources().getString(R.string.ack_357);
                case ACK_CODE_358 : return ctx.getResources().getString(R.string.ack_358);
                case ACK_CODE_359 : return ctx.getResources().getString(R.string.ack_359);
                case ACK_CODE_360 : return ctx.getResources().getString(R.string.ack_360);
                case ACK_CODE_361 : return ctx.getResources().getString(R.string.ack_361);
                case ACK_CODE_362 : return ctx.getResources().getString(R.string.ack_362);
                case ACK_CODE_363 : return ctx.getResources().getString(R.string.ack_363);
                case ACK_CODE_364 : return ctx.getResources().getString(R.string.ack_364);
                case ACK_CODE_365 : return ctx.getResources().getString(R.string.ack_365);
                case ACK_CODE_366 : return ctx.getResources().getString(R.string.ack_366);
                case ACK_CODE_367 : return ctx.getResources().getString(R.string.ack_367);
                case ACK_CODE_368 : return ctx.getResources().getString(R.string.ack_368);
                case ACK_CODE_369 : return ctx.getResources().getString(R.string.ack_369);
                case ACK_CODE_370 : return ctx.getResources().getString(R.string.ack_370);
                case ACK_CODE_371 : return ctx.getResources().getString(R.string.ack_371);
                case ACK_CODE_372 : return ctx.getResources().getString(R.string.ack_372);
                case ACK_CODE_373 : return ctx.getResources().getString(R.string.ack_373);
                case ACK_CODE_374 : return ctx.getResources().getString(R.string.ack_374);
                case ACK_CODE_375 : return ctx.getResources().getString(R.string.ack_375);
                case ACK_CODE_376 : return ctx.getResources().getString(R.string.ack_376);
                case ACK_CODE_377 : return ctx.getResources().getString(R.string.ack_377);
                case ACK_CODE_378 : return ctx.getResources().getString(R.string.ack_378);
                case ACK_CODE_379 : return ctx.getResources().getString(R.string.ack_379);
                case ACK_CODE_380 : return ctx.getResources().getString(R.string.ack_380);
                case ACK_CODE_381 : return ctx.getResources().getString(R.string.ack_381);
                case ACK_CODE_382 : return ctx.getResources().getString(R.string.ack_382);
                case ACK_CODE_383 : return ctx.getResources().getString(R.string.ack_383);
                case ACK_CODE_384 : return ctx.getResources().getString(R.string.ack_384);
                case ACK_CODE_385 : return ctx.getResources().getString(R.string.ack_385);
                case ACK_CODE_386 : return ctx.getResources().getString(R.string.ack_386);
                case ACK_CODE_387 : return ctx.getResources().getString(R.string.ack_387);
                case ACK_CODE_388 : return ctx.getResources().getString(R.string.ack_388);
                case ACK_CODE_389 : return ctx.getResources().getString(R.string.ack_389);
                case ACK_CODE_390 : return ctx.getResources().getString(R.string.ack_390);
                case ACK_CODE_391 : return ctx.getResources().getString(R.string.ack_391);
                case ACK_CODE_392 : return ctx.getResources().getString(R.string.ack_392);
                case ACK_CODE_393 : return ctx.getResources().getString(R.string.ack_393);
                case ACK_CODE_394 : return ctx.getResources().getString(R.string.ack_394);
                case ACK_CODE_395 : return ctx.getResources().getString(R.string.ack_395);
                case ACK_CODE_396 : return ctx.getResources().getString(R.string.ack_396);
                case ACK_CODE_397 : return ctx.getResources().getString(R.string.ack_397);
                case ACK_CODE_398 : return ctx.getResources().getString(R.string.ack_398);
                case ACK_CODE_399 : return ctx.getResources().getString(R.string.ack_399);
                case ACK_CODE_400 : return ctx.getResources().getString(R.string.ack_400);
                case ACK_CODE_401 : return ctx.getResources().getString(R.string.ack_401);
                case ACK_CODE_402 : return ctx.getResources().getString(R.string.ack_402);
                case ACK_CODE_403 : return ctx.getResources().getString(R.string.ack_403);
                case ACK_CODE_404 : return ctx.getResources().getString(R.string.ack_404);

                case ACK_CODE_405 : return ctx.getResources().getString(R.string.ack_405);
                case ACK_CODE_406 : return ctx.getResources().getString(R.string.ack_406);
                case ACK_CODE_407 : return ctx.getResources().getString(R.string.ack_407);
                case ACK_CODE_408 : return ctx.getResources().getString(R.string.ack_408);
                case ACK_CODE_409 : return ctx.getResources().getString(R.string.ack_409);
                case ACK_CODE_410 : return ctx.getResources().getString(R.string.ack_410);
                case ACK_CODE_411 : return ctx.getResources().getString(R.string.ack_411);
                case ACK_CODE_412 : return ctx.getResources().getString(R.string.ack_412);
                case ACK_CODE_413 : return ctx.getResources().getString(R.string.ack_413);
                case ACK_CODE_414 : return ctx.getResources().getString(R.string.ack_414);
                case ACK_CODE_415 : return ctx.getResources().getString(R.string.ack_415);
                case ACK_CODE_416 : return ctx.getResources().getString(R.string.ack_416);
                case ACK_CODE_417 : return ctx.getResources().getString(R.string.ack_417);
                case ACK_CODE_418 : return ctx.getResources().getString(R.string.ack_418);
                case ACK_CODE_419 : return ctx.getResources().getString(R.string.ack_419);
                case ACK_CODE_420 : return ctx.getResources().getString(R.string.ack_420);
                case ACK_CODE_430 : return ctx.getResources().getString(R.string.ack_430);
                case ACK_CODE_640 : return ctx.getResources().getString(R.string.ack_640);
                case ACK_CODE_641 : return ctx.getResources().getString(R.string.ack_641);
                case ACK_CODE_642 : return ctx.getResources().getString(R.string.ack_642);
                case ACK_CODE_643 : return ctx.getResources().getString(R.string.ack_643);
                case ACK_CODE_644 : return ctx.getResources().getString(R.string.ack_644);
                case ACK_CODE_645 : return ctx.getResources().getString(R.string.ack_645);
                case ACK_CODE_646 : return ctx.getResources().getString(R.string.ack_646);
                case ACK_CODE_647 : return ctx.getResources().getString(R.string.ack_647);
                case ACK_CODE_648 : return ctx.getResources().getString(R.string.ack_648);
                case ACK_CODE_649 : return ctx.getResources().getString(R.string.ack_649);
                case ACK_CODE_650 : return ctx.getResources().getString(R.string.ack_650);
                case ACK_CODE_651 : return ctx.getResources().getString(R.string.ack_651);
                case ACK_CODE_652 : return ctx.getResources().getString(R.string.ack_652);
                case ACK_CODE_653 : return ctx.getResources().getString(R.string.ack_653);
                case ACK_CODE_654 : return ctx.getResources().getString(R.string.ack_654);
                case ACK_CODE_655 : return ctx.getResources().getString(R.string.ack_655);
                case ACK_CODE_656 : return ctx.getResources().getString(R.string.ack_656);
                case ACK_CODE_657 : return ctx.getResources().getString(R.string.ack_657);
                case ACK_CODE_658 : return ctx.getResources().getString(R.string.ack_658);
                case ACK_CODE_659 : return ctx.getResources().getString(R.string.ack_659);
                case ACK_CODE_660 : return ctx.getResources().getString(R.string.ack_660);
                case ACK_CODE_661 : return ctx.getResources().getString(R.string.ack_661);
                case ACK_CODE_662 : return ctx.getResources().getString(R.string.ack_662);
                case ACK_CODE_663 : return ctx.getResources().getString(R.string.ack_663);
                case ACK_CODE_664 : return ctx.getResources().getString(R.string.ack_664);
                case ACK_CODE_665 : return ctx.getResources().getString(R.string.ack_665);
                case ACK_CODE_666 : return ctx.getResources().getString(R.string.ack_666);
                case ACK_CODE_667 : return ctx.getResources().getString(R.string.ack_667);
                case ACK_CODE_668 : return ctx.getResources().getString(R.string.ack_668);
                case ACK_CODE_669 : return ctx.getResources().getString(R.string.ack_669);
                case ACK_CODE_670 : return ctx.getResources().getString(R.string.ack_670);
                case ACK_CODE_671 : return ctx.getResources().getString(R.string.ack_671);
                case ACK_CODE_672 : return ctx.getResources().getString(R.string.ack_672);
                case ACK_CODE_673 : return ctx.getResources().getString(R.string.ack_673);
                case ACK_CODE_674 : return ctx.getResources().getString(R.string.ack_674);
                case ACK_CODE_675 : return ctx.getResources().getString(R.string.ack_675);
                case ACK_CODE_676 : return ctx.getResources().getString(R.string.ack_676);
                case ACK_CODE_677 : return ctx.getResources().getString(R.string.ack_677);
                case ACK_CODE_678 : return ctx.getResources().getString(R.string.ack_678);
                case ACK_CODE_679 : return ctx.getResources().getString(R.string.ack_679);
                case ACK_CODE_680 : return ctx.getResources().getString(R.string.ack_680);
                case ACK_CODE_681 : return ctx.getResources().getString(R.string.ack_681);
                case ACK_CODE_682 : return ctx.getResources().getString(R.string.ack_682);
                case ACK_CODE_683 : return ctx.getResources().getString(R.string.ack_683);
                case ACK_CODE_684 : return ctx.getResources().getString(R.string.ack_684);
                case ACK_CODE_685 : return ctx.getResources().getString(R.string.ack_685);
                case ACK_CODE_686 : return ctx.getResources().getString(R.string.ack_686);
                case ACK_CODE_687 : return ctx.getResources().getString(R.string.ack_687);
                case ACK_CODE_688 : return ctx.getResources().getString(R.string.ack_688);
                case ACK_CODE_689 : return ctx.getResources().getString(R.string.ack_689);
                case ACK_CODE_690 : return ctx.getResources().getString(R.string.ack_690);
                case ACK_CODE_691 : return ctx.getResources().getString(R.string.ack_691);
                case ACK_CODE_692 : return ctx.getResources().getString(R.string.ack_692);
                case ACK_CODE_693 : return ctx.getResources().getString(R.string.ack_693);
                case ACK_CODE_694 : return ctx.getResources().getString(R.string.ack_694);
                case ACK_CODE_695 : return ctx.getResources().getString(R.string.ack_695);
                case ACK_CODE_696 : return ctx.getResources().getString(R.string.ack_696);
                case ACK_CODE_697 : return ctx.getResources().getString(R.string.ack_697);
                case ACK_CODE_698 : return ctx.getResources().getString(R.string.ack_698);
                case ACK_CODE_699 : return ctx.getResources().getString(R.string.ack_699);
                case ACK_CODE_700 : return ctx.getResources().getString(R.string.ack_700);
                case ACK_CODE_701 : return ctx.getResources().getString(R.string.ack_701);
                case ACK_CODE_702 : return ctx.getResources().getString(R.string.ack_702);
                case ACK_CODE_703 : return ctx.getResources().getString(R.string.ack_703);
                case ACK_CODE_704 : return ctx.getResources().getString(R.string.ack_704);
                case ACK_CODE_705 : return ctx.getResources().getString(R.string.ack_705);
                case ACK_CODE_706 : return ctx.getResources().getString(R.string.ack_706);
                case ACK_CODE_707 : return ctx.getResources().getString(R.string.ack_707);
                case ACK_CODE_708 : return ctx.getResources().getString(R.string.ack_708);
                case ACK_CODE_709 : return ctx.getResources().getString(R.string.ack_709);
                case ACK_CODE_710 : return ctx.getResources().getString(R.string.ack_710);
                case ACK_CODE_711 : return ctx.getResources().getString(R.string.ack_711);
                case ACK_CODE_712 : return ctx.getResources().getString(R.string.ack_712);
                case ACK_CODE_713 : return ctx.getResources().getString(R.string.ack_713);
                case ACK_CODE_714 : return ctx.getResources().getString(R.string.ack_714);
                case ACK_CODE_715 : return ctx.getResources().getString(R.string.ack_715);
                case ACK_CODE_716 : return ctx.getResources().getString(R.string.ack_716);
                case ACK_CODE_717 : return ctx.getResources().getString(R.string.ack_717);
                case ACK_CODE_718 : return ctx.getResources().getString(R.string.ack_718);
                case ACK_CODE_719 : return ctx.getResources().getString(R.string.ack_719);
                case ACK_CODE_720 : return ctx.getResources().getString(R.string.ack_720);
                case ACK_CODE_721 : return ctx.getResources().getString(R.string.ack_721);
                case ACK_CODE_722 : return ctx.getResources().getString(R.string.ack_722);
                case ACK_CODE_723 : return ctx.getResources().getString(R.string.ack_723);
                case ACK_CODE_724 : return ctx.getResources().getString(R.string.ack_724);
                case ACK_CODE_725 : return ctx.getResources().getString(R.string.ack_725);
                case ACK_CODE_726 : return ctx.getResources().getString(R.string.ack_726);
                case ACK_CODE_727 : return ctx.getResources().getString(R.string.ack_727);
                case ACK_CODE_728 : return ctx.getResources().getString(R.string.ack_728);
                case ACK_CODE_729 : return ctx.getResources().getString(R.string.ack_729);
                case ACK_CODE_730 : return ctx.getResources().getString(R.string.ack_730);
                case ACK_CODE_731 : return ctx.getResources().getString(R.string.ack_731);
                case ACK_CODE_732 : return ctx.getResources().getString(R.string.ack_732);
                case ACK_CODE_733 : return ctx.getResources().getString(R.string.ack_733);
                case ACK_CODE_734 : return ctx.getResources().getString(R.string.ack_734);
                case ACK_CODE_735 : return ctx.getResources().getString(R.string.ack_735);
                case ACK_CODE_736 : return ctx.getResources().getString(R.string.ack_736);
                case ACK_CODE_737 : return ctx.getResources().getString(R.string.ack_737);
                case ACK_CODE_738 : return ctx.getResources().getString(R.string.ack_738);
                case ACK_CODE_739 : return ctx.getResources().getString(R.string.ack_739);
                case ACK_CODE_740 : return ctx.getResources().getString(R.string.ack_740);
                case ACK_CODE_741 : return ctx.getResources().getString(R.string.ack_741);
                case ACK_CODE_742 : return ctx.getResources().getString(R.string.ack_742);
                case ACK_CODE_743 : return ctx.getResources().getString(R.string.ack_743);
                case ACK_CODE_744 : return ctx.getResources().getString(R.string.ack_744);
                case ACK_CODE_745 : return ctx.getResources().getString(R.string.ack_745);
                case ACK_CODE_746 : return ctx.getResources().getString(R.string.ack_746);
                case ACK_CODE_747 : return ctx.getResources().getString(R.string.ack_747);
                case ACK_CODE_748 : return ctx.getResources().getString(R.string.ack_748);
                case ACK_CODE_749 : return ctx.getResources().getString(R.string.ack_749);
                case ACK_CODE_750 : return ctx.getResources().getString(R.string.ack_750);
                case ACK_CODE_751 : return ctx.getResources().getString(R.string.ack_751);
                case ACK_CODE_752 : return ctx.getResources().getString(R.string.ack_752);
                case ACK_CODE_753 : return ctx.getResources().getString(R.string.ack_753);
                case ACK_CODE_754 : return ctx.getResources().getString(R.string.ack_754);
                case ACK_CODE_755 : return ctx.getResources().getString(R.string.ack_755);
                case ACK_CODE_756 : return ctx.getResources().getString(R.string.ack_756);
                case ACK_CODE_757 : return ctx.getResources().getString(R.string.ack_757);
                case ACK_CODE_758 : return ctx.getResources().getString(R.string.ack_758);
                case ACK_CODE_759 : return ctx.getResources().getString(R.string.ack_759);
                case ACK_CODE_760 : return ctx.getResources().getString(R.string.ack_760);
                case ACK_CODE_761 : return ctx.getResources().getString(R.string.ack_761);
                case ACK_CODE_762 : return ctx.getResources().getString(R.string.ack_762);

                case ACK_CODE_763 : return ctx.getResources().getString(R.string.ack_763);
                case ACK_CODE_764 : return ctx.getResources().getString(R.string.ack_764);
                case ACK_CODE_765 : return ctx.getResources().getString(R.string.ack_765);
                case ACK_CODE_766 : return ctx.getResources().getString(R.string.ack_766);
                case ACK_CODE_767 : return ctx.getResources().getString(R.string.ack_767);
                case ACK_CODE_768 : return ctx.getResources().getString(R.string.ack_768);
                case ACK_CODE_769 : return ctx.getResources().getString(R.string.ack_769);
                case ACK_CODE_770 : return ctx.getResources().getString(R.string.ack_770);
                case ACK_CODE_771 : return ctx.getResources().getString(R.string.ack_771);
                case ACK_CODE_772 : return ctx.getResources().getString(R.string.ack_772);
                case ACK_CODE_773 : return ctx.getResources().getString(R.string.ack_773);
                case ACK_CODE_774 : return ctx.getResources().getString(R.string.ack_774);
                case ACK_CODE_775 : return ctx.getResources().getString(R.string.ack_775);
                case ACK_CODE_776 : return ctx.getResources().getString(R.string.ack_776);
                case ACK_CODE_777 : return ctx.getResources().getString(R.string.ack_777);
                case ACK_CODE_778 : return ctx.getResources().getString(R.string.ack_778);
                case ACK_CODE_779 : return ctx.getResources().getString(R.string.ack_779);
                case ACK_CODE_780 : return ctx.getResources().getString(R.string.ack_780);
                case ACK_CODE_781 : return ctx.getResources().getString(R.string.ack_781);
                case ACK_CODE_782 : return ctx.getResources().getString(R.string.ack_782);
                case ACK_CODE_783 : return ctx.getResources().getString(R.string.ack_783);
                case ACK_CODE_784 : return ctx.getResources().getString(R.string.ack_784);
                case ACK_CODE_785 : return ctx.getResources().getString(R.string.ack_785);
                case ACK_CODE_786 : return ctx.getResources().getString(R.string.ack_786);
                case ACK_CODE_790 : return ctx.getResources().getString(R.string.ack_790);
                case ACK_CODE_791 : return ctx.getResources().getString(R.string.ack_791);
                case ACK_CODE_792 : return ctx.getResources().getString(R.string.ack_792);
                case ACK_CODE_793 : return ctx.getResources().getString(R.string.ack_793);
                case ACK_CODE_794 : return ctx.getResources().getString(R.string.ack_794);
                case ACK_CODE_795 : return ctx.getResources().getString(R.string.ack_795);
                case ACK_CODE_796 : return ctx.getResources().getString(R.string.ack_796);



                default: return "ERROR "+code;
            }
        }
    }





}
