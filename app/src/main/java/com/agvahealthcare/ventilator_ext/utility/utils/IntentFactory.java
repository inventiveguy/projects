package com.agvahealthcare.ventilator_ext.utility.utils;

/**
 * Created by MOHIT MALHOTRA on 24-10-2018.
 */

public interface IntentFactory {
    String ACTION_USB_PERMISSION_VENTILATOR = "com.agvahealthcare.ventilator_ext.ACTION_USB_PERMISSION_VENTILATOR";
    String ACTION_USB_PERMISSION_HID = "com.agvahealthcare.ventilator_ext.ACTION_USB_PERMISSION_HID";
    String ACTION_DEVICE_CONNECTED = "com.agvahealthcare.ventilator_ext.ACTION_DEVICE_CONNECTED";
    String ACTION_DEVICE_FAST_CONNECTED = "com.agvahealthcare.ventilator_ext.ACTION_DEVICE_CONNECTED";
    String ACTION_DEVICE_DISCONNECTED = "com.agvahealthcare.ventilator_ext.ACTION_DEVICE_DISCONNECTED";
    String ACTION_BT_DEVICE_FOUND = "com.agvahealthcare.ventilator_ext.ACTION_BT_DEVICE_FOUND";
    String ACTION_BT_DEVICE_NOT_FOUND = "com.agvahealthcare.ventilator_ext.ACTION_BT_DEVICE_NOT_FOUND";
    String ACTION_GATT_SERVICES_DISCOVERED = "com.agvahealthcare.ventilator_ext.ACTION_GATT_SERVICES_DISCOVERED";
    String ACTION_DATA_AVAILABLE = "com.agvahealthcare.ventilator_ext.ACTION_DATA_AVAILABLE";
    String ACTION_RAW_DATA_AVAILABLE = "com.agvahealthcare.ventilator_ext.ACTION_RAW_DATA_AVAILABLE";
    String ACTION_SOFTWARE_VERSION_AVAILABLE = "com.agvahealthcare.ventilator_ext.ACTION_SOFTWARE_VERSION_AVAILABLE";
    String ACTION_VENTILATOR_WIFI_CONNECTION_REQUESTED = "com.agvahealthcare.ventilator_ext.ACTION_VENTILATOR_WIFI_CONNECTION_REQUESTED";
    String ACTION_VENTILATOR_WIFI_CONNECTION_RESPONSED = "com.agvahealthcare.ventilator_ext.ACTION_VENTILATOR_WIFI_CONNECTION_RESPONSED";
    String ACTION_ACK_AVAILABLE = "com.agvahealthcare.ventilator_ext.ACTION_ACK_AVAILABLE";
    String ACTION_BATTERY_STATUS_AVAILABLE = "com.agvahealthcare.ventilator_ext.ACTION_BATTERY_STATUS_AVAILABLE";
    String ACTION_MOTOR_LIFE_STATUS_AVAILABLE = "com.agvahealthcare.ventilator_ext.ACTION_MOTOR_LIFE_STATUS_AVAILABLE";
    String ACTION_HEATSENSE_STATUS_AVAILABLE = "com.agvahealthcare.ventilator_ext.ACTION_HEATSENSE_STATUS_AVAILABLE";
    String ACTION_HANDSHAKE_CALIBRATION_AVAILABLE = "com.agvahealthcare.ventilator_ext.ACTION_HANDSHAKE_CALIBRATION_AVAILABLE";

    String ACTION_SENSOR_AVAILABILITY_RESPONSE = "com.agvahealthcare.ventilator_ext.ACTION_SENSOR_AVAILABILITY_RESPONSE";

    String ACTION_SENSOR_CALIBRATION_RESPONSE = "com.agvahealthcare.ventilator_ext.ACTION_SENSOR_CALIBRATION_RESPONSE";


