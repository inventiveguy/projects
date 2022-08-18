package com.agvahealthcare.ventilator_ext.system.test_calib

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.callback.OnCalibrationOxygen
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.service.CommunicationService
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_test_calib.*
import java.util.*

class TestCalibrationFragment(private var communicationService: CommunicationService?, val calibrationOxygen: OnCalibrationOxygen?) : Fragment(), View.OnClickListener {

    companion object {
        const val TAG = "TestCalibrationFragment"


//        fun newInstance(
//                calibrationOxygen: OnCalibrationOxygen?
//        ): TestCalibrationFragment {
//            val args = Bundle()
//            val fragment = TestCalibrationFragment()
//            fragment.arguments = args
//            fragment.calibrationOxygen = calibrationOxygen
//            return fragment
//        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_test_calib, container, false)

    }

    private var o2CalibrateDialog: AlertDialog? = null
    private var prefManager: PreferenceManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(requireContext())

        setUpView()
        setUpOnClickListener()
    }

    private fun setUpView() {

        includeButtonTurbine.buttonView.text = getString(R.string.hint_tightness)
        includeButtonFlowSensor.buttonView.text = getString(R.string.hint_flow_sensor)
        includeButtonO2Sensor.buttonView.text = getString(R.string.hint_o2_sensor)
        includeButtonPressureSensor.buttonView.text = getString(R.string.hint_pressure_sensor)

//        ivO2SensorStatus.visibility = View.GONE
//      //  tvO2Sensor.text = "N/A"
//        includeButtonO2Sensor.isEnabled = false
    }


    private fun setUpOnClickListener() {
        includeButtonTurbine.buttonView.setOnClickListener(this)
        includeButtonFlowSensor.buttonView.setOnClickListener(this)
        includeButtonO2Sensor.buttonView.setOnClickListener(this)
        includeButtonPressureSensor.buttonView.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when(v){
            includeButtonTurbine.buttonView->{ sendCalibrationCommandToVentilator(Configs.TAG_SENSOR_TURBINE) }

            includeButtonFlowSensor.buttonView->{ sendCalibrationCommandToVentilator(Configs.TAG_SENSOR_FLOW) }

            includeButtonO2Sensor.buttonView ->{

                sendCalibrationCommandToVentilator(Configs.TAG_SENSOR_OXYGEN)
                tvO2Sensor.text = Date().toString()
//                DialogBoxFactory.showTwoBtnDialog(activity, "Warning", "Please disconnect the patient first and then tap 'YES' in order to proceed further") {
//                    o2CalibrateDialog = DialogBoxFactory.showO2CalibrateDialog(activity , this::requestO2CalibrateCommand)
//                }
            }

            includeButtonPressureSensor.buttonView ->{ sendCalibrationCommandToVentilator(Configs.TAG_SENSOR_PRESSURE) }


        }

    }

    fun updateSensorCalibrationStatus(){
        prefManager?.apply {
            Log.i("CALIBCHECK", "Sensor data is refreshing on the view......")
            //ToDo : Oxygen sensor calibration status is yet to code.


            // Turbine sensor
            if(readTurbineCalibrationStatus()){
                tvTurbineSensor.text = readTurbineCalibrationDate()
                ivTurbineSensorStatus.setImageResource(R.drawable.ic_green_circle_tick)
            }else{
                tvTurbineSensor.text= getString(R.string.sensore_not_calibrated)
                ivTurbineSensorStatus.setImageResource(R.drawable.ic_red_cross)
            }
            if(readOxygenCalibrationStatus()){
                tvO2Sensor.text = readOxygenCalibrationDate()
                ivO2SensorStatus.setImageResource(R.drawable.ic_green_circle_tick)
            }
            else{
                tvO2Sensor.text = getString(R.string.sensore_not_calibrated)
                ivO2SensorStatus.setImageResource(R.drawable.ic_red_cross)
            }

            // Flow sensor
            if(readFlowCalibrationStatus()){
                tvFlowSensor.text= readFlowCalibrationDate()
                ivFlowSensorStatus.setImageResource(R.drawable.ic_green_circle_tick)
            }else{
                tvFlowSensor.text= getString(R.string.sensore_not_calibrated)
                ivFlowSensorStatus.setImageResource(R.drawable.ic_red_cross)
            }

            // Pressure sensor
            if(readPressureCalibrationStatus()){
                tvPressureSensor.text= readPressureCalibrationDate()
                ivPressureSensorStatus.setImageResource(R.drawable.ic_green_circle_tick)
            }else{
                tvPressureSensor.text= getString(R.string.sensore_not_calibrated)
                ivPressureSensorStatus.setImageResource(R.drawable.ic_red_cross)
            }


        }
    }

    fun sendCalibrationCommandToVentilator(sensorTag: String){
        communicationService?.send("CM+"+Configs.PREFIX_SENSOR_CALIBRATION + sensorTag)
    }


    fun updateOxygenCalibrateProgressStatus(progress: Int, msg: String, textAlignment: Int ) {
        o2CalibrateDialog?.takeIf { it.isShowing }?.apply {
            val tvProgressCount = o2CalibrateDialog?.findViewById<TextView>(R.id.tvProgress)
            val tvProgressMsg = o2CalibrateDialog?.findViewById<TextView>(R.id.tvProgressMsg)
            if (progress >= 0) {
                tvProgressCount?.visibility = View.VISIBLE
                tvProgressCount?.text = "$progress%"
            } else {
                tvProgressCount?.visibility = View.GONE
            }
            tvProgressMsg?.text = msg
            tvProgressMsg?.textAlignment = textAlignment
        }
    }

    fun hideOxygenCalibrateDialog() {
        o2CalibrateDialog?.takeIf { it.isShowing }?.apply {
            cancel()
        }
    }

    fun isOxygenCalibrateDialogShowing(): AlertDialog? {
        return  o2CalibrateDialog?.takeIf { it.isShowing }
    }
}

private fun TestCalibrationFragment.requestO2CalibrateCommand() {
    calibrationOxygen?.apply {
        onCalibration()
    }
}
