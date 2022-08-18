
package com.agvahealthcare.ventilator_ext.modes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.logs.event.EventViewModel
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.model.ControlParameterModel
import com.agvahealthcare.ventilator_ext.utility.ToastFactory
import com.agvahealthcare.ventilator_ext.utility.VENTILATOR_MODES
import com.agvahealthcare.ventilator_ext.utility.setHeightWidthPercent
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import com.agvahealthcare.ventilator_ext.utility.utils.Configs.*
import com.agvahealthcare.ventilator_ext.utility.utils.IntentFactory
import com.agvahealthcare.ventilator_ext.R
import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_mode_dialog.*

interface OnModeConfirmListener {
    fun onConfirm(modeCode: Int)
    fun onCancel()
}


class ModeDialogFragment : DialogFragment(), View.OnClickListener {

    companion object {
        const val TAG = "ModeDialog"
        private const val KEY_HEIGHT = "KEY_HEIGHT"
        private const val KEY_WIDTH = "KEY_WIDTH"
        private const val KEY_STATUS = "KEY_STATUS"


        fun newInstance(
            height: Int?,
            width: Int?,
            status: Boolean?,
            onModeConfirmListener: OnModeConfirmListener?,
            closeListener: OnDismissDialogListener?
        ): ModeDialogFragment {
            val args = Bundle()
            height?.let { args.putInt(KEY_HEIGHT, it) }
            width?.let { args.putInt(KEY_WIDTH, it) }
            status?.let { args.putBoolean(KEY_STATUS, it) }
            val fragment = ModeDialogFragment()
            fragment.arguments = args
            fragment.onModeConfirmListener = onModeConfirmListener
            fragment.closeListener = closeListener
            return fragment
        }
    }

    private var closeListener: OnDismissDialogListener? = null
    private var onModeConfirmListener: OnModeConfirmListener? = null
    private lateinit var modeButtons: List<AppCompatButton>
    private var ventMode: Int? = null
    private var currentMode: String? = null
    private var preferenceManager: PreferenceManager? = null
    private var modeSettingsList: ArrayList<ControlParameterModel>? = null
    //private var dialogModeConfirmation: AlertDialog? = null
    private var isStatus: Boolean? = null
    private lateinit var mEventViewModel: EventViewModel


    init {
        modeSettingsList = ArrayList<ControlParameterModel>()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_mode_dialog, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mEventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)

        preferenceManager = PreferenceManager(requireContext())
        modeButtons = listOf(
            buttonVcCmv,
            buttonVcSimv,
            buttonAcv,
            buttonPcCmv,
            buttonPcSimv,
            buttonPsv,
            buttonPcac,
            buttonAprv,
            buttonAIVent,
            buttonBpap,
            buttonCpap
        )

        setUpData()
        setupClickListener()

