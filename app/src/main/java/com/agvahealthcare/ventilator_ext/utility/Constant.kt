package com.agvahealthcare.ventilator_ext.utility

const val VENTILATOR_DATA = "vent_data"
const val VENTILATOR_ACK = "vent_ack"
const val VENTILATOR_BATTERY_LEVEL = "vent_battery_level"
const val VENTILATOR_BATTERY_HEALTH = "vent_battery_health"
const val VENTILATOR_BATTERY_TTE = "vent_battery_tte"
const val VENTILATOR_HEATSENSE_DATA = "vent_heatsense_data"
const val VENTILATOR_DEV_NAME_RESPONSE = "vent_dev_name_response"
const val VENTILATOR_MOTOR_LIFE = "vent_motor_life"
const val VENTILATOR_HANDSHAKE_CALIBRATION = "vent_handshake_calibrated"
const val VENTILATOR_STANDBY_STATUS = "vent_standby_status"
const val VENTILATOR_SHUTDOWN_STATUS = "vent_shutdown_status"
const val VENTILATOR_WIFI_CONNECTION_RESPONSE = "vent_wifi_conn_response"
const val VENTILATOR_SELF_TEST_STATUS = "vent_stp_status"
const val VENTILATOR_WIFI_DEVS = "vent_wifi_devs"
const val VENTILATOR_SOFTWARE_VERSION = "vent_software_version"
const val VENTILATOR_RAW_DATA = "vent_raw_data"
const val VENTILATOR_CONTROL_SUB_MODE = "control_sub_mode"
const val VENTILATOR_DATA_SEND = "vent_data_send"
const val VENTILATOR_MODES = "vent_mode"

const val ALERT_MSG ="alert_msg"
const val ALERT_LABEL ="alert_label"

const val FIFO_CAPACITY = 340
const val GRAPH_THRESHOLD = FIFO_CAPACITY + 10

const val GRAPH_PRESSURE_MIN = 0

const val GRAPH_PRESSURE_MAX = 60

const val GRAPH_VOLUME_MIN = 0
const val GRAPH_VOLUME_MAX = 600

const val GRAPH_FLOW_MIN = -100
const val GRAPH_FLOW_MAX = 100

const val ADULT_GRAPH_PRESSURE_MIN = 40
const val ADULT_GRAPH_PRESSURE_MAX = 140

const val PED_GRAPH_PRESSURE_MIN = 20
const val PED_GRAPH_PRESSURE_MAX = 120

const val NEO_GRAPH_PRESSURE_MIN = 20
const val NEO_GRAPH_PRESSURE_MAX = 120

const val ADULT_GRAPH_VOLUME_MIN = 400
const val ADULT_GRAPH_VOLUME_MAX = 2000

const val PED_GRAPH_VOLUME_MIN = 200
const val PED_GRAPH_VOLUME_MAX = 2000

const val NEO_GRAPH_VOLUME_MIN = 100
const val NEO_GRAPH_VOLUME_MAX = 2000

const val ADULT_GRAPH_FLOW_MIN = 40
const val ADULT_GRAPH_FLOW_MAX = 200

const val PED_GRAPH_FLOW_MIN = 40
const val PED_GRAPH_FLOW_MAX = 200

const val NEO_GRAPH_FLOW_MIN = 40
const val NEO_GRAPH_FLOW_MAX = 200

const val TIMEOUT_TOUCH_INTERACTION = 1000 * 10
const val SETTINGS_SAVED_DIALOG_LIFETIME = 1000

const val TRIGG_BALANCE_COUNT = 1 //6

const val DYNAMIC_COMPLIANCE_THRESHOLD = 10f
const val LEAK_BASED_ALARM_THRESHOLD = 40f // SEPARATE VALUE ASSIGNED FOR LEAK DETECTION

const val HIGH_LEAK_INACCURACY_ALARM_THRESHOLD = 90f
const val MIN_EXPIRE_TIME_THRESHOLD = 0.6f
const val MAX_EXPIRE_TIME_THRESHOLD = 0.8f
const val MIN_LEAK_THRESHOLD = 50f
const val MAX_LEAK_THRESHOLD = 90f
const val THRESHOLD_RR_FOR_CUFF_LEAK = 40f
const val THRESHOLD_RR_FOR_FLOW_SENSOR = 80f
const val THRESHOLD_RR_FOR_TUBE_BLOCKAGE = 40f
const val COMPLIANCE_THRESHOLD_CYCLE_COUNT = 4f
const val EXPIRE_TIME_THRESHOLD_CYCLE_COUNT = 4f
const val LEAK_THRESHOLD_CYCLE_COUNT = 4f
const val HIGH_LEAK_INACCURACY_THRESHOLD_CYCLE_COUNT = 4f
const val LEAK_BASED_ALARM_THRESHOLD_CYCLE_COUNT = 4f
const val RR_THRESHOLD_CYCLE_COUNT = 4f
const val FIO2_DISCONNECT_ALARM_THRESHOLD_CYCLE_COUNT = 4f
const val FIO2_LEVEL_ALARM_THRESHOLD_CYCLE_COUNT = 10f
const val THRESHOLD_PEEP_DEVIATION = 2f
//    final static float VPEAKI_THRESHOLD = 86;  // Temporary fix
//    final static float VPEAKI_THRESHOLD = 86;  // Temporary fix
// tolerance levels
const val FIO2_TOLERANCE_THRESHOLD = 10f
const val VTI_TOLERANCE_PERCENT_THRESHOLD = 15f

