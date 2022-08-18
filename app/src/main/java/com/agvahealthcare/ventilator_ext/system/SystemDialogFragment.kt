
package com.agvahealthcare.ventilator_ext.system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.agvahealthcare.ventilator_ext.callback.OnCalibrationOxygen
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.callback.OnLoudnessAdjustmentListener
import com.agvahealthcare.ventilator_ext.service.CommunicationService
import com.agvahealthcare.ventilator_ext.system.info.InfoFragment
import com.agvahealthcare.ventilator_ext.system.sensors.SensorsFragment
import com.agvahealthcare.ventilator_ext.system.settings.SettingFragment
import com.agvahealthcare.ventilator_ext.system.test_calib.TestCalibrationFragment
import com.agvahealthcare.ventilator_ext.utility.replaceFragment
import com.agvahealthcare.ventilator_ext.utility.setHeightWidthPercent
import com.agvahealthcare.ventilator_ext.R
import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_system_dialog.*

class SystemDialogFragment : DialogFragment() {


    companion object {
        var TAG = "SystemDialog"
        private const val KEY_HEIGHT = "KEY_HEIGHT"
        private const val KEY_WIDTH = "KEY_WIDTH"
        private const val KEY_STATUS = "KEY_STATUS"


        fun newInstance(
            height: Int?,
            width: Int?,
            status: Boolean?,
            closeListener: OnDismissDialogListener?,
            calibrationOxygen: OnCalibrationOxygen?,
            onLoudnessAdjustmentListener: OnLoudnessAdjustmentListener,
            communicationService: CommunicationService?
        ): SystemDialogFragment {
            val args = Bundle()
            height?.let { args.putInt(KEY_HEIGHT, it) }
            width?.let { args.putInt(KEY_WIDTH, it) }
            status?.let { args.putBoolean(KEY_STATUS, it) }
            val fragment = SystemDialogFragment()
            fragment.arguments = args
            fragment.closeListener = closeListener
            fragment.communicationService = communicationService
            fragment.calibrationOxygen = calibrationOxygen
            fragment.onLoudnessAdjustmentListener = onLoudnessAdjustmentListener
            return fragment
        }

//        fun newInstance1(
//            height: Int?,
//            width: Int?,
//            status: Boolean?,
//            closeListener: OnDismissDialog?,
//            calibrationOxygen: OnCalibrationOxygen?,
//            checkLoudness: OnCheckLoudness,
//            communicationService: CommunicationService?
//        ): SystemDialogFragment {
//            val args = Bundle()
//            height?.let { args.putInt(KEY_HEIGHT, it) }
//            width?.let { args.putInt(KEY_WIDTH, it) }
//            status?.let { args.putBoolean(KEY_STATUS, it) }
//            val fragment = SystemDialogFragment()
//            fragment.arguments = args
//            fragment.closeListener = closeListener
//            fragment.communicationService = communicationService
//            fragment.calibrationOxygen = calibrationOxygen
//            fragment.checkLoudness = checkLoudness
//            return fragment
//        }

    }

    private var closeListener: OnDismissDialogListener? = null
    private var communicationService: CommunicationService? = null

    private var calibrationOxygen: OnCalibrationOxygen? = null
    var onLoudnessAdjustmentListener: OnLoudnessAdjustmentListener? = null

    private var infoFragment: InfoFragment? = null
    private var testCalibrationFragment: TestCalibrationFragment? = null
    private var settingFragment: SettingFragment? = null
    private var sensorsFragment: SensorsFragment? = null