       // setModeViaPreference()
    }

    private fun setModeViaPreference() {

        when (getExistingVentilatorMode()) {

            MODE_VCV_CMV -> {
                this select buttonVcCmv
                currentMode = getString(R.string.hint_vc_cmv)
            }

            MODE_VCV_SIMV -> {
                this select buttonVcSimv
                currentMode = getString(R.string.hint_vc_simv)

            }

            MODE_VCV_ACV -> {
                this select buttonAcv
                currentMode = getString(R.string.hint_vc_cv)

            }


            MODE_PC_CMV -> {
                this select buttonPcCmv
                currentMode = getString(R.string.hint_pc_cmv)

            }

            MODE_PC_SIMV -> {
                this select buttonPcSimv
                currentMode = getString(R.string.hint_pc_imv)


            }

            MODE_PC_AC -> {
                this select buttonPcac
                currentMode = getString(R.string.hint_spont)

            }

            MODE_PC_APRV -> {
                this select buttonAprv
                currentMode = getString(R.string.hint_pc_aprv)

            }
            MODE_PC_PSV -> {
                this select buttonPsv
                currentMode = getString(R.string.hint_psv)

            }

            MODE_AUTO_VENTILATION -> {
                this select buttonAIVent
                currentMode = getString(R.string.hint_ai_vent)

            }

            MODE_NIV_BPAP -> {
                this select buttonBpap
                currentMode = getString(R.string.hint_bpap)
            }

            MODE_NIV_CPAP -> {
                this select buttonCpap
                currentMode = getString(R.string.hint_cpap)


            }
        }
    }


    // setUp Data
    private fun setUpData() {


        includeButtonCancel.buttonView.text = getString(R.string.hint_cancel)
        includeButtonConfirm.buttonView.text = getString(R.string.hint_confirm)

        // set Padding
        includeButtonCancel.buttonView.setPadding(65, 0, 65, 0)
        includeButtonConfirm.buttonView.setPadding(65, 0, 65, 0)
        // set Drawable
        includeButtonCancel.buttonView.setBackgroundResource(R.drawable.background_light_grey)
        includeButtonConfirm.buttonView.setBackgroundResource(R.drawable.background_light_grey_border)
    }


    // ClickListener on Buttons
    private fun setupClickListener() {
        modeButtons.forEach { it.setOnClickListener(this) }


        includeButtonCancel.buttonView.setOnClickListener {

            requireActivity().supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commitNow()
//            requireActivity().supportFragmentManager.popBackStack()

            closeListener?.handleDialogClose()

            onModeConfirmListener?.onCancel()
           // dismiss()
        }
        imageViewCross.setOnClickListener {

            requireActivity().supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commitNow()
//            requireActivity().supportFragmentManager.popBackStack()

            closeListener?.handleDialogClose()

            /* closeListener?.handleDialogClose()
             dismiss()*/
        }

        includeButtonConfirm.buttonView.setOnClickListener {

            if (ventMode != null) {

//                showConfirmationDialog(currentMode, ventMode, isStatus, isExistingVentilationModeAvailable())
            } else {
                ToastFactory.custom(requireContext(), getString(R.string.hint_select_mode))

            }

        }

    }

    private fun getExistingVentilatorMode(): Int? = preferenceManager?.readVentilationMode()

    private fun isExistingVentilationModeAvailable(): Boolean {
        val ventMode = getExistingVentilatorMode()
        return ventMode != null && isValidVentilatorMode(requireContext(), ventMode)
    }

/*    private fun showConfirmationDialog(
        currentMode: String?,
        newMode: VentMode?,
        isStatus: Boolean?,
        isVentMode: Boolean?
    ) {
        var msg = ""
        if (isVentMode == true) {
            msg = "Do you want to switch from current mode"
            msg += if (currentMode != null) " from $currentMode" else " the mode"
            msg += " to " + newMode?.modeType
            msg += " ? \nPress the knob to confirm"
        } else {
            msg = "Do you want to start ventilator "
            msg += newMode?.modeType
            msg += " ? \nPress the knob to confirm"
        }

        // repeat prevention check
        dialogModeConfirmation?.takeIf { it.isShowing }?.apply {
            return
        }
        dialogModeConfirmation = DialogBoxFactory.selectModeConfirmationDialog(
            requireActivity(),
            msg,
            ventMode,
            this
        )

    }*/

//    private fun updatePreferences() {
//
//        preferenceManager?.apply {
//
//            getParamByName(LBL_PIP)?.let { setPip(it.getReading()?.toFloat()) }
//
//            getParamByName(LBL_VTI)?.let { setVti(it.getReading()?.toFloat()) }
//
//            getParamByName(LBL_PEAK_FLOW)?.let { setPeakFlow(it.getReading()?.toFloat()) }
//
//            getParamByName(LBL_PEEP)?.let { setPEEP(it.getReading()?.toFloat()) }
//
//            getParamByName(LBL_RR)?.let { setRR(it.getReading()?.toFloat()) }
//
//            getParamByName(LBL_TRIG_FLOW)?.let { setTrigFlow(it.getReading()?.toFloat()) }
//
//            getParamByName(LBL_PPLAT)?.let { setPplat(it.getReading()?.toFloat()) }
//
//            getParamByName(LBL_TINSP)?.let { setTinsp(it.getReading()?.toFloat()) }
//
//            getParamByName(LBL_FIO2)?.let { setFiO2(it.getReading()?.toFloat()) }
//
//            getParamByName(LBL_SUPPORT_PRESSURE)?.let {
//                setSupportPressure(
//                    it.getReading()?.toFloat()
//                )
//            }
//
//            getParamByName(LBL_SLOPE)?.let { setSlope(it.getReading()?.toFloat()) }
//
//            getParamByName(LBL_TLOW)?.let { setTlow(it.getReading()?.toFloat()) }
//
//            getParamByName(LBL_TEXP)?.let { setTexp(it.getReading()?.toFloat()) }
//
//
//        }
//
//    }