const val CALIBRATION_DIALOG_STOP_WATCH = 1000
const val CALIBRATION_DIALOG_START_WATCH = 40 * 1000


//const val KEY_TITLE_VIEW = "KEY_TITLE_VIEW"
const val KEY_MIN_VALUE_VIEW = "KEY_MIN_VALUE_VIEW"
const val KEY_MAX_VALUE_VIEW = "KEY_MAX_VALUE_VIEW"

// knob limit settings
const val  UPPER_LIMIT="UPPER_LIMIT"
const val  LOWER_LIMIT="LOWER_LIMIT"

var EMPTY_PROFILE_ID = "0"
var ACTIVE_PROFILE_ID = "1"

const val VENTILATOR_CONTROL_KNOB = "control_sub_knob"
const val SENSOR_ANALYSIS = "vent_sensor"

const val VENTILATOR_SENSOR_CALIBRATION_RESULT = "sensor_calib_result"
const val VENTILATOR_SENSOR_CALIBRATION_TAG = "sensor_calib_tag"


//quick button hardware
const val ALARM_MUTE_UNMUTE_CONTROL="alarm_mute_unmute_control"
const val NEBULISER_CONTROL="nubliser_control"
const val OXYGEN_CONTROL="oxygen_control"
const val INSPIRATORY_HOLD_CONTROL="inspiratory_hold_control"
const val EXPIRATORY_HOLD_CONTROL="expiratory_hold_control"
const val MANUAL_BREATH_CONTROL="manual_breath_control"
const val HOME_CONTROL="home_control"
const val LOCK_CONTROL="lock_control"
const val POWER_SWITCH_CONTROL="power_switch_control"


const val LABEL_PATIENT = "Patient"
const val LABEL_MACHINE = "Machine"

const val LABEL_HEIGHT = "HEIGHT"
const val LABEL_WEIGHT = "WEIGHT"

const val LABEL_MALE = "MALE"
const val LABEL_FEMALE = "FEMALE"


const val PATIENT_ADULT_WEIGHT_LOWER : Int= 30
const val PATIENT_ADULT_WEIGHT_UPPER : Int= 200

const val PATIENT_ADULT_HEIGHT_LOWER : Int= 80
const val PATIENT_ADULT_HEIGHT_UPPER : Int= 250



const val PATIENT_AGE_LOWER : Int = 16
const val PATIENT_AGE_UPPER : Int = 100

const val NEO_AGE_LOWER : Int = 1
const val NEO_AGE_UPPER : Int = 28

const val NEO_HEIGHT_LOWER : Int = 25
const val NEO_HEIGHT_UPPER : Int = 90

const val NEO_WEIGHT_LOWER : Int = 1
const val NEO_WEIGHT_UPPER : Int = 5

const val PED_WEIGHT_LOWER : Int = 10
const val PED_WEIGHT_UPPER : Int = 70

const val PED_HEIGHT_LOWER : Int = 10
const val PED_HEIGHT_UPPER : Int = 190

const val PED_AGE_LOWER : Int = 1
const val PED_AGE_UPPER : Int = 16

/// Till this


const val VOLUME_MIN_VALUE = 2
const val VOLUME_MAX_VALUE = 10




const val IS_STAND_BY = "IS_STAND_BY"

//const val HEART_RATE ="HR"
//const val SPO2 ="SpO₂"

const val SPLASH_SCREEN_LIFE: Long = 100 // in millisec

const val LOG_TYPE_DEBUG = "debug"
const val LOG_TYPE_WARN = "warn"
const val LOG_TYPE_INFO = "info"
const val LOG_TYPE_ERROR = "error"
const val MODEL_TYPE = "001"

const val ERROR_LOG_THRESHOLD_INTERVAL = (1000 * 60 * 60 * 24 * 30 ).toLong() // 30 days
const val DATA_LOG_THRESHOLD_INTERVAL = (1000 * 60 * 60 * 24 * 30  ).toLong()// 30 days
const val ERROR_LOG_INTERVAL = (1000 * 5   ).toLong() // 5 sec
const val SIMILAR_ERROR_LOG_INTERVAL = (1000 * 30 ).toLong() // 30 sec

// Pplat -> Support Pressure or IPap or plateu Pressure
// PEEP -> Epap
// Pressure Limit -> Pinsp or PIP or Peak Pressure
// PeakFlow -> PFRI ->




