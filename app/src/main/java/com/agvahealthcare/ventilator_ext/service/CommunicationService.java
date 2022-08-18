package com.agvahealthcare.ventilator_ext.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.agvahealthcare.ventilator_ext.callback.ConfigurationMiddleware;
import com.agvahealthcare.ventilator_ext.connection.support_threads.WatchDogTask;
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager;
import com.agvahealthcare.ventilator_ext.utility.utils.AppUtils;
import com.agvahealthcare.ventilator_ext.utility.utils.Configs;
import com.agvahealthcare.ventilator_ext.utility.utils.ConfigurationArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public abstract class CommunicationService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private final WatchDogTask watchDog;

    public CommunicationService() {
        watchDog = new WatchDogTask(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /*
     * Generates a log for the class with which service is bound
     */
    public void makeLog(String className) {
        Log.i(this.getClass().getSimpleName(), "bounded with " + className);
    }

    /*
     * Initiate Watch dog timer to keep a check of incoming data
     */
    public void startWatchDog() {
        if (watchDog != null) watchDog.startSurveillance();
    }

    /*
     * Halt the watching and kills the watch dog thread
     * After this invocation, data monitoring will no more work
     */
    public void stopWatchDog() {
        if (watchDog != null) watchDog.stop();
    }

    /*
     * Inform the watch dog thread regarding the incoming data
     * and updates the last updation count
     */
    public void informWatchDog() {
        if (watchDog != null) watchDog.inform();
    }

    /*
     * Verifies if the ventilator is connected to the device
     */
    abstract public boolean isVentilatorConnected();

    /*
     * Verifies if the encoder knob is connected to the device
     */
    abstract public boolean isHIDConnected();

    /*
     * Verifies if the encoder knob & ventilator both are connected to the device
     */
    public final boolean isPortsConnected() {
        //        ToDo: || for testing, it should be &&
       // return isVentilatorConnected() || isHIDConnected();
        return isVentilatorConnected() && isHIDConnected();
//        return isVentilatorConnected();
    }

    /*
     * Broadcast Ventilator raw data signal throughout app receivers
     */
    abstract protected void broadcastRawData(String data);

    /*
     * Broadcast Ventilator functional data signal throughout app receivers
     */
    abstract protected void broadcastData(String data);

    /*
     * Broadcast Ventilator acknowledgement throughout app receivers
     */
    abstract protected void broadcastAcknowledgement(String ack);

    /*
     * Broadcast Ventilator battery status throughout app receivers
     */
    abstract protected void broadcastBatteryStatus(String brtyLevel, String btryHealth, String remainingTime);

    /*
     * Broadcast Ventilator battery status throughout app receivers
     */
    abstract protected void broadcastHeatSensorStatus(ArrayList<String> values);


    /*
     * Broadcast Ventilator aensor availability status throughout app receivers
     */
    abstract protected void broadcastSensorAnalysis(String sensorAnalysis);



    /*
     * Broadcast Ventilator sensor calibration analysis status throughout app receivers
     */
    abstract protected void broadcastCalibrationSensorAnalysis(String sensorTag, String calibrationStatus);


    /*
     * Broadcast Ventilator Motor life (in hrs) status throughout app receivers
     */
    abstract protected void broadcastMotorLifeLevelStatus(String motorLife);

    /*
     * Broadcast Standby response (in code 00/01) status throughout app receivers
     */
    abstract protected void broadcastStandbyResponse(String standbyResponse);

    /*
     * Broadcast Wifi connection response (in code 00/01) status throughout app receivers
     */
    abstract protected void broadcastWifiConnectionResponse(String wifiConnectionResponse);

    /*
     * Broadcast Self Test response (in code 00/007) status throughout app receivers
     */
    abstract protected void broadcastSelfTestResponse(String stpResponse);

    /*
     * Broadcast Available Wifi devices throughout app receivers
     */
    abstract protected void broadcastScannedWifiDevices(String devicesJson);

    /*
     * Broadcast Available ventilator software version throughout app receivers
     */
    abstract protected void broadcastSoftwareVersion(String softwareUpdateData);

    /*
     * Broadcast Device name request by ventilator throughout app receivers
     */
    abstract protected void broadcastDeviceNameRequested(String deviceNameReqCode);

    /*
     * Broadcast Ventilator connected signal throughout app receivers
     */
    abstract public void sendBroadcastHandshakeCompleted();

    /*
     * This enables the device to start listening from Ventilator.
     */
    abstract public void startReading();

    /*
     * This disable the device to stop listening from Ventilator.
     */
    abstract public void stopReading();

    /*
     * Send data to the ventilator
     */
    abstract public void send(String data);


    /*
     *  send KNob response
     */
    abstract protected void broadcastKnobResponse(String knobResponse);


    abstract protected void broadcastAlarmMuteUmuteResponse();

    abstract protected void broadcastNebuliserResponse();

    abstract protected void broadcastOxygenResponse();

    abstract protected void broadcastInspiratoryHoldResponse();

    abstract protected void broadcastExpiratoryHoldResponse();

    abstract protected void broadcastManualBreathResponse();

    abstract protected void broadcastHomeResponse();

    abstract protected void broadcastLockResponse();

    abstract protected void broadcastPowerSwitchResponse();


    /*
     * Send date time to ventilator
     */
    public void sendCurrentDateTime() {
        send("DT@" + AppUtils.ventDateTimeFormatter.format(new Date()) + "#");
    }

    public void sendConfigurationToVentilator() {

        sendConfigurationToVentilator(null);
    }


    public void sendAlarmLimitsToVentilator() {
        final ConfigurationArrayList settings = getAlarmSettingsList();
        String prefix = "L,";
        String data = prefix + settings.toString() + ",#";
        send(data);
        Log.i("ALARMSETTINGCHECK", "Sent -> " + data);

    }

    public void sendConfigurationToVentilator(ConfigurationMiddleware middleware) {
//        int moduleIndex = 1;

        final ConfigurationArrayList settings = (middleware != null) ? middleware.modify(getControlSettingsList()) : getControlSettingsList();
        String prefix = "S,";
        String data = prefix + settings.toString() + ",#";
        send(data);
        Log.i("CONFIGCHECK", "Sent -> " + data);

        /*final ConfigurationArrayList settings = (middleware != null) ? middleware.modify(getSettingsList()) : getSettingsList();
        for (ConfigurationArrayList configs : ConfigurationArrayList.bisectConfigurationIntoList(settings)) {

            // SLEEP TIME
            try {
                Thread.sleep(Configs.CONFIGURATION_MODULE_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
                FileLogger.Companion.e(getApplicationContext(), e);
            }

            // check for configuration array size
            if (configs.size() <= ConfigurationArrayList.MAX_MODULE_SIZE) {
                String prefix = "S" + (moduleIndex++) + ",";
                String data = prefix + configs.toString();

                send(data);

                Log.i("CONFIG SENT CHECK", data);
            }
        }*/
    }

    private ConfigurationArrayList getAlarmSettingsList() {
        final PreferenceManager prefManager = new PreferenceManager(getApplicationContext());

        List<Float> pip = Arrays.asList(prefManager.readPipLimits());
        List<Float> vte = Arrays.asList(prefManager.readVteLimits());
        List<Float> peep = Arrays.asList(prefManager.readPeepLimits());
        List<Float> rr = Arrays.asList(prefManager.readRRLimits());
        List<Float> mvi = Arrays.asList(prefManager.readMviLimits());
        List<Float> fio2 = Arrays.asList(prefManager.readFiO2Limits());
        List<Float> spo2 = Arrays.asList(prefManager.readSpO2Limits());

        List<String> flattenList = Stream.of(
                pip,
                vte,
                peep,
                rr,
                mvi,
                fio2,
                spo2
        ).flatMap(List::stream).map(e -> String.valueOf(e.intValue())).collect(Collectors.toList());
        Log.i("ALARMSETTINGCHECK", "FLAT : " + flattenList);

        ConfigurationArrayList configs = new ConfigurationArrayList();
        configs.addAll(flattenList);
        Log.i("ALARMSETTINGCHECK", "CONFIG ARRAY : " + flattenList);

        return configs;
    }

    private ConfigurationArrayList getControlSettingsList() {

        final PreferenceManager prefManager = new PreferenceManager(getApplicationContext());
        String pip = String.valueOf(prefManager.readPip().intValue());
        String vti = String.valueOf(prefManager.readVti().intValue());
        String peep = String.valueOf(prefManager.readPEEP().intValue());
        String trigFlow = String.valueOf(prefManager.readTrigFlow().intValue());

        // support pressure is delta only in SPONt and PSV modes
        final boolean isPplatDeltaRequired = Configs.getModeCategory(prefManager.readVentilationMode()) != Configs.MODE_NIV;
//        float pplatReading = isPplatDeltaRequired ? (prefManager.readPplat().intValue()) : prefManager.readPplat().intValue();
        int pplatReading = isPplatDeltaRequired ? ( prefManager.readPplat().intValue() + prefManager.readPEEP().intValue() ) : prefManager.readPplat().intValue();
        String pplat = String.valueOf(pplatReading);

        String inhaleTime = String.format("%.1f", prefManager.readTinsp());
        String peakFlow = String.valueOf(prefManager.readPeakFlow().intValue());
        String fio2 = String.valueOf(prefManager.readFiO2().intValue());
        String supportPressure = String.valueOf(prefManager.readSupportPressure().intValue() +  prefManager.readPEEP().intValue() ); // SP = SP + PEEP
        String slope = String.valueOf(prefManager.readSlope().intValue());

        // modify RR to reduces the error
        // mapping is done by best fit polynomial equation
//        final int rawRR = prefManager.readRR().intValue();
        //    String rr = String.valueOf((rawRR >= 30) ? Configs.getCompensateInputRR(prefManager.readRR().intValue()) : rawRR);
        String rr = String.valueOf(prefManager.readRR().intValue());

        String tlow = String.format("%.1f", prefManager.readTlow());

        String texp = String.valueOf(prefManager.readTexp().intValue());

        // backup ventilation
        String statusApnea = String.valueOf(prefManager.readApneaSettingsStatus() ? 1 : 0);
        String rrApnea = String.valueOf(prefManager.readRRApnea().intValue());
        String tApnea = String.valueOf(prefManager.readTApnea().intValue());
        String vtApnea = String.valueOf(prefManager.readVtApnea().intValue());
        String trigFlowApnea = String.valueOf(prefManager.readTrigFlowApnea());

        // IMPORTANT : Order should be preserved
        ConfigurationArrayList configs = new ConfigurationArrayList();
        configs.add(pip);
        configs.add(vti);
        configs.add(peep);
        configs.add(rr);
        configs.add(trigFlow);
        configs.add(pplat);
        configs.add(inhaleTime);
        configs.add(peakFlow);
        if (Configs.isFio2SettingAvailable) configs.add(fio2);
        configs.add(supportPressure);
        configs.add(slope);
        configs.add(tlow);
        configs.add(texp);


        configs.add(statusApnea);
        configs.add(rrApnea);
        configs.add(tApnea);
        configs.add(vtApnea);
        configs.add(trigFlowApnea);

        return configs;
    }

    public class LocalBinder extends Binder {
        public CommunicationService getService() {
            return CommunicationService.this;
        }
    }


}