    fun getTestCalibFragment(): TestCalibrationFragment? = testCalibrationFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_system_dialog, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TAG=tag.toString()
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
        setUpNavigation()
    }


    private fun setUpNavigation() {

        showInfoFragment(communicationService)
        setupClickListener()
    }

    // ClickListener on Buttons
    private fun setupClickListener() {

        includeButtonInfo.buttonView.text = getString(R.string.hint_info)
        includeButtonTestCalib.buttonView.text = getString(R.string.hint_test_calib)
        includeButtonSensors.buttonView.text = getString(R.string.hint_sensors)
        includeButtonSettings.buttonView.text = getString(R.string.hint_settings)

        imageViewCross.setOnClickListener {

            requireActivity().supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commitNow()
//            requireActivity().supportFragmentManager.popBackStack()

            closeListener?.handleDialogClose()


         /*   closeListener?.handleDialogClose()
            dismiss()*/
        }

        includeButtonInfo.buttonView.setOnClickListener {
            showInfoFragment(communicationService)


        }

        includeButtonTestCalib.buttonView.setOnClickListener {

           // infoFragment = null
            //sensorsFragment = null
            //settingFragment = null
            if(testCalibrationFragment==null)
            testCalibrationFragment = TestCalibrationFragment(communicationService, calibrationOxygen)
            testCalibrationFragment?.apply {
                replaceFragment(this,this::class.java.javaClass.simpleName, R.id.system_nav_container )
            }



            includeButtonInfo.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonTestCalib.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
            includeButtonSensors.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonSettings.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)


            setPaddingOnButtons(includeButtonTestCalib.buttonView)

        }

        includeButtonSensors.buttonView.setOnClickListener {

          //  infoFragment = null
           // testCalibrationFragment = null
           // settingFragment = null
             if(sensorsFragment==null)
            sensorsFragment = SensorsFragment()
            sensorsFragment?.apply {
                replaceFragment(this,this::class.java.javaClass.simpleName, R.id.system_nav_container )
            }


            includeButtonInfo.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonTestCalib.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonSensors.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
            includeButtonSettings.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)


            setPaddingOnButtons(includeButtonSensors.buttonView)

        }

        includeButtonSettings.buttonView.setOnClickListener {

          //  infoFragment = null
           // testCalibrationFragment = null
          //  sensorsFragment = null


            if(settingFragment==null)
            settingFragment = SettingFragment.newInstance(onLoudnessAdjustmentListener)
            settingFragment?.apply {
                replaceFragment(this,this::class.java.javaClass.simpleName, R.id.system_nav_container )
            }


            includeButtonInfo.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonTestCalib.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonSensors.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonSettings.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
            setPaddingOnButtons(includeButtonSettings.buttonView)

        }

    }

    //By Default Fragment
    private fun showInfoFragment(communicationService: CommunicationService?) {


       // testCalibrationFragment = null
       // settingFragment = null
        //sensorsFragment = null
        if(infoFragment==null)
        infoFragment = InfoFragment(communicationService)
        infoFragment?.apply {
            replaceFragment(this, TAG, R.id.system_nav_container )
        }

        includeButtonInfo.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
        includeButtonTestCalib.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonSensors.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonSettings.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)

        setPaddingOnButtons(includeButtonInfo.buttonView)
    }

    private fun setPaddingOnButtons(buttonView: AppCompatButton) {

       // includeButtonInfo.buttonView.setPadding(50, 10, 50, 10)
        //includeButtonTestCalib.buttonView.setPadding(15, 10, 15, 10)
       // includeButtonSensors.buttonView.setPadding(35, 10, 35, 10)
       // includeButtonSettings.buttonView.setPadding(35, 10, 35, 10)

        when (buttonView) {
            includeButtonInfo.buttonView -> {
               // includeButtonInfo.buttonView.setTextColor(Color.parseColor("#3b3b3b"))
                includeButtonInfo.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))


                includeButtonTestCalib.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                includeButtonSensors.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                includeButtonSettings.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            includeButtonTestCalib.buttonView -> {
                //includeButtonTestCalib.buttonView.setTextColor(Color.parseColor("#3b3b3b"))
                includeButtonTestCalib.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))



                includeButtonInfo.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                includeButtonSensors.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                includeButtonSettings.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            }
            includeButtonSensors.buttonView -> {
               // includeButtonSensors.buttonView.setTextColor(Color.parseColor("#3b3b3b"))
                includeButtonSensors.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))


                includeButtonInfo.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                includeButtonTestCalib.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                includeButtonSettings.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            }
            includeButtonSettings.buttonView -> {
                //includeButtonSettings.buttonView.setTextColor(Color.parseColor("#3b3b3b"))
                includeButtonSettings.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))



                includeButtonInfo.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                includeButtonTestCalib.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                includeButtonSensors.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            }
        }
/*        includeButtonInfo.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonTestCalib.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonSensors.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonSettings.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))*/
    }

    override fun onStart() {
        super.onStart()
        val heightDialog = arguments?.getInt(KEY_HEIGHT)
        val widthDialog = arguments?.getInt(KEY_WIDTH)
        val isCheck = arguments?.getBoolean(KEY_STATUS)

        setHeightWidthPercent(heightDialog, widthDialog, isCheck)

    }

    fun updateOxygenCalibrateProgressStatus(progress: Int, msg: String, textAlignment: Int) {

        testCalibrationFragment?.takeIf { it.isVisible }?.apply {
            updateOxygenCalibrateProgressStatus(
                progress,
                msg,
                textAlignment
            )
        }

    }

    fun hideOxygenCalibrateDialog() {
        testCalibrationFragment?.takeIf { it.isVisible }?.apply {
            hideOxygenCalibrateDialog()
        }
    }

    fun isOxygenCalibrateDialogShowing(): TestCalibrationFragment? {
        return testCalibrationFragment?.takeIf { it.isVisible }
            ?.apply { isOxygenCalibrateDialogShowing() }
    }

    fun setBatteryLevelUpdate(batteryLevel: Int) {
        infoFragment?.takeIf { it.isVisible }?.apply {
          //  setBatteryLevelUpdate(batteryLevel)
        }
    }

   /* fun setBatteryHealthUpdate(batteryLevel: Int) {
        infoFragment?.takeIf { it.isVisible }?.apply {
            setBatteryHealthUpdate(batteryLevel)
        }
    }*/

    fun setBatteryTTEUpdate(remainingTime: Int) {
        infoFragment?.takeIf { it.isVisible }?.apply {
            //setBatteryTTEUpdate(remainingTime)
        }
    }

    fun setSoftWareUpdate(softwareUpdate: String?) {
        infoFragment?.takeIf { it.isVisible }?.apply {
            setSoftWareUpdate(softwareUpdate)
        }

    }

    fun updateKnob(data: String?) {
        settingFragment?.takeIf { it.isVisible }?.apply {
            updateKnobSetting(data.toString())
        }
    }



}