//    private fun getParamByName(name: String?): ControlParameterModel? {
//        if (name != null) {
//            modeSettingsList?.takeIf { it.isNotEmpty() }?.apply {
//                for (vm in this) {
//                    if (name == vm.getVentKey()) return vm
//                }
//            }
//        }
//        return null
//    }


    private infix fun ModeDialogFragment.select(btn: AppCompatButton) {
        btn.setBackgroundResource(R.drawable.background_green_border)
        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private infix fun ModeDialogFragment.deSelect(btn: AppCompatButton) {
        btn.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    override fun onStart() {
        super.onStart()
        val heightDialog = arguments?.getInt(KEY_HEIGHT)
        val widthDialog = arguments?.getInt(KEY_WIDTH)
        isStatus = arguments?.getBoolean(KEY_STATUS)

        setHeightWidthPercent(heightDialog, widthDialog, isStatus)
    }

    override fun onClick(v: View?) {

        when (v) {

            buttonVcCmv -> {

                this select buttonVcCmv
                this deSelect buttonVcSimv
                this deSelect buttonAcv
                this deSelect buttonPcCmv
                this deSelect buttonPcSimv
                this deSelect buttonPcac
                this deSelect buttonPsv
                this deSelect buttonAprv
                this deSelect buttonAIVent
                this deSelect buttonBpap
                this deSelect buttonCpap

                ventMode =
                    MODE_VCV_CMV



            }


            buttonVcSimv -> {
                this deSelect buttonVcCmv
                this select buttonVcSimv
                this deSelect buttonAcv
                this deSelect buttonPcCmv
                this deSelect buttonPcSimv
                this deSelect buttonPcac
                this deSelect buttonPsv
                this deSelect buttonAprv
                this deSelect buttonAIVent
                this deSelect buttonBpap
                this deSelect buttonCpap
                ventMode = MODE_VCV_SIMV



            }

            buttonAcv -> {
                this deSelect buttonVcCmv
                this deSelect buttonVcSimv
                this select buttonAcv
                this deSelect buttonPcCmv
                this deSelect buttonPcSimv
                this deSelect buttonPcac
                this deSelect buttonPsv
                this deSelect buttonAprv
                this deSelect buttonAIVent
                this deSelect buttonBpap
                this deSelect buttonCpap
                ventMode = MODE_VCV_ACV



            }

            buttonPcCmv -> {
                this deSelect buttonVcCmv
                this deSelect buttonVcSimv
                this deSelect buttonAcv
                this select buttonPcCmv
                this deSelect buttonPcSimv
                this deSelect buttonPcac
                this deSelect buttonPsv
                this deSelect buttonAprv
                this deSelect buttonAIVent
                this deSelect buttonBpap
                this deSelect buttonCpap
                ventMode = MODE_PC_CMV


            }

            buttonPcSimv -> {
                this deSelect buttonVcCmv
                this deSelect buttonVcSimv
                this deSelect buttonAcv
                this deSelect buttonPcCmv
                this select buttonPcSimv
                this deSelect buttonPcac
                this deSelect buttonPsv
                this deSelect buttonAprv
                this deSelect buttonAIVent
                this deSelect buttonBpap
                this deSelect buttonCpap


                ventMode = MODE_PC_SIMV



            }

            buttonPcac -> {

                this deSelect buttonVcCmv
                this deSelect buttonVcSimv
                this deSelect buttonAcv
                this deSelect buttonPcCmv
                this deSelect buttonPcSimv
                this select buttonPcac
                this deSelect buttonPsv
                this deSelect buttonAprv
                this deSelect buttonAIVent
                this deSelect buttonBpap
                this deSelect buttonCpap


                ventMode =
                    MODE_PC_AC


            }

            buttonPsv -> {

                this deSelect buttonVcCmv
                this deSelect buttonVcSimv
                this deSelect buttonAcv
                this deSelect buttonPcCmv
                this deSelect buttonPcSimv
                this deSelect buttonPcac
                this select buttonPsv
                this deSelect buttonAprv
                this deSelect buttonAIVent
                this deSelect buttonBpap
                this deSelect buttonCpap

                ventMode =
                    MODE_PC_PSV


            }

            buttonAprv -> {

                this deSelect buttonVcCmv
                this deSelect buttonVcSimv
                this deSelect buttonAcv
                this select buttonAprv
                this deSelect buttonPcCmv
                this deSelect buttonPcSimv
                this deSelect buttonPcac
                this deSelect buttonPsv
                this deSelect buttonAIVent
                this deSelect buttonBpap
                this deSelect buttonCpap

                ventMode =
                    MODE_PC_APRV

            }

            buttonAIVent -> {

                this deSelect buttonVcCmv
                this deSelect buttonVcSimv
                this deSelect buttonAcv
                this deSelect buttonPcCmv
                this deSelect buttonPcSimv
                this deSelect buttonPcac
                this deSelect buttonPsv
                this deSelect buttonAprv
                this select buttonAIVent
                this deSelect buttonBpap
                this deSelect buttonCpap

                ventMode =
                    MODE_AUTO_VENTILATION


            }

            buttonBpap -> {

                this select buttonBpap

                this deSelect buttonVcCmv
                this deSelect buttonVcSimv
                this deSelect buttonAcv
                this deSelect buttonPcCmv
                this deSelect buttonPcSimv
                this deSelect buttonPcac
                this deSelect buttonPsv
                this deSelect buttonAprv
                this deSelect buttonAIVent
                this select buttonBpap
                this deSelect buttonCpap



                ventMode =
                    MODE_NIV_BPAP

            }

            buttonCpap -> {

                this deSelect buttonVcCmv
                this deSelect buttonVcSimv
                this deSelect buttonAcv
                this deSelect buttonPcCmv
                this deSelect buttonPcSimv
                this deSelect buttonPcac
                this deSelect buttonPsv
                this deSelect buttonAprv
                this deSelect buttonAIVent
                this deSelect buttonBpap
                this select buttonCpap



                ventMode = MODE_NIV_CPAP


            }

        }

        ventMode?.takeIf { Configs.isValidVentilatorMode(requireContext(), it) }
            ?.apply {
                //n IMPORTANT : Order should be preserved
                sendModeBroadcast(this)
                onModeConfirmListener?.onConfirm(this)
            }



       /* if (ventMode != null) {

   *//**//*        *//* showConfirmationDialog(currentMode, ventMode, isStatus, isExistingVentilationModeAvailable())*//*
        } else {
            ToastFactory.custom(requireContext(), getString(R.string.hint_select_mode))

        }*/

    }
/*
    fun updateKnob(data: String?) {
        when (data) {
            PREFIX_AND -> modeConfirm()
        }

    }




    }*/

    private fun sendModeBroadcast(mode:Int) {
        val i = Intent(IntentFactory.ACTION_MODE_SET)
        i.putExtra(VENTILATOR_MODES, mode)
        requireContext().sendBroadcast(i)

        dismiss()
    }


 //   public fun getModeConfirmationDialog() = dialogModeConfirmation

/*
   private fun onModeConfirm(mode: VentMode){
       onModeConfirmListener?.onConfirm(mode.modeCode)

       // updating preferences is required
       updatePreferences()
       // sending mode to the ventilator

    }
*/

//    override fun onModeChange(mode: VentMode) {
//
//        onModeConfirmListener?.onConfirm(mode.modeCode)

        // updating preferences is required
//        updatePreferences()
        // sending mode to the ventilator

       /* if (isStatus == true) {
            (activity as DashBoardActivity?)?.apply {
                ventMode?.modeCode?.let { sendControlModeToVentilator(it) }
            }
            Log.i("DashBoardActivity   : ", "StatusValue$isStatus")

            val eventDataModel =
                EventDataModel("Ventilation Mode Change from $currentMode to ${ventMode?.modeType}")
            mEventViewModel.addEvent(eventDataModel)

        } else {
            (activity as MainActivity?)?.apply {
                ventMode?.modeCode?.let { sendControlModeToVentilator(it) }
            }
            Log.i("MainActivity   : ", "StatusValue$isStatus")
        }
*/
//        setMode(mode)

//    }


}