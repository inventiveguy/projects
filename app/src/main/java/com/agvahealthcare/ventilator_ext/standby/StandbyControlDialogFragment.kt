package com.agvahealthcare.ventilator_ext.standby

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

import androidx.fragment.app.commit
import com.agvahealthcare.ventilator_ext.MainActivity
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.callback.OnStartVentilationListener
import com.agvahealthcare.ventilator_ext.control.advanced.AdvancedFragment

import com.agvahealthcare.ventilator_ext.control.backup.BackupFragment
import com.agvahealthcare.ventilator_ext.control.basic.ControlParameterClickListener
import com.agvahealthcare.ventilator_ext.dashboard.DashBoardActivity
import com.agvahealthcare.ventilator_ext.model.ControlParameterModel
import com.agvahealthcare.ventilator_ext.utility.hideSystemUI
import com.agvahealthcare.ventilator_ext.utility.replaceFragment

import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import com.github.angads25.toggle.interfaces.OnToggledListener
import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_contol_dialog.imageViewCross
import kotlinx.android.synthetic.main.fragment_standbycontrol_dialog.*
import java.lang.Double
import kotlin.Boolean
import kotlin.Exception
import kotlin.Int
import kotlin.String
import kotlin.apply
import kotlin.let
import kotlin.run

class StandbyControlDialogFragment : DialogFragment() {


    private var basicParameterClickListener: ControlParameterClickListener?=null
    private var backupParameterClickListener: ControlParameterClickListener?=null

    private var advancedParameterClickListener : ControlParameterClickListener?=null
    private var closeListener: OnDismissDialogListener? = null
    private  var modeCode = 0
    private var onStartVentilationListener: OnStartVentilationListener? = null
    private var dialogStartVentConfirmation: AlertDialog? = null

    private var standbyBasicFragment: StandbyControlSettingFragment? = null
    private  var standbyAdvancedFragment: StandbyControlSettingFragment?=null
    private var standbyBackupFragment : StandbyBackupFragment? = null


//    private var visibilityTimeout: CountDownTimer? = null


    private var isStatus: Boolean? = null
    private var basicControlParams :MutableList<ControlParameterModel>? = null
    private var backupControlParams :MutableList<ControlParameterModel>? = null
    private var onToggledListener: OnToggledListener?=null
    private var advancedControlParams : MutableList<ControlParameterModel>? = null

    companion object {
        const val TAG = "StandbyControlDialog"
        private const val KEY_HEIGHT = "KEY_HEIGHT"
        private const val KEY_WIDTH = "KEY_WIDTH"
        private const val KEY_STATUS = "KEY_STATUS"


        fun newInstance(
            height: Int?,
            width: Int?,
            status: Boolean?,
            basicParams: MutableList<ControlParameterModel>,
            advancedParams: MutableList<ControlParameterModel>?,
            backupParams: MutableList<ControlParameterModel>?,
            closeListener: OnDismissDialogListener?,
            basicParameterClickListener: ControlParameterClickListener?,
            advancedParameterClickListener: ControlParameterClickListener?,
            backupParameterClickListener: ControlParameterClickListener?,
            onStartVentilationListener: OnStartVentilationListener? = null
        ): StandbyControlDialogFragment {
            val args = Bundle()
            height?.let { args.putInt(KEY_HEIGHT, it) }
            width?.let { args.putInt(KEY_WIDTH, it) }
            status?.let { args.putBoolean(KEY_STATUS, it) }

            val fragment = StandbyControlDialogFragment()
            fragment.arguments = args
            fragment.closeListener = closeListener
            fragment.basicParameterClickListener = basicParameterClickListener
            fragment.advancedParameterClickListener = advancedParameterClickListener
            fragment.backupParameterClickListener = backupParameterClickListener
            fragment.onStartVentilationListener = onStartVentilationListener
            fragment.basicControlParams = basicParams
            fragment.advancedControlParams = advancedParams
            fragment.backupControlParams = backupParams

            return fragment

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_standbycontrol_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
        setUpStandbyBasic()
        setOnClickListener()
        includeButtonStandbyBackup.visibility = if (backupControlParams?.isNotEmpty() == true) View.VISIBLE else View.GONE
        modebutton.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
        checkMode()

    }


    override fun onStart() {
        super.onStart()
        val heightDialog = arguments?.getInt(StandbyControlDialogFragment.KEY_HEIGHT)
        val widthDialog = arguments?.getInt(StandbyControlDialogFragment.KEY_WIDTH)
        isStatus = arguments?.getBoolean(StandbyControlDialogFragment.KEY_STATUS)
        setHeightWidth(heightDialog, widthDialog, isStatus)

    }

    private fun checkMode(){
        if (tag=="FromDashBoardActivity"){
            modeCode=(activity as DashBoardActivity).modeCode
        } else {
            modeCode=(activity as MainActivity).requestedModeCode
        }

        when (modeCode) {
            Configs.MODE_VCV_CMV -> {
                modebutton.text = getString(R.string.hint_vc_cmv)
            }
            Configs.MODE_VCV_ACV -> {
                modebutton.text = getString(R.string.hint_vc_cv)
            }
            Configs.MODE_VCV_SIMV -> {
                modebutton.text = getString(R.string.hint_vc_simv)
            }
            Configs.MODE_PC_CMV -> {
                modebutton.text = getString(R.string.hint_pc_cmv)
            }
            Configs.MODE_PC_SIMV -> {
                modebutton.text = getString(R.string.hint_pc_simv)
            }
            Configs.MODE_PC_AC -> {
                modebutton.text = getString(R.string.hint_spont)
            }
            Configs.MODE_PC_PSV -> {
                modebutton.text = getString(R.string.hint_psv)
            }
            Configs.MODE_PC_APRV -> {
                modebutton.text = getString(R.string.hint_pc_aprv)
            }
            Configs.MODE_AUTO_VENTILATION -> {
                modebutton.text = getString(R.string.hint_ai_vent)
            }
            Configs.MODE_NIV_BPAP -> {
                modebutton.text = getString(R.string.hint_bpap)
            }

            Configs.MODE_NIV_CPAP -> {
                modebutton.text = getString(R.string.hint_cpap)
            }
        }

    }
    private fun setUpStandbyBasic() {

        basicControlParams?.let {
            Log.i("CONTROLPARAMCHECK", "SIZE = ${it.size}")
            if (standbyBasicFragment == null) standbyBasicFragment = StandbyBasicFragment(ArrayList(it), basicParameterClickListener)

            standbyBasicFragment?.apply {
                replaceFragment(
                    this,
                    this::class.java.javaClass.simpleName,
                    R.id.standbycontrol_nav_container
                )
            }

        }

        includeButtonStandbyAdvanced.buttonView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        includeButtonStandbyAdvanced.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonStandbyBackup.buttonView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )


        includeButtonStandbyAdvanced.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)

