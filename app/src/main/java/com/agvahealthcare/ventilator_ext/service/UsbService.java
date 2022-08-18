package com.agvahealthcare.ventilator_ext.service;

import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.*;
/*
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_ACK;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_BATTERY_HEALTH;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_BATTERY_LEVEL;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_BATTERY_TTE;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_CONTROL_KNOB;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_CONTROL_SUB_MODE;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_DATA;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_DATA_SEND;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_DEV_NAME_RESPONSE;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_HANDSHAKE_CALIBRATION;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_HEATSENSE_DATA;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_MOTOR_LIFE;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_RAW_DATA;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_SELF_TEST_STATUS;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_SENSOR_CALIBRATION_RESULT;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_SENSOR_CALIBRATION_TAG;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_SOFTWARE_VERSION;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_STANDBY_STATUS;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_WIFI_CONNECTION_RESPONSE;
import static com.agvahealthcare.ventilator_ext.utility.ConstantKt.VENTILATOR_WIFI_DEVS;
*/

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;

import com.agvahealthcare.ventilator_ext.manager.PreferenceManager;
import com.agvahealthcare.ventilator_ext.utility.ToastFactory;
import com.agvahealthcare.ventilator_ext.utility.utils.Configs;
import com.agvahealthcare.ventilator_ext.utility.utils.IntentFactory;
import com.felhr.usbserial.UsbSerialDevice;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class UsbService extends CommunicationService{


    private static final int ARDUINO_VENDOR_ID_VENTILATOR = 9025;
    private static final int DEFAULT_BAUD_RATE_VENTILATOR = 9600;
    private static final int READ_DELAY = 10;

    private static final int ARDUINO_VENDOR_ID_HID = 1003;
    private static final int DEFAULT_BAUD_RATE_HID = 9600;


    private UsbManager usbManager;
    private UsbDevice usbDeviceVentilator;
    private UsbDevice usbDeviceHID;
    private UsbSerialDevice usbVentilator;
    private UsbSerialDevice usbHID;


    private final StringBuffer dataBufferVentilator = new StringBuffer();
    private final StringBuffer dataBufferHID = new StringBuffer();
    private Thread bufferReadingThreadVentilator;
    private Thread bufferReadingThreadHID;
    private PreferenceManager preferenceManager;

    /*
     * Reading thread : Constantly monitors the data buffer and reads the data frames
     * intercept the data into acknowledgements and vent data etc
     */
    private class ReadingRunnableVentilator implements Runnable {
        @Override
        public void run() {
            // reading and interception of received ventilator data
            Log.i("USB_BUFFER", "Reading thread started");
            while (true) {
                if (dataBufferVentilator.length() > 0) {
                    String buffData = dataBufferVentilator.toString();
                    try {
                        // To separated the ACKNOWLEDGEMENTS
                        if (buffData.contains(Configs.PREFIX_ACK)) {
                            // +1 for ACK code number (Don't remove -1 +1 this is for understanding)
                            int ackStartIndex = buffData.indexOf(Configs.PREFIX_ACK);
                            int ackTerminalIndex = buffData.indexOf(Configs.PREFIX_ACK) + Configs.PREFIX_ACK.length() + Configs.ACK_CODE_LENGTH - 1;
                            String ack = buffData.substring(ackStartIndex, ackTerminalIndex + 1);

                            int ackInt = Integer.valueOf(ack.replace("ACK",""));
                            try{
                                Log.i("ACKCHECKER",String.valueOf(ackInt));
                                //int acknew = Integer.parseInt(acktrimmed);

                                broadcastAcknowledgement(ack);
                                Log.d("ACKCODE",ack);


                                dataBufferVentilator.delete(ackStartIndex, ackTerminalIndex + 1);
                            }catch (Exception e){
                                dataBufferVentilator.delete(0, dataBufferVentilator.length());
                                Log.i("dataBufferLength",String.valueOf(dataBufferVentilator.length()));
                            }


                        } else if (buffData.contains(Configs.PREFIX_BATTERY)) {
                            // +1 for BATTERY STATUS code number (Don't remove -1 +1 this is for understanding)
                            int btryStartIndex = buffData.indexOf(Configs.PREFIX_BATTERY);
                            int btryTerminalIndex = buffData.indexOf(Configs.PREFIX_BATTERY) + Configs.PREFIX_BATTERY.length() + Configs.BATTERY_CODE_LENGTH - 1;
                            String batteryData = buffData.substring(btryStartIndex + Configs.PREFIX_BATTERY.length(), btryTerminalIndex + 1);
                            String batteryLevel = batteryData.substring(0, 3);
                            String batteryHealth = batteryData.substring(3, 6);
                            String batteryRemainingTime = batteryData.substring(6);
                            broadcastBatteryStatus(batteryLevel, batteryHealth, batteryRemainingTime);
                            dataBufferVentilator.delete(btryStartIndex, btryTerminalIndex + 1);

                        } else if (buffData.contains(Configs.PREFIX_MOTOR_LIFE)) {
                            // +1 for BATTERY STATUS code number (Don't remove -1 +1 this is for understanding)
                            int motorLifeStartIndex = buffData.indexOf(Configs.PREFIX_MOTOR_LIFE);
                            int motorLifeTerminalIndex = buffData.indexOf(Configs.PREFIX_MOTOR_LIFE) + Configs.PREFIX_MOTOR_LIFE.length() + Configs.MOTOR_LIFE_CODE_LENGTH - 1;
                            String motorLifeLevel = buffData.substring(motorLifeStartIndex + Configs.PREFIX_MOTOR_LIFE.length(), motorLifeTerminalIndex + 1);

                            broadcastMotorLifeLevelStatus(motorLifeLevel);

                            dataBufferVentilator.delete(motorLifeStartIndex, motorLifeTerminalIndex + 1);

                        } else if (buffData.contains(Configs.PREFIX_STANDBY)) {
                            // +1 for STANDBY STATUS code number (Don't remove -1 +1 this is for understanding)
                            int standbyResponseStartIndex = buffData.indexOf(Configs.PREFIX_STANDBY);
                            int standbyResponseTerminalIndex = buffData.indexOf(Configs.PREFIX_STANDBY) + Configs.PREFIX_STANDBY.length() + Configs.STANDBY_RESPONSE_LENGTH - 1;
                            String standbyResponse = buffData.substring(standbyResponseStartIndex + Configs.PREFIX_STANDBY.length(), standbyResponseTerminalIndex + 1);

                            broadcastStandbyResponse(standbyResponse);

                            dataBufferVentilator.delete(standbyResponseStartIndex, standbyResponseTerminalIndex + 1);

                        } else if (buffData.contains(Configs.PREFIX_WIFI_CONN)) {
                            // +1 for STANDBY STATUS code number (Don't remove -1 +1 this is for understanding)
                            int wifiConnResponseStartIndex = buffData.indexOf(Configs.PREFIX_WIFI_CONN);
                            int wifiConnResponseTerminalIndex = buffData.indexOf(Configs.PREFIX_WIFI_CONN) + Configs.PREFIX_WIFI_CONN.length() + Configs.WIFI_CONN_RESPONSE_LENGTH - 1;
                            String wifiConnResponse = buffData.substring(wifiConnResponseStartIndex + Configs.PREFIX_WIFI_CONN.length(), wifiConnResponseTerminalIndex + 1);

                            broadcastWifiConnectionResponse(wifiConnResponse);

                            dataBufferVentilator.delete(wifiConnResponseStartIndex, wifiConnResponseTerminalIndex + 1);

                        } else if (buffData.contains(Configs.PREFIX_SELFTEST)) {
                            // +1 for STANDBY STATUS code number (Don't remove -1 +1 this is for understanding)
                            int selftestResponseStartIndex = buffData.indexOf(Configs.PREFIX_SELFTEST);
                            int selftestResponseTerminalIndex = buffData.indexOf(Configs.PREFIX_SELFTEST) + Configs.PREFIX_SELFTEST.length() + Configs.SELFTEST_RESPONSE_LENGTH - 1;
                            String selftestResponse = buffData.substring(selftestResponseStartIndex + Configs.PREFIX_SELFTEST.length(), selftestResponseTerminalIndex + 1);

                            broadcastSelfTestResponse(selftestResponse);

                            dataBufferVentilator.delete(selftestResponseStartIndex, selftestResponseTerminalIndex + 1);

                        } else if (buffData.contains(Configs.PREFIX_DEVICE_NAME_REQUEST)) {
                            // +1 for STANDBY STATUS code number (Don't remove -1 +1 this is for understanding)
                            int deviceNameReqStartIndex = buffData.indexOf(Configs.PREFIX_DEVICE_NAME_REQUEST);
                            int deviceNameReqTerminalIndex = buffData.indexOf(Configs.PREFIX_DEVICE_NAME_REQUEST) + Configs.PREFIX_DEVICE_NAME_REQUEST.length() + Configs.DEVICE_NAME_REQUEST_LENGTH - 1;
                            String deviceNameReqResponse = buffData.substring(deviceNameReqStartIndex + Configs.PREFIX_DEVICE_NAME_REQUEST.length(), deviceNameReqTerminalIndex + 1);

                            broadcastDeviceNameRequested(deviceNameReqResponse);

                            dataBufferVentilator.delete(deviceNameReqStartIndex, deviceNameReqTerminalIndex + 1);

                        } else if (buffData.contains(Configs.PREFIX_HANDSHAKE_CALIBRATE)) {
                            Log.i("RAWREAD", buffData.substring(buffData.indexOf(Configs.PREFIX_HANDSHAKE_CALIBRATE)));
                            // +1 for STANDBY STATUS code number (Don't remove -1 +1 this is for understanding)
                            int handshakeCalibrationStartIndex = buffData.indexOf(Configs.PREFIX_HANDSHAKE_CALIBRATE);
                            int handshakeCalibrationTerminalIndex = buffData.indexOf(Configs.PREFIX_HANDSHAKE_CALIBRATE) + Configs.PREFIX_HANDSHAKE_CALIBRATE.length() + Configs.HANDSHAKE_CALIBRATE_LENGTH - 1;
                            String handshakeCalibrationValue = buffData.substring(handshakeCalibrationStartIndex + Configs.PREFIX_HANDSHAKE_CALIBRATE.length(), handshakeCalibrationTerminalIndex + 1);

                            broadcastHandshakeCalibration(handshakeCalibrationValue);

                            dataBufferVentilator.delete(handshakeCalibrationStartIndex, handshakeCalibrationTerminalIndex + 1);

                        } else if (buffData.contains(Configs.PREFIX_HEATSENSE)) {
                            // +1 for HEAT SENSE STATUS code number (Don't remove -1 +1 this is for understanding)
                            int btryStartIndex = buffData.indexOf(Configs.PREFIX_HEATSENSE);
                            int btryTerminalIndex = buffData.indexOf(Configs.PREFIX_HEATSENSE) + Configs.PREFIX_HEATSENSE.length() + Configs.HEATSENSE_CODE_LENGTH - 1;
                            String sensorData = buffData.substring(btryStartIndex + Configs.PREFIX_HEATSENSE.length(), btryTerminalIndex + 1);
                            String sensor1 = sensorData.substring(0, 3);
                            String sensor2 = sensorData.substring(3, 6);
                            String sensor3 = sensorData.substring(6, 9);
                            String sensor4 = sensorData.substring(9);

                            ArrayList<String> sensorDataList = new ArrayList<>();
                            sensorDataList.add(sensor1);
                            sensorDataList.add(sensor2);
                            sensorDataList.add(sensor3);
                            sensorDataList.add(sensor4);

                            broadcastHeatSensorStatus(sensorDataList);

                            dataBufferVentilator.delete(btryStartIndex, btryTerminalIndex + 1);

                        } else {

                            // Ventilator data
                            if (buffData.contains("#")) {

                                int dataTerminalIndex = buffData.indexOf("#");
                                String data = buffData.substring(0, dataTerminalIndex + 1);

                                broadcastData(data);

                                dataBufferVentilator.delete(0, dataTerminalIndex + 1);
                            }


                            // scanned WIFI devices
                            if (buffData.contains("[")) {
                                if (buffData.contains("]")) {
                                    int wifiDataStartIndex = buffData.indexOf("[");
                                    int wifiDataTerminalIndex = buffData.indexOf("]");
                                    if (wifiDataStartIndex < wifiDataTerminalIndex) {
                                        String wifiData = buffData.substring(wifiDataStartIndex, wifiDataTerminalIndex + 1);
                                        broadcastScannedWifiDevices(wifiData);
                                        dataBufferVentilator.delete(wifiDataStartIndex, wifiDataTerminalIndex + 1);
                                    }
                                }
                            }

                            if (buffData.contains(Configs.PREFIX_HARDWARE_VERSION)) {
                                if (buffData.contains("$")) {
                                    int updationStartIndex = buffData.indexOf(Configs.PREFIX_HARDWARE_VERSION);
                                    int updationTerminalIndex = buffData.indexOf("$");
                                    if (updationStartIndex < updationTerminalIndex) {
                                        String softwareUpdateData = buffData.substring(updationStartIndex, updationTerminalIndex);
                                        broadcastSoftwareVersion(softwareUpdateData);
                                        dataBufferVentilator.delete(updationStartIndex, updationTerminalIndex + 1);
                                    }
                                }
                            }

                            if(buffData.contains(Configs.PREFIX_SENSOR_AVAILABILITY)){

                                if(buffData.contains(Configs.PREFIX_SENSOR_AVAILABILITY)){
                                    int sensorStartIndex = buffData.indexOf(Configs.PREFIX_SENSOR_AVAILABILITY) ;
                                    int sensorTerminalIndex = buffData.indexOf(Configs.SUFIX_SENSOR_AVAILABILITY);
                                    if (sensorStartIndex < sensorTerminalIndex) {
                                        String sensorData = buffData.substring(sensorStartIndex + Configs.PREFIX_SENSOR_AVAILABILITY.length(), sensorTerminalIndex);
                                        broadcastSensorAnalysis(sensorData);
                                        dataBufferVentilator.delete(sensorStartIndex, sensorTerminalIndex + 1);
                                    }

                                }

                            }

                            if(buffData.contains(Configs.PREFIX_SENSOR_CALIBRATION)){

                                int sensorCalibStartIndex = buffData.indexOf(Configs.PREFIX_SENSOR_CALIBRATION);
                                int sensorCalibTerminalIndex = buffData.indexOf(Configs.PREFIX_SENSOR_CALIBRATION) + Configs.PREFIX_SENSOR_CALIBRATION.length() + Configs.SENSOR_CALIBRATION_REQUEST_LENGTH - 1;

                                if (sensorCalibStartIndex < sensorCalibTerminalIndex) {
                                    String sensorCalibrationData = buffData.substring(sensorCalibStartIndex + Configs.PREFIX_SENSOR_CALIBRATION.length(), sensorCalibTerminalIndex + 1);
                                    if(sensorCalibrationData.length() == Configs.SENSOR_CALIBRATION_REQUEST_LENGTH){
                                        broadcastCalibrationSensorAnalysis(sensorCalibrationData.substring(0, Configs.TAG_SENSOR_LENGTH), sensorCalibrationData.substring(1, Configs.SENSOR_CALIBRATION_REQUEST_LENGTH));
                                        dataBufferVentilator.delete(sensorCalibStartIndex, sensorCalibTerminalIndex + 1);
                                    }
                                }
                            }
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        Log.i("READ_THREAD_CHECK", "Index shortage");
                        e.printStackTrace();
//                        FileLogger.Companion.e(UsbService.this, e);
                    }

                }

                try {
                    Thread.sleep(READ_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class ReadingRunnableHID implements Runnable {
        @Override
        public void run() {
            // reading and interception of received ventilator data
            // Log.i("USB_BUFFER2", "Reading thread started");
            while (true) {
                if (dataBufferHID.length() > 0) {

                    String buffData2 = dataBufferHID.toString();
                    Log.i("USB_BUFFER2", "Reading thread started  " + buffData2.contains(Configs.PREFIX_MINUS) + " " + buffData2);

                    try {


                        if (buffData2.contains(Configs.PREFIX_PLUS)) {

                            int prefixPlusStartIndex = buffData2.indexOf(Configs.PREFIX_PLUS);
                            int prefixPlusTerminalIndex = buffData2.indexOf(Configs.PREFIX_PLUS) + Configs.KNOB_LENGTH;
                            String plusValue = buffData2.substring(prefixPlusStartIndex, prefixPlusTerminalIndex);
                            //  Log.i("READ THREAD CHECK plus", "Index shortage "+motorLifeLevel);
                            // buffData2="";
                            broadcastKnobResponse(plusValue);
                            dataBufferHID.delete(prefixPlusStartIndex, prefixPlusTerminalIndex + 1);

                        } else if (buffData2.contains(Configs.PREFIX_MINUS)) {
                            int prefixMinusStartIndex = buffData2.indexOf(Configs.PREFIX_MINUS);
                            int prefixMinusTerminalIndex = buffData2.indexOf(Configs.PREFIX_MINUS) + Configs.KNOB_LENGTH;
                            String minusValue = buffData2.substring(prefixMinusStartIndex, prefixMinusTerminalIndex);
                            //  Log.i("READ THREAD CHECK minus", "Index shortage "+motorLifeLevel);
                            // buffData2="";
                            broadcastKnobResponse(minusValue);
                            dataBufferHID.delete(prefixMinusStartIndex, prefixMinusTerminalIndex + 1);

                        } else if (buffData2.contains(Configs.PREFIX_AND)) {
                            int prefixAndStartIndex = buffData2.indexOf(Configs.PREFIX_AND);
                            int prefixAndTerminalIndex = buffData2.indexOf(Configs.PREFIX_AND) + Configs.KNOB_LENGTH;
                            String andValue = buffData2.substring(prefixAndStartIndex, prefixAndTerminalIndex);


                            // Log.i("READ THREAD CHECK push", "Index shortage "+motorLifeLevel);
                            // buffData2="";
                            broadcastKnobResponse(andValue);
                            dataBufferHID.delete(prefixAndStartIndex, prefixAndTerminalIndex + 1);

                        } else if (buffData2.contains(Configs.QB_ALARM_MUTE_UNMUTE)) {
                            // Log.i("READ THREAD CHECK push", "Index shortage "+buffData2);
                            int muteOptionStartIndex = buffData2.indexOf(Configs.QB_ALARM_MUTE_UNMUTE);

                            broadcastAlarmMuteUmuteResponse();
                            dataBufferHID.delete(muteOptionStartIndex, muteOptionStartIndex + Configs.QB_ALARM_MUTE_UNMUTE.length());

                        } else if (buffData2.contains(Configs.QB_NEBULISER)) {
                            //Log.i("READ THREAD CHECK push", "Index shortage "+buffData2);
                            int nubliserStartIndex = buffData2.indexOf(Configs.QB_NEBULISER);


                            //Log.i("READ THREAD CHECK push", "Index shortage "+motorLifeLevel);
                            // buffData2="";
                            broadcastNebuliserResponse();
                            dataBufferHID.delete(nubliserStartIndex, nubliserStartIndex + Configs.QB_NEBULISER.length());

                        } else if (buffData2.contains(Configs.QB_OXYGEN)) {
                            //  Log.i("READ THREAD CHECK push", "Index shortage "+buffData2);
                            int oxygenStartIndex = buffData2.indexOf(Configs.QB_OXYGEN);


                            //Log.i("READ THREAD CHECK push", "Index shortage "+motorLifeLevel);
                            // buffData2="";
                            broadcastOxygenResponse();
                            dataBufferHID.delete(oxygenStartIndex, oxygenStartIndex + Configs.QB_OXYGEN.length());

                        } else if (buffData2.contains(Configs.QB_INSPIRATORY_HOLD)) {
                            //   Log.i("READ THREAD CHECK push", "Index shortage "+buffData2);
                            int inspiratoryStartIndex = buffData2.indexOf(Configs.QB_INSPIRATORY_HOLD);

                            Log.d("ValueOf",String.valueOf(inspiratoryStartIndex));
                            // Log.i("READ THREAD CHECK push", "Index shortage "+motorLifeLevel);
                            // buffData2="";
                            broadcastInspiratoryHoldResponse();
                            dataBufferHID.delete(inspiratoryStartIndex, inspiratoryStartIndex + Configs.QB_INSPIRATORY_HOLD.length());

                        } else if (buffData2.contains(Configs.QB_EXPIRATORY_HOLD)) {
                            // Log.i("READ THREAD CHECK push", "Index shortage "+buffData2);
                            int expiratoryStartIndex = buffData2.indexOf(Configs.QB_EXPIRATORY_HOLD);
                            //Log.i("READ THREAD CHECK push", "Index shortage "+motorLifeLevel);
                            // buffData2="";
                            Log.d("ValueOf",String.valueOf(expiratoryStartIndex));
                            broadcastExpiratoryHoldResponse();
                            dataBufferHID.delete(expiratoryStartIndex, expiratoryStartIndex + Configs.QB_EXPIRATORY_HOLD.length());

                        } else if (buffData2.contains(Configs.QB_MANUAL_BREATH)) {
                            //  Log.i("READ THREAD CHECK push", "Index shortage "+buffData2);
                            int manualStartIndex = buffData2.indexOf(Configs.QB_MANUAL_BREATH);


                            //Log.i("READ THREAD CHECK push", "Index shortage "+motorLifeLevel);
                            // buffData2="";
                            broadcastManualBreathResponse();
                            dataBufferHID.delete(manualStartIndex, manualStartIndex + Configs.QB_MANUAL_BREATH.length());

                        } else if (buffData2.contains(Configs.QB_HOME)) {
                            // Log.i("READ THREAD CHECK push", "Index shortage "+buffData2);
                            int homeStartIndex = buffData2.indexOf(Configs.QB_HOME);


                            // Log.i("READ THREAD CHECK push", "Index shortage "+motorLifeLevel);
                            // buffData2="";
                            broadcastHomeResponse();
                            dataBufferHID.delete(homeStartIndex, homeStartIndex + Configs.QB_HOME.length());

                        } else if (buffData2.contains(Configs.QB_LOCK)) {
                            Log.i("READ THREAD CHECK push", "Index shortage " + buffData2);
                            int lockStartIndex = buffData2.indexOf(Configs.QB_LOCK);


                            // Log.i("READ THREAD CHECK push", "Index shortage "+motorLifeLevel);
                            // buffData2="";
                            // if (AppUtils.LOCK)
                            broadcastLockResponse();
                            dataBufferHID.delete(lockStartIndex, lockStartIndex + Configs.QB_LOCK.length());

                        } else if (buffData2.contains(Configs.QB_POWER_SWITCH)) {
                            // Log.i("READ THREAD CHECK push", "Index shortage "+buffData2);
                            int switchStartIndex = buffData2.indexOf(Configs.QB_POWER_SWITCH);


                            //Log.i("READ THREAD CHECK push", "Index shortage "+motorLifeLevel);
                            // buffData2="";

                            broadcastPowerSwitchResponse();
                            dataBufferHID.delete(switchStartIndex, switchStartIndex + Configs.QB_POWER_SWITCH.length());

                        } else {

                        }


                        // To separated the ACKNOWLEDGEMENTS


                    } catch (StringIndexOutOfBoundsException e) {
                        Log.i("READ THREAD CHECK", "Index shortage");
                        e.printStackTrace();
//                        FileLogger.Companion.e(UsbService.this, e);
                    }

                }
                /*try {
                    Thread.sleep(READ_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        }

    }

    ;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null || intent.getAction() == null) return;

            switch (intent.getAction()) {
                case IntentFactory.ACTION_USB_PERMISSION_VENTILATOR:
                    Log.i("USB_SERVICE_STATUS", "USB PERMISSION VENTILATOR");
                    if (intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED))
                        openConnectionToReadVentilator();
                    break;

                case IntentFactory.ACTION_USB_PERMISSION_HID:
                    Log.i("USB_SERVICE_STATUS_HID", "USB PERMISSION HID");
                    if (intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED))
                        openConnectionToReadHID();
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    Log.i("USB_SERVICE_STATUS", "USB STATE_CONNECTED");
//                    preferenceManager.setBoundDevice(usb.getDevice(), gatt.getDevice().getName());
                    sendBroadcast(new Intent(IntentFactory.ACTION_DEVICE_CONNECTED));
                    break;

                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    Log.e("USB_SERVICE_STATUS", "USB STATE_DISCONNECTED");
                    sendBroadcast(new Intent(IntentFactory.ACTION_DEVICE_DISCONNECTED));
                    usbVentilator = null;
                    preferenceManager.setBoundDevice(null, null);
                    break;
            }
        }
    };

    private IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(IntentFactory.ACTION_USB_PERMISSION_VENTILATOR);
        filter.addAction(IntentFactory.ACTION_USB_PERMISSION_HID);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        return filter;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        preferenceManager = new PreferenceManager(this);
        this.usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        registerReceiver(receiver, getIntentFilter());


        Log.i("USB_SERVICE_STATUS", "Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferenceManager.setServiceStatus(true);
        Log.i("USB_SERVICE_STATUS", "Started");
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.i("USB_SERVICE_STATUS", "Destroyed");
        unregisterReceiver(receiver);
        closeConnection();
        preferenceManager.setServiceStatus(false);   // NEW SHARED PREFERENCE STATUS
        usbHID = null;
        super.onDestroy();

    }

    @Override
    public boolean isVentilatorConnected() {
        for (Map.Entry<String, UsbDevice> entry : usbManager.getDeviceList().entrySet()) {
            if (entry.getValue().getVendorId() == ARDUINO_VENDOR_ID_VENTILATOR) return true;
        }
        return false;
    }

    @Override
    public boolean isHIDConnected() {
        for (Map.Entry<String, UsbDevice> entry : usbManager.getDeviceList().entrySet()) {
            if (entry.getValue().getVendorId() == ARDUINO_VENDOR_ID_HID) return true;
        }
        return false;
    }

    @Override
    public void sendBroadcastHandshakeCompleted() {
        Log.i("HANDSHAKE CHECK", "Double handshake completed");
        sendBroadcast(new Intent(IntentFactory.ACTION_HANDSHAKE_COMPLETED));
    }

    @Override
    protected void broadcastAcknowledgement(String ack) {
        Log.w("ACK CHECK", ack);
        Intent i = new Intent(IntentFactory.ACTION_ACK_AVAILABLE);
        i.putExtra(VENTILATOR_ACK, ack);
        sendBroadcast(i);
    }

    @Override
    protected void broadcastBatteryStatus(String btryLevel, String btryHealth, String remainingTime) {
        Log.w("BATTERY CHECK", btryLevel);
        try {
            Intent i = new Intent(IntentFactory.ACTION_BATTERY_STATUS_AVAILABLE);
            i.putExtra(VENTILATOR_BATTERY_LEVEL, Integer.valueOf(btryLevel));
            i.putExtra(VENTILATOR_BATTERY_HEALTH, Integer.valueOf(btryHealth));
            i.putExtra(VENTILATOR_BATTERY_TTE, Integer.valueOf(remainingTime));
            sendBroadcast(i);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // ServerLogger.Companion.e(UsbService.this, e);
        }
    }

    @Override
    protected void broadcastHeatSensorStatus(ArrayList<String> values) {
        Log.w("HEATSENSE_CHECK", values.toString());
        try{
            Intent i = new Intent(IntentFactory.ACTION_HEATSENSE_STATUS_AVAILABLE);
            i.putIntegerArrayListExtra(VENTILATOR_HEATSENSE_DATA, new ArrayList<Integer>(values.stream().map(Integer::valueOf).collect(Collectors.toList())));
            sendBroadcast(i);
        } catch(Exception e){
            Log.w("HEATSENSE_CHECK", "Unable to parse some values");

            e.printStackTrace();
            // ServerLogger.Companion.e(UsbService.this, e);
        }
    }

    @Override
    protected void broadcastSensorAnalysis(String sensorAnalysis) {
        Log.w("SENSOR_ANALYSIS_CHECK", String.valueOf(sensorAnalysis));
        try {
            Intent i = new Intent(IntentFactory.ACTION_SENSOR_AVAILABILITY_RESPONSE);
            String[] value=sensorAnalysis.split(",");
            ArrayList<String> listValue= new ArrayList<String>();
            for (int j=0;j<value.length;j++) {
                listValue.add(value[j]);
            }
            i.putStringArrayListExtra(SENSOR_ANALYSIS,  listValue);
            sendBroadcast(i);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //  ServerLogger.Companion.e(UsbService.this, e);
        }
    }

    @Override
    protected void broadcastCalibrationSensorAnalysis(String sensorTag, String calibrationStatus) {

        Log.w("CALIBRATION_CHECK", sensorTag + " sensor calibration = " + calibrationStatus);

        try {

            Intent i = new Intent(IntentFactory.ACTION_SENSOR_CALIBRATION_RESPONSE);
            i.putExtra(VENTILATOR_SENSOR_CALIBRATION_TAG, sensorTag);
            i.putExtra(VENTILATOR_SENSOR_CALIBRATION_RESULT, Integer.valueOf(calibrationStatus));

            sendBroadcast(i);
        } catch (NumberFormatException e){
            e.printStackTrace();
            //  ServerLogger.Companion.e(UsbService.this, e);
        }

    }


    @Override
    protected void broadcastDeviceNameRequested(String deviceNameReqCode) {
        Log.w("DEVICENAME CHECK", "Requested by ventilator");
        try {
            if (deviceNameReqCode != null) {
                Intent i = null;
                switch (deviceNameReqCode) {
                    case "0":
                        i = new Intent(IntentFactory.ACTION_DEVICE_NAME_REQUESTED);
                        break;

                    case "1":
                        i = new Intent(IntentFactory.ACTION_DEVICE_NAME_RESPONSED);
                        i.putExtra(VENTILATOR_DEV_NAME_RESPONSE, true);
                        break;

                    case "2":
                        i = new Intent(IntentFactory.ACTION_DEVICE_NAME_RESPONSED);
                        i.putExtra(VENTILATOR_DEV_NAME_RESPONSE, false);
                        break;
                }

                if (i != null) sendBroadcast(i);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //  ServerLogger.Companion.e(UsbService.this, e);
        }
    }

    @Override
    protected void broadcastMotorLifeLevelStatus(String motorLifeLevel) {
        Log.w("MOTORLIFE CHECK", motorLifeLevel);
        try {
            Intent i = new Intent(IntentFactory.ACTION_MOTOR_LIFE_STATUS_AVAILABLE);
            i.putExtra(VENTILATOR_MOTOR_LIFE, Integer.valueOf(motorLifeLevel));
            sendBroadcast(i);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // ServerLogger.Companion.e(UsbService.this, e);
        }
    }

    private void broadcastHandshakeCalibration(String calibrationValue) {
        Log.i("HS_CALIB_CHECK", calibrationValue);
        try {
            Intent i = new Intent(IntentFactory.ACTION_HANDSHAKE_CALIBRATION_AVAILABLE);
            i.putExtra(VENTILATOR_HANDSHAKE_CALIBRATION, calibrationValue);
            sendBroadcast(i);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //   ServerLogger.Companion.e(UsbService.this, e);
        }
    }


    @Override
    protected void broadcastStandbyResponse(String standbyResponse) {
        Log.w("STANDBY_CHECK", standbyResponse);
        try {
            Intent i = new Intent(IntentFactory.ACTION_STANDBY_STATUS_AVAILABLE);
            i.putExtra(VENTILATOR_STANDBY_STATUS, Integer.valueOf(standbyResponse));
            sendBroadcast(i);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //      ServerLogger.Companion.e(UsbService.this, e);
        }
    }


    @Override
    protected void broadcastWifiConnectionResponse(String wifiConnectionResponse) {


        Log.w("STANDBY_CHECK", wifiConnectionResponse);
        try {
            Intent i = new Intent(IntentFactory.ACTION_VENTILATOR_WIFI_CONNECTION_RESPONSED);
            i.putExtra(VENTILATOR_WIFI_CONNECTION_RESPONSE, Integer.valueOf(wifiConnectionResponse) == 1);
            sendBroadcast(i);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //ServerLogger.Companion.e(UsbService.this, e);
        }
    }

    @Override
    protected void broadcastSelfTestResponse(String selftestResponse) {
        Log.w("SELFTEST_CHECK", selftestResponse);
        try {
            Intent i = new Intent(IntentFactory.ACTION_SELF_TEST_STATUS_AVAILABLE);
            i.putExtra(VENTILATOR_SELF_TEST_STATUS, Integer.valueOf(selftestResponse));
            sendBroadcast(i);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //ServerLogger.Companion.e(UsbService.this, e);
        }
    }


    @Override
    protected void broadcastData(String data) {
        Log.w("SMART DATA CHECK", data);
        Intent i = new Intent(IntentFactory.ACTION_DATA_AVAILABLE);
        i.putExtra(VENTILATOR_DATA, data);
        sendBroadcast(i);
    }

    @Override
    protected void broadcastScannedWifiDevices(String devicesJson) {
        Log.w("PLAIN DATA CHECK", devicesJson);
        try {
            JSONArray json = new JSONArray(devicesJson);
            boolean isJsonValid = (json.length() > 0);
            if (isJsonValid) {
                Intent i = new Intent(IntentFactory.ACTION_VENTILATOR_WIFI_CONNECTION_REQUESTED);
                i.putExtra(VENTILATOR_WIFI_DEVS, devicesJson);
                sendBroadcast(i);
            }
        } catch (JSONException e) {
            Log.i("WIFIDEVICES CHECK", "[INVALID JSON] Unable to parse the data from ventilator");
            e.printStackTrace();
            //ServerLogger.Companion.e(UsbService.this, e);

        }

    }


    @Override
    protected void broadcastSoftwareVersion(String softwareUpdateData) {
        Log.w("SOFTVERSION CHECK", softwareUpdateData);
        try {
            Intent i = new Intent(IntentFactory.ACTION_SOFTWARE_VERSION_AVAILABLE);
            i.putExtra(VENTILATOR_SOFTWARE_VERSION, softwareUpdateData);
            sendBroadcast(i);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //ServerLogger.Companion.e(UsbService.this, e);
        }
    }


    @Override
    protected void broadcastRawData(String data) {
        Log.w("PLAIN DATA CHECK", data);
        Intent i = new Intent(IntentFactory.ACTION_RAW_DATA_AVAILABLE);
        i.putExtra(VENTILATOR_RAW_DATA, data);
        sendBroadcast(i);
    }

    @Override
    public void startReading() {


        if (usbManager != null) {
            for (Map.Entry<String, UsbDevice> entry : usbManager.getDeviceList().entrySet()) {
                if (entry.getValue().getVendorId() == ARDUINO_VENDOR_ID_VENTILATOR) {
//                    Log.i("USB_READ_vener", "Received vendor id" + entry.getValue().getVendorId());
                    usbDeviceVentilator = entry.getValue();
                    //  break;
                }
                if (entry.getValue().getVendorId() == ARDUINO_VENDOR_ID_HID) {
//                    Log.i("USB_READ_ENCODER", "Received vendor id" + entry.getValue().getVendorId());
                    usbDeviceHID = entry.getValue();
                }

                Log.i("USB_READ_ENCODER", "Received vendor id" + entry.getValue().getVendorId());

            }


            if (usbDeviceVentilator != null) {
                Intent intent = new Intent(IntentFactory.ACTION_USB_PERMISSION_VENTILATOR);
                usbManager.requestPermission(usbDeviceVentilator, PendingIntent.getBroadcast(UsbService.this, 0, intent, 0));
            }
            if (usbDeviceHID != null) {
                Intent intent = new Intent(IntentFactory.ACTION_USB_PERMISSION_HID);
                usbManager.requestPermission(usbDeviceHID, PendingIntent.getBroadcast(UsbService.this, 0, intent, 0));
            }
        }

        // starting watch dog surveillance
        new Handler().postDelayed(this::startWatchDog, 500);


        // start monitoring buffer and read data concurrently
        if (bufferReadingThreadVentilator == null)
            bufferReadingThreadVentilator = new Thread(new ReadingRunnableVentilator());
        if (!bufferReadingThreadVentilator.isAlive()) bufferReadingThreadVentilator.start();

        if (bufferReadingThreadHID == null)
            bufferReadingThreadHID = new Thread(new ReadingRunnableHID());
        if (!bufferReadingThreadHID.isAlive()) bufferReadingThreadHID.start();


        Log.i("THREAD_CHECK", "Ventilator Thread name = " + bufferReadingThreadVentilator.getName() + " | id = " + bufferReadingThreadVentilator.getId());
        Log.i("THREAD_CHECK", "HID Thread name = " + bufferReadingThreadHID.getName() + " | id = " + bufferReadingThreadHID.getId());

    }

    @Override
    public void stopReading() {
        stopWatchDog();

        if (bufferReadingThreadVentilator != null) {
            if (bufferReadingThreadVentilator.isAlive() && !bufferReadingThreadVentilator.isInterrupted()) {
                bufferReadingThreadVentilator.interrupt();
            }
        }

        if (bufferReadingThreadHID != null) {
            if (bufferReadingThreadHID.isAlive() && !bufferReadingThreadHID.isInterrupted()) {
                bufferReadingThreadHID.interrupt();
            }
        }

    }

    @Override
    public void send(String data) {
        Log.i("WRITE_CHECK", "PRE_DATA : " + data);

        if (usbVentilator != null) {
            if (data.isEmpty()) return;
            Log.i("WRITE_CHECK", "NO_EMPTY_DATA : " + data);
            if (isVentilatorMode(data)) {
                int mode = Integer.parseInt(data);
                // data manipulation at the end
                // BPAP mapped to PSV
                //data = (mode == Configs.MODE_NIV_BPAP) ? String.valueOf(Configs.MODE_PC_PSV) : data;  //ToDo : Temparary  BPAP removed
                // SPONT mapped to BPAP
                data = (mode == Configs.MODE_PC_SPONT_DUMMY) ? String.valueOf(Configs.MODE_NIV_BPAP) : data;
                Log.d("databeingsend",data);
                if (Configs.isOxygenLevelsAvailable) {
                    final boolean isHighOxygenRequired = isOxygenSupportedVentilatorMode(data) && preferenceManager.readOxygenLevelStatus();
                    final boolean isAIVent = mode == Configs.MODE_AUTO_VENTILATION;

                    // AIVENT Default has high Oxygen
                    // sending data as MODE + OXYGEN LEVEL
//                    final int oxyLevel = (isHighOxygenRequired) ? 1 : 0;

                    usbVentilator.write((data).getBytes());

                    //     final int oxyLevel = 1;
                    //     usbVentilator.write((data + oxyLevel).getBytes());
                } else
                    //   usbVentilator.write((data + 1).getBytes());   // Forced High Oxygen mode [Temporary fix]
                    usbVentilator.write((data).getBytes());


                Log.i("MODE_CHECK", "Sending mode = " + data + " to ventilator");
                Intent i = new Intent(IntentFactory.ACTION_SUBMODE_SET);
                i.putExtra(VENTILATOR_CONTROL_SUB_MODE, mode);
                sendBroadcast(i);

            } else {
                usbVentilator.write((data).getBytes());

                Intent i = new Intent(IntentFactory.ACTION_DATA_SENT);
                i.putExtra(VENTILATOR_DATA_SEND, data);
                sendBroadcast(i);
            }

            Log.i("WRITE_CHECK", "DATA : " + data);
        } else if (usbHID != null) {

        }
    }

    private String appendStringTermination(String str) {
//        return str + '\0';
        return str ;
    }

    @Override
    protected void broadcastKnobResponse(String knobResponse) {
        if (knobResponse != null) {
            Log.i("KNOB_CHECK", "DATA : " + knobResponse);
            Intent i = new Intent(IntentFactory.ACTION_KNOB_CHANGE);
            i.putExtra(VENTILATOR_CONTROL_KNOB, knobResponse);
            sendBroadcast(i);
        }
    }


    @Override
    protected void broadcastAlarmMuteUmuteResponse() {
        sendBroadcast(new Intent(IntentFactory.ACTION_MUTE_UNMUTE));
    }


    @Override
    protected void broadcastNebuliserResponse() {
        sendBroadcast(new Intent(IntentFactory.ACTION_NEBULISER));
    }

    @Override
    protected void broadcastOxygenResponse() {
        sendBroadcast(new Intent(IntentFactory.ACTION_OXYGEN_100));
    }

    @Override
    protected void broadcastInspiratoryHoldResponse() {
        sendBroadcast(new Intent(IntentFactory.ACTION_INSPIRATORY_HOLD));
    }

    @Override
    protected void broadcastExpiratoryHoldResponse() {
        sendBroadcast(new Intent(IntentFactory.ACTION_EXPIRATORY_HOLD));
    }

    @Override
    protected void broadcastManualBreathResponse() {
        sendBroadcast(new Intent(IntentFactory.ACTION_MANUAL_BREATH));
    }

    @Override
    protected void broadcastHomeResponse() {

        sendBroadcast(new Intent(IntentFactory.ACTION_HOME));
    }

    @Override
    protected void broadcastLockResponse() {

        //  sendBroadcast(new Intent(IntentFactory.ACTION_LOCK));
    }

    @Override
    protected void broadcastPowerSwitchResponse() {
        sendBroadcast(new Intent(IntentFactory.ACTION_POWER_SWITCH));

    }


    private boolean isVentilatorMode(String ventData) {
        try {
            return Configs.isValidVentilatorMode(getApplicationContext(), Integer.parseInt(ventData));
        } catch (Exception e) {
            e.printStackTrace();
            // ServerLogger.Companion.e(UsbService.this, e);
            return false;
        }

    }

    private boolean isOxygenSupportedVentilatorMode(String ventData) {
        try {
            return Configs.isValidOxygenSupportedMode(getApplicationContext(), Integer.parseInt(ventData));
        } catch (Exception e) {
            e.printStackTrace();
            //ServerLogger.Companion.e(UsbService.this, e);
            return false;
        }

    }

    public void closeConnection() {
        if (usbVentilator != null) {
            usbVentilator.close();
            usbVentilator = null;
        }
    }


    private void openConnectionToReadVentilator() {
        if (usbManager != null) {
            if (usbDeviceVentilator != null) {
                try {

                    usbVentilator = UsbSerialDevice.createUsbSerialDevice(usbDeviceVentilator, usbManager.openDevice(usbDeviceVentilator));
                    if (usbVentilator != null && usbVentilator.open()) {
                        usbVentilator.setDataBits(UsbSerialDevice.DATA_BITS_8);
                        usbVentilator.setStopBits(UsbSerialDevice.STOP_BITS_1);
                        usbVentilator.setParity(UsbSerialDevice.PARITY_NONE);
                        usbVentilator.setFlowControl(UsbSerialDevice.FLOW_CONTROL_OFF);
                        usbVentilator.setBaudRate(DEFAULT_BAUD_RATE_VENTILATOR);

                        Log.i("USB_READ", "Connection established");

                        usbVentilator.read(data -> readBytesDataVentilator(new String(data)));
                    }


                } catch (Exception e) {
                    Log.i("USB_CHECK", "Permissions are not granted to USB");
                    e.printStackTrace();
                }
            }
        }

    }


    private void openConnectionToReadHID() {
        if (usbManager != null) {
            if (usbDeviceHID != null) {
                try {

                    usbHID = UsbSerialDevice.createUsbSerialDevice(usbDeviceHID, usbManager.openDevice(usbDeviceHID));
                    if (usbHID != null && usbHID.open()) {
                        usbHID.setDataBits(UsbSerialDevice.DATA_BITS_8);
                        usbHID.setStopBits(UsbSerialDevice.STOP_BITS_1);
                        usbHID.setParity(UsbSerialDevice.PARITY_NONE);
                        usbHID.setFlowControl(UsbSerialDevice.FLOW_CONTROL_OFF);
                        usbHID.setBaudRate(DEFAULT_BAUD_RATE_HID);

                        Log.i("USB_READ_HID", "Connection established");

                        usbHID.read(data -> readBytesDataHID(new String(data)));
                    }


                } catch (Exception e) {
                    Log.i("USB_CHECK", "Permissions are not granted to USB");
                    e.printStackTrace();
                }
            }
        }

    }

    private void readBytesDataVentilator(String receivedData) {
        Log.i("USB_READ", "Data received from ventilator : " + receivedData);

        // appending to the data buffer
        if (receivedData.trim().length() > 0) {
            dataBufferVentilator.append(receivedData);
            informWatchDog();
        }

    }


    private void readBytesDataHID(String receivedData) {
        // Log.i("USB_READ_ENCODER", "Data received from encoder : " + receivedData);

        // appending to the data buffer
        if (receivedData.trim().length() > 0) {
            dataBufferHID.append(receivedData);

        }

    }

}
