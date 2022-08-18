package com.agvahealthcare.ventilator_ext.system.sensors

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_sensor.*

class SensorsFragment : Fragment() {
    private var prefManager: PreferenceManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sensor, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(context)
        includeButtonPneumatic.buttonView.setTextColor(Color.parseColor("#3b3b3b"))

        tvInhaleFlowSensorStatus.visibility = View.GONE
        tvExhaleFlowSensorStatus.visibility = View.GONE
        tvHighPressureO2SensorStatus.visibility = View.GONE
        tvLowPressureO2SensorStatus.visibility = View.GONE
        tvCo2SensorStatus.visibility = View.GONE
        tvSpo2SensorStatus.visibility = View.GONE
        tvTempSensorStatus.visibility = View.GONE

        setUpInfo()
        setOnClickListener()
    }

    // ClickListener on Button
    private fun setOnClickListener() {

        includeButtonPneumatic.buttonView.text = getString(R.string.hint_pneumatic)
        includeButtonElectronic.buttonView.text = getString(R.string.hint_electronic)


        includeButtonPneumatic.buttonView.setOnClickListener {
            setUpInfo()
        }

        includeButtonElectronic.buttonView.setOnClickListener {
            includeButtonPneumatic.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonElectronic.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected)
            layoutPneumatic.visibility = View.GONE
            layoutElectronic.visibility = View.VISIBLE
            setPaddingOnButtons(includeButtonElectronic.buttonView)
        }
    }

    private fun setUpInfo() {
        includeButtonPneumatic.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected)
        includeButtonElectronic.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        layoutPneumatic.visibility = View.VISIBLE
        layoutElectronic.visibility = View.GONE

        prefManager?.apply {
            if(readSensorLowO2() == Configs.SENSOR_AVAILABLE){
                tvLowPressureO2SensorStatus.text= getString(R.string.hint_active)
                ivLowPressureO2Sensor.setImageResource(R.drawable.ic_green_circle_tick)
            }else{
                tvLowPressureO2SensorStatus.text= getString(R.string.hint_inactive)
                ivLowPressureO2Sensor.setImageResource(R.drawable.ic_red_cross)

            }

            if(readSensorHighO2() == Configs.SENSOR_AVAILABLE){
                tvHighPressureO2SensorStatus.text= getString(R.string.hint_active)
                ivHighPressureO2Sensor.setImageResource(R.drawable.ic_green_circle_tick)
            }else{
                tvHighPressureO2SensorStatus.text= getString(R.string.hint_inactive)
                ivHighPressureO2Sensor.setImageResource(R.drawable.ic_red_cross)

            }

            if(readSensorCO2() == Configs.SENSOR_AVAILABLE){
                tvCo2SensorStatus.text= getString(R.string.hint_active)
                ivC02Sensor.setImageResource(R.drawable.ic_green_circle_tick)
            }else{
                tvCo2SensorStatus.text= getString(R.string.hint_inactive)
                ivC02Sensor.setImageResource(R.drawable.ic_red_cross)

            }

            if(readSensorSPO2() == Configs.SENSOR_AVAILABLE){
                tvSpo2SensorStatus.text= getString(R.string.hint_active)
                ivSP02Sensor.setImageResource(R.drawable.ic_green_circle_tick)
            }else{
                tvSpo2SensorStatus.text= getString(R.string.hint_inactive)
                ivSP02Sensor.setImageResource(R.drawable.ic_red_cross)

            }

            if(readSensorTemp() == Configs.SENSOR_AVAILABLE){
                tvTempSensorStatus.text= getString(R.string.hint_active)
                ivTempSensor.setImageResource(R.drawable.ic_green_circle_tick)
            }else{
                tvTempSensorStatus.text= getString(R.string.hint_inactive)
                ivTempSensor.setImageResource(R.drawable.ic_red_cross)

            }

            if(readSensorInhaleFlow() == Configs.SENSOR_AVAILABLE){
              //  tvInhaleFlowSensorStatus.text= getString(R.string.hint_active)
                ivInhaleFlowSensor.setImageResource(R.drawable.ic_green_circle_tick)
            }else{
                tvInhaleFlowSensorStatus.text= getString(R.string.hint_inactive)
                ivInhaleFlowSensor.setImageResource(R.drawable.ic_red_cross)

            }

            if(readSensorExhaleFlow() == Configs.SENSOR_AVAILABLE){
                tvExhaleFlowSensorStatus.text= getString(R.string.hint_active)
                ivExhaleFlowSensor.setImageResource(R.drawable.ic_green_circle_tick)
            }else{
                tvExhaleFlowSensorStatus.text= getString(R.string.hint_inactive)
                ivExhaleFlowSensor.setImageResource(R.drawable.ic_red_cross)

            }




        }


        setPaddingOnButtons(includeButtonPneumatic.buttonView)
    }


    private fun setPaddingOnButtons(buttonView: AppCompatButton) {
        includeButtonPneumatic.buttonView.setPadding(25, 10, 25, 10)
        includeButtonElectronic.buttonView.setPadding(25, 10, 25, 10)


        when (buttonView) {
            includeButtonPneumatic.buttonView -> {
                includeButtonPneumatic.buttonView.setTextColor(Color.parseColor("#3b3b3b"))
                includeButtonElectronic.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            }
            includeButtonElectronic.buttonView -> {
                includeButtonElectronic.buttonView.setTextColor(Color.parseColor("#3b3b3b"))
                includeButtonPneumatic.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            }
        }



      //  includeButtonPneumatic.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
       // includeButtonElectronic.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))






    }
}