        includeButtonStandbyBackup.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)

        includeButtonStandbyBasic.buttonView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        includeButtonStandbyBasic.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)


    }
    private fun setUpAdvanced() {

        if(advancedControlParams?.isNotEmpty() == true){
            advancedControlParams?.let {
                if(standbyAdvancedFragment == null) standbyAdvancedFragment = AdvancedFragment(ArrayList(it), advancedParameterClickListener)
                standbyAdvancedFragment?.apply {
                    replaceFragment(
                        this,
                        this::class.java.javaClass.simpleName,
                        R.id.standbycontrol_nav_container
                    )
                }
            }
        }
        includeButtonStandbyBasic.buttonView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )

        includeButtonStandbyBasic.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonStandbyBackup.buttonView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )

        includeButtonStandbyBackup.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonStandbyAdvanced.buttonView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        includeButtonStandbyAdvanced.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)

//        standbyAdvancedFragment?.apply {
//            replaceFragment(this,this::class.java.javaClass.simpleName,
//                R.id.standbycontrol_nav_container)
//        }
    }


    private fun setUpBackup() {

        if(backupControlParams?.isNotEmpty() == true){            // patientFragment = null
            backupControlParams?.let {
                if (standbyBackupFragment == null) standbyBackupFragment =
                    StandbyBackupFragment(ArrayList(it), backupParameterClickListener, onToggledListener )
                standbyBackupFragment?.apply {
                    replaceFragment(
                        this,
                        this::class.java.javaClass.simpleName,
                        R.id.standbycontrol_nav_container
                    )
                }
            }



            /*btn_update_settings.visibility = View.GONE*/
            includeButtonStandbyBackup.buttonView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            includeButtonStandbyBackup.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)

            includeButtonStandbyBasic.buttonView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            includeButtonStandbyBasic.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)

            includeButtonStandbyAdvanced.buttonView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            includeButtonStandbyAdvanced.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        }

    }

    private fun setOnClickListener() {

        includeButtonStandbyBasic.buttonView.text = getString(R.string.hint_basic)
        includeButtonStandbyBackup.buttonView.text = getString(R.string.hint_backupsettings)

        includeButtonStandbyAdvanced.buttonView.text = getString(R.string.hint_advancedsettings)

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

        includeButtonStandbyBasic.buttonView.setOnClickListener {
            setUpStandbyBasic()
        }

        includeButtonStandbyBackup.buttonView.setOnClickListener {
            setUpBackup()
        }

        includeButtonStandbyAdvanced.buttonView.setOnClickListener {
            setUpAdvanced()
        }
        buttonStartVent.setOnClickListener {
            // send mode code to ventilator
            onStartVentilationListener?.onStart()
        }
    }


    fun getApneaStatus() = standbyBackupFragment?.getApneaToggleStatus()

    fun getAllControlParameters() : ArrayList<ControlParameterModel>{

        val basicSettings = standbyBasicFragment?.getControlParameters()
        val advancedSettings = standbyAdvancedFragment?.getControlParameters()
        val backupSettings = standbyBackupFragment?.getControlParameters()

        val allSettings = arrayListOf<ControlParameterModel>()

        basicSettings?.let { allSettings.addAll(it) }
        advancedSettings?.let { allSettings.addAll(it) }
        backupSettings?.let { allSettings.addAll(it) }

        return allSettings
    }

    fun updateParameterValue(lbl: String, value: String, type: Configs.ControlSettingType) : ControlParameterModel?{
        val settingsFragment = when(type){
            Configs.ControlSettingType.BASIC-> standbyBasicFragment
            Configs.ControlSettingType.BACKUP -> standbyBackupFragment
            Configs.ControlSettingType.ADVANCED -> standbyAdvancedFragment
        }
        return updateParameterValue(settingsFragment, lbl, value)
    }

    private fun updateParameterValue(controlSettingFragment: StandbyControlSettingFragment?, lbl: String, value: String):  ControlParameterModel?{
        var paramModel: ControlParameterModel? = null
        controlSettingFragment?.apply {
            val parameters = this.getControlParameters()
            paramModel = parameters?.filter { it.ventKey == lbl }?.getOrNull(0)

            paramModel?.let { model ->
                // not to update if the value is same
                if (value == model.reading) {
                    return model
                }


                model.reading = Configs.supportPrecision(lbl, value)

//                if (Configs.isDecimalSupported(lbl)) {
//                    try {
//                        model.reading = String.format("%.1f", Double.parseDouble(value))
//                    } catch (e: Exception) {
//                        model.reading = value
//                        e.printStackTrace()
//                    }
//                    model.reading = value
//                } else {
//                    try {
//                        model.reading = Double.parseDouble(value).toInt().toString()
//                    } catch (e: Exception) {
//                        model.reading = value
//                        e.printStackTrace()
//                    }
//                }


                parameters?.let {
                    if (it.contains(model)) {
                        it[it.indexOf(model)] = model
                        // update list view item wise
                        this.notifyAdapter()
                    } else Log.i("ADAPTER", "Unable to update")
                }
            }

        }

        return paramModel
    }



    fun updateBasicParameterValue(lbl: String, value: String): ControlParameterModel? = updateParameterValue(standbyBasicFragment, lbl, value)

    fun updateAdvancedParameterValue(lbl: String, value: String): ControlParameterModel? = updateParameterValue(standbyAdvancedFragment, lbl, value)

    fun updateBackupParameterValue(lbl: String, value: String): ControlParameterModel? = updateParameterValue(standbyBackupFragment, lbl, value)




    /*fun updateKnob(){
        run {
            dialogStartVentConfirmation?.dismiss()
        }
    }*/
    fun notifyParameterAdapter() {
        if (standbyBasicFragment?.isVisible == true) {
            standbyBasicFragment?.notifyAdapter()
        }
        if (standbyBackupFragment?.isVisible == true) {
            standbyBackupFragment?.notifyAdapter()
        }

        if (standbyAdvancedFragment?.isVisible==true)
            standbyAdvancedFragment?.notifyAdapter()
    }
    public fun getStartVentConfirmation() = dialogStartVentConfirmation
    fun StandbyControlDialogFragment.setHeightWidth(
        heightDialog: Int?,
        widthDialog: Int?,
        status: Boolean?
    ) {
        dialog?.window?.apply {
            if (status == true) {
                setGravity(Gravity.CENTER_HORIZONTAL)
                decorView.apply {
                    val params: WindowManager.LayoutParams = attributes
                    params.x = -58
                    params.y = -15
                    params.dimAmount = 0.0F
                    params.screenBrightness = 5.0F
                    params.width = widthDialog!! + 30
                    params.height = heightDialog!! + 30
                    attributes = params
                }
            } else {
                setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
                decorView.apply {
                    val params: WindowManager.LayoutParams = attributes
                    params.dimAmount = 0.0F
                    params.screenBrightness = 1.0F
                    params.width = widthDialog!! + 20
                    params.height = heightDialog!! + 20
                    attributes = params
                }
            }
        }
        hideSystemUI()
    }
}