    String ACTION_STANDBY_STATUS_AVAILABLE = "com.agvahealthcare.ventilator_ext.ACTION_STANDBY_STATUS_AVAILABLE";
    String ACTION_SELF_TEST_STATUS_AVAILABLE = "com.agvahealthcare.ventilator_ext.ACTION_SELF_TEST_STATUS_AVAILABLE";
    String ACTION_DEVICE_NAME_REQUESTED = "com.agvahealthcare.ventilator_ext.ACTION_DEVICE_NAME_REQUESTED";
    String ACTION_DEVICE_NAME_RESPONSED = "com.agvahealthcare.ventilator_ext.ACTION_DEVICE_NAME_RESPONSED";
    String ACTION_DATA_SENT = "com.agvahealthcare.ventilator_ext.ACTION_DATA_SENT";
    String ACTION_HANDSHAKE_COMPLETED = "com.agvahealthcare.ventilator_ext.ACTION_HANDSHAKE_COMPLETED";
    String ACTION_HANDSHAKE_TIMEOUT = "com.agvahealthcare.ventilator_ext.ACTION_HANDSHAKE_TIMEOUT";
    String ACTION_TUBE_BLOCKAGE_DETECTED = "com.agvahealthcare.ventilator_ext.ACTION_TUBE_BLOCKAGE_DETECTED";
    String ACTION_TUBE_BLOCKAGE_RESOLVED = "com.agvahealthcare.ventilator_ext.ACTION_TUBE_BLOCKAGE_RESOLVED";
    String ACTION_HIGH_LEAK_INACCURACY_DETECTED = "com.agvahealthcare.ventilator_ext.ACTION_HIGH_LEAK_INACCURACY_DETECTED";
    String ACTION_HIGH_LEAK_INACCURACY_RESOLVED = "com.agvahealthcare.ventilator_ext.ACTION_HIGH_LEAK_INACCURACY_RESOLVED";
    String ACTION_LEAK_BASED_ALARM_DETECTED = "com.agvahealthcare.ventilator_ext.ACTION_LEAK_BASED_ALARM_DETECTED";
    String ACTION_LEAK_BASED_ALARM_RESOLVED = "com.agvahealthcare.ventilator_ext.ACTION_LEAK_BASED_ALARM_RESOLVED";
    String ACTION_CUFF_LEAKAGE_DETECTED = "com.agvahealthcare.ventilator_ext.ACTION_CUFF_LEAKAGE_DETECTED";
    String ACTION_CUFF_LEAKAGE_RESOLVED = "com.agvahealthcare.ventilator_ext.ACTION_CUFF_LEAKAGE_RESOLVED";
//    String ACTION_FIO2_DISCONNECT_RESOLVED = "com.agvahealthcare.ventilator_ext.ACTION_FIO2_DISCONNECT_RESOLVED";
    String ACTION_FLOW_SENSOR_OCCLUSION_DETECTED = "agvahealthcare.com.falcon.ACTION_FLOW_SENSOR_OCCLUSION_DETECTED";
    String ACTION_FLOW_SENSOR_OCCLUSION_RESOLVED = "agvahealthcare.com.falcon.ACTION_FLOW_SENSOR_OCCLUSION_RESOLVED";
    String
            ACTION_EXPIRE_TIME_OUT_OF_RANGE = "com.agvahealthcare.ventilator_ext.ACTION_EXPIRE_TIME_OUT_OF_RANGE";
    String ACTION_EXPIRE_TIME_UNDER_RANGE = "com.agvahealthcare.ventilator_ext.ACTION_EXPIRE_TIME_UNDER_RANGE";
    String ACTION_MENDOR_SYMLINKING_REQUEST = "com.agvahealthcare.ventilator_ext.ACTION_MENDOR_SYMLINKING_REQUEST";
    String ACTION_MENDOR_SYMLINKING_STARTED = "com.agvahealthcare.ventilator_ext.ACTION_MENDOR_SYMLINKING_STARTED";
    String ACTION_MENDOR_SYMLINKING_FAILED = "com.agvahealthcare.ventilator_ext.ACTION_MENDOR_SYMLINKING_FAILED";
    String ACTION_MENDOR_SYMLINKING_COMPLETED= "com.agvahealthcare.ventilator_ext.ACTION_MENDOR_SYMLINKING_COMPLETED";


    //battery connected and disconnected
    String ACTION_BATTERY_CONNECTED="com.agvahealthcare.ventilator_ext.ACTION_BATTERY_CONNECTED";
    String ACTION_BATTERY_DISCONNECTED="com.agvahealthcare.ventilator_ext.ACTION_BATTERY_DISCONNECTED";

    String ACTION_MODE_SET = "com.agvahealthcare.ventilator_ext.ACTION_MODE_SET";
    String ACTION_SUBMODE_SET = "com.agvahealthcare.ventilator_ext.ACTION_SUBMODE_SET";

    // VENTILATION LIMIT OVERFLOW ALERTS
    String ACTION_VENT_PARAM_LIMIT_UNDERFLOW= "com.agvahealthcare.ventilator_ext.ACTION_LIMIT_UNDERFLOW";
    String ACTION_VENT_PARAM_LIMIT_OVERFLOW= "com.agvahealthcare.ventilator_ext.ACTION_LIMIT_OVERFLOW";

    // LIMITING ALARM
    String ACTION_LOW_O2 = "com.agvahealthcare.ventilator_ext.ACTION_LOW_O2";



    String ACTION_INACTIVE = "com.agvahealthcare.ventilator_ext.ACTION_APP_INACTIVE";

    String ACTION_SF_SHOW_FULL_SCREEN = "com.promobitech.intent.ADD_FULL_SCREEN";
    String ACTION_SF_HIDE_FULL_SCREEN = "com.promobitech.intent.REMOVE_FULL_SCREEN";
    String ACTION_ON_SYSTEM_KILL = "com.agvahealthcare.ventilator_ext.ACTION_ON_SYSTEM_KILL";



    String ACTION_KNOB_CHANGE = "com.agvahealthcare.ventilator_ext.ACTION_KNOB_CHANGE";

     // Quick button hardware integration
    String ACTION_MUTE_UNMUTE="com.agvahealthcare.ventilator_ext.ACTION_MUTE_UNMUTE";
    String ACTION_NEBULISER="com.agvahealthcare.ventilator_ext.ACTION_NEBULISER";
    String ACTION_OXYGEN_100 ="com.agvahealthcare.ventilator_ext.ACTION_OXYGEN";
    String ACTION_INSPIRATORY_HOLD="com.agvahealthcare.ventilator_ext.ACTION_INSPIRATORY_HOLD";
    String ACTION_EXPIRATORY_HOLD="com.agvahealthcare.ventilator_ext.ACTION_EXPIRATORY_HOLD";
    String ACTION_MANUAL_BREATH="com.agvahealthcare.ventilator_ext.ACTION_MANUAL_BREATH";
    String ACTION_HOME="com.agvahealthcare.ventilator_ext.ACTION_HOME";
    String ACTION_LOCK="com.agvahealthcare.ventilator_ext.ACTION_LOCK";
    String ACTION_POWER_SWITCH="com.agvahealthcare.ventilator_ext.ACTION_POWER_SWITCH";





}
