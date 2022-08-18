package com.agvahealthcare.ventilator_ext.alarm.limit_one

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.callback.OnAlarmLimitChangeListener
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.callback.OnKnobPressListener
import com.agvahealthcare.ventilator_ext.callback.OnLimitChangeListener
import com.agvahealthcare.ventilator_ext.logs.event.EventViewModel
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.model.ControlParameterLimit
import com.agvahealthcare.ventilator_ext.service.CommunicationService
import com.agvahealthcare.ventilator_ext.utility.KnobDialog
import com.agvahealthcare.ventilator_ext.utility.hideSystemUI
import com.agvahealthcare.ventilator_ext.utility.utils.Configs.*
import kotlinx.android.synthetic.main.fragment_limit_one.*
import kotlinx.android.synthetic.main.knob_progress_view.view.*
import kotlin.collections.LinkedHashMap


class LimitOneFragment(private val communicationService: CommunicationService?, private  val limitChangeListener: OnAlarmLimitChangeListener) : Fragment(), OnKnobPressListener,
    OnDismissDialogListener,OnLimitChangeListener,View.OnClickListener/*, OnToggledListener*/ {
    companion object {
        val TAG = "LimitOneFragment"
    }
    private var prefManager: PreferenceManager? = null
    private var presserUpperLimit: Float? = null
    private var presserLowerLimit: Float? = null
    private var vtiUpperLimit: Float? = null
    private var vtiLowerLimit: Float? = null
    private var vteUpperLimit: Float? = null
    private var vteLowerLimit: Float? = null
    private var peepUpperLimit: Float? = null
    private var peepLowerLimit: Float? = null
    private var respiratoryUpperLimit: Float? = null
    private var respiratoryLowerLimit: Float? = null
    private var mviUpperLimit: Float? = null
    private var mviLowerLimit: Float? = null


    private var default_presserUpperLimit:Float? = null
    private var default_presserLowerLimit:Float? = null
    private var default_vtiUpperLimit:Float? =  null
    private var default_vtiLowerLimit:Float? =  null
    private var default_vteUpperLimit:Float? =  null
    private var default_vteLowerLimit:Float? = null
    private var default_peepUpperLimit:Float? =  null
    private var default_peepLowerLimit:Float? =  null
    private var default_respiratoryUpperLimit:Float? = null
    private var default_respiratoryLowerLimit:Float? = null
    private var default_mviUpperLimit:Float? = null
    private var default_mviLowerLimit:Float? =  null

    private var customProgressDialog: KnobDialog? = null

    private lateinit var mEventViewModel: EventViewModel

    private var labelViewHolderMap: MutableMap<String, ViewHolder> = LinkedHashMap()
    private var parameterModel: KnobParameterModel? = null
    private var encoderOption: ControlParameterLimit? = null
    private var encoderValue: EncoderValue? = null

    private var currentView: View? = null
    private var currentKey: String? = null
    private var limitOneObserver:LimitOneObserver?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        limitOneObserver= LimitOneObserver()
        limitOneObserver.apply {
            this?.let { this@LimitOneFragment.lifecycle.addObserver(it) }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.i("ACTIVITY_LIFECYCLE", "ON_CREATE $javaClass")
        default_presserUpperLimit = requireActivity().getString(R.string.default_max_pip_limit).toFloat()
        default_presserLowerLimit =  requireActivity().getString(R.string.default_min_pip_limit).toFloat()
        default_vtiUpperLimit =  requireActivity().getString(R.string.default_max_vti_limit).toFloat()
        default_vtiLowerLimit =  requireActivity().getString(R.string.default_min_vti_limit).toFloat()
        default_vteUpperLimit =  requireActivity().getString(R.string.default_max_vte_limit).toFloat()
        default_vteLowerLimit =  requireActivity().getString(R.string.default_min_vte_limit).toFloat()
        default_peepUpperLimit =  requireActivity().getString(R.string.default_max_peep_limit).toFloat()
        default_peepLowerLimit =  requireActivity().getString(R.string.default_min_peep_limit).toFloat()
        default_respiratoryUpperLimit= requireActivity().getString(R.string.default_max_rr_limit).toFloat()
        default_respiratoryLowerLimit =  requireActivity().getString(R.string.default_min_rr_limit).toFloat()
        default_mviUpperLimit =  requireActivity().getString(R.string.default_max_mvi_limit).toFloat()
        default_mviLowerLimit =  requireActivity().getString(R.string.default_min_mvi_limit).toFloat()
        return inflater.inflate(R.layout.fragment_limit_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("ACTIVITY_LIFECYCLE", "ON_VIEW_CREATE $javaClass")



        /*  presserToggle.setOnToggledListener(this)
        vteToggle.setOnToggledListener(this)
        peepToggle.setOnToggledListener(this)
        rrToggle.setOnToggledListener(this)
        mviToggle.setOnToggledListener(this)
*/

        // initTogalState()
    }
    override fun onStart() {
        super.onStart()
        /*prefManager = PreferenceManager(requireContext())
        mEventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        hideSystemUI()
        setupClickListener()
        createViewBindingMap()*/
        Log.i("ACTIVITY_LIFECYCLE", "ON_START $javaClass")
    }

    override fun onResume() {
        super.onResume()
        Log.i("ACTIVITY_LIFECYCLE", "ON_RESUME $javaClass")
    }

    private fun initTogalState() {
        prefManager?.apply {
            presserToggle.isOn = readPipLimitState()
            vteToggle.isOn = readVteLimitState()
            peepToggle.isOn = readPeepLimitState()
            rrToggle.isOn = readRRLimitState()
            mviToggle.isOn = readMviLimitState()
        }

    }


    private fun createViewBindingMap() {
        createViewHolderMapping()
        initUserSetLimits()
        initTogalState()
    }

    private fun createViewHolderMapping() {

        labelViewHolderMap.clear()



        labelViewHolderMap[LBL_PIP] = ViewHolder(
            default_presserLowerLimit,
            default_presserUpperLimit,
            presserLowerLimit,
            presserUpperLimit
        )
        labelViewHolderMap[LBL_VTE] = ViewHolder(
            default_vteLowerLimit,
            default_vteUpperLimit,
            vteLowerLimit,
            vteUpperLimit
        )

        labelViewHolderMap[LBL_PEEP] = ViewHolder(
            default_peepLowerLimit,
            default_peepUpperLimit,
            peepLowerLimit,
            peepUpperLimit
        )
        labelViewHolderMap[LBL_RR] = ViewHolder(
            default_respiratoryLowerLimit,
            default_respiratoryUpperLimit,
            respiratoryLowerLimit,
            respiratoryUpperLimit
        )
        labelViewHolderMap[LBL_MVI] =
            ViewHolder(default_mviLowerLimit, default_mviUpperLimit, mviLowerLimit, mviUpperLimit)


    }


    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        customProgressDialog?.takeIf { it.isVisible }?.apply {
            hideSystemUI()
        }
        Log.i("testingHideBar", isInMultiWindowMode.toString())
    }

    // set on ClickListener
    private fun setupClickListener() {
        includePressureUpperLimit.setOnClickListener(this)
        includePressureLoweLimit.setOnClickListener(this)
        //ToDo:-includeVTeUpperLimit
        includeVTeUpperLimit.setOnClickListener(this)
        includeVTeLowerLimit.setOnClickListener(this)
        //ToDo:-includePeepUpperLimit
        includePeepUpperLimit.setOnClickListener(this)
        includePeepLowerLimit.setOnClickListener(this)
        //ToDo:-includeRRUpperLimit
        includeRRUpperLimit.setOnClickListener(this)
        includeRRLowerLimit.setOnClickListener(this)
        //ToDo:-includeMVIUpperLimit
        includeMVIUpperLimit.setOnClickListener(this)
        includeMVILowerLimit.setOnClickListener(this)
    }

    private fun initUserSetLimits() {
        Log.i("USER_LIMIT", "Init user limit called")
        prefManager?.apply {
            renderUserLimits(LBL_PIP, readPipLimits())
            renderUserLimits(LBL_VTE, readVteLimits())
            renderUserLimits(LBL_PEEP, readPeepLimits())
            renderUserLimits(LBL_RR, readRRLimits())
            renderUserLimits(LBL_MVI, readMviLimits())
        }
    }


    private fun renderUserLimits(lbl: String, limit: Array<Float?>) {
        val isLimitValid = limit.size == 2 && limit[0] != null && limit[1] != null
        if (isLimitValid) {
            val minUserLimit = limit[0]!!
            val maxUserLimit = limit[1]!!

            var defaultUpperLimit: Float? = null
            var defaultLowerLimit: Float? = null
            var upperLimitView: View? = null
            var lowerLimitView: View? = null
            when (lbl) {
                LBL_PIP -> {
                    upperLimitView = includePressureUpperLimit
                    lowerLimitView = includePressureLoweLimit
                    defaultUpperLimit = default_presserUpperLimit
                    defaultLowerLimit = default_presserLowerLimit
                }
                LBL_VTE -> {
                    upperLimitView = includeVTeUpperLimit
                    lowerLimitView = includeVTeLowerLimit
                    defaultUpperLimit = default_vteUpperLimit
                    defaultLowerLimit = default_vteLowerLimit
                }
                LBL_PEEP -> {
                    upperLimitView = includePeepUpperLimit
                    lowerLimitView = includePeepLowerLimit
                    defaultUpperLimit = default_peepUpperLimit
                    defaultLowerLimit = default_peepLowerLimit
                }
                LBL_RR -> {
                    upperLimitView = includeRRUpperLimit
                    lowerLimitView = includeRRLowerLimit
                    defaultUpperLimit = default_respiratoryUpperLimit
                    defaultLowerLimit = default_respiratoryLowerLimit
                }
                LBL_MVI -> {
                    upperLimitView = includeMVIUpperLimit
                    lowerLimitView = includeMVILowerLimit
                    defaultUpperLimit = default_mviUpperLimit
                    defaultLowerLimit = default_mviLowerLimit
                }
            }
            lowerLimitView?.apply {
                if (defaultLowerLimit != null && defaultUpperLimit != null) setValueOnProgressView(
                    this,
                    defaultLowerLimit,
                    defaultUpperLimit
                )
                setValueOnLimitView(this, minUserLimit)
            }

            upperLimitView?.apply {
                if (defaultLowerLimit != null && defaultUpperLimit != null) setValueOnProgressView(
                    this,
                    defaultLowerLimit,
                    defaultUpperLimit
                )
                setValueOnLimitView(this, maxUserLimit)
            }
        }
        createViewHolderMapping()

    }





    override fun onKnobPress(previousValue: Float, newValue: Float) {
        currentView?.let {
            this deSelect it

            prefManager?.apply {
                when (it) {


                    includePressureUpperLimit -> {
                        setPipLimits(presserLowerLimit, presserUpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            presserLowerLimit!!,
                            presserUpperLimit
                        )
                    }
                    includePressureLoweLimit -> {
                        setPipLimits(presserLowerLimit, presserUpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            presserLowerLimit!!,
                            presserUpperLimit
                        )
                    }

                    includeVTeUpperLimit -> {
                        setVteLimits(vteLowerLimit, vteUpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            vteLowerLimit!!,
                            vteUpperLimit
                        )
                    }
                    includeVTeLowerLimit -> {
                        setVteLimits(vteLowerLimit, vteUpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            vteLowerLimit!!,
                            vteUpperLimit
                        )
                    }
                    includePeepUpperLimit -> {
                        setPEEPLimits(peepLowerLimit, peepUpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            peepLowerLimit!!,
                            peepUpperLimit
                        )
                    }
                    includePeepLowerLimit -> {
                        setPEEPLimits(peepLowerLimit, peepUpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            peepLowerLimit!!,
                            peepUpperLimit
                        )
                    }
                    includeRRUpperLimit -> {
                        setRRLimits(respiratoryLowerLimit, respiratoryUpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            respiratoryLowerLimit!!,
                            respiratoryUpperLimit
                        )
                    }
                    includeRRLowerLimit -> {
                        setRRLimits(respiratoryLowerLimit, respiratoryUpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            respiratoryLowerLimit!!,
                            respiratoryUpperLimit
                        )
                    }
                    includeMVIUpperLimit -> {
                        setMviLimits(mviLowerLimit, mviUpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            mviLowerLimit!!,
                            mviUpperLimit
                        )
                    }

                    includeMVILowerLimit -> {
                        setMviLimits(mviLowerLimit, mviUpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            mviLowerLimit!!,
                            mviUpperLimit
                        )
                    }
                }

            }

            currentView = null
            initUserSetLimits()
            createViewHolderMapping()
            communicationService?.sendAlarmLimitsToVentilator()
        }
    }


    fun updateKnob(value: String) {
        customProgressDialog?.takeIf { it.isVisible }?.apply {
            updateWithTimeoutDebounce(value)
        }

    }

    override fun handleDialogClose() {
        customProgressDialog?.takeIf { it.isVisible }?.dismiss()
        this deSelect currentView
        initUserSetLimits()
    }

    override fun onLimitChange(previousValue: Float, newValue: Float) {
        currentView?.apply { setValueOnLimitView(this, newValue) }
    }

    private fun setValueOnProgressView(activeView: View, lowerLimit: Float, upperLimit: Float) {
        activeView.progress_bar.max = upperLimit.toInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) activeView.progress_bar.min =
            lowerLimit.toInt()
    }

    private fun setValueOnLimitView(activeView: View, newValue: Float) {
        // update the view
        //activeView.progress_bar.progress = newValue.toInt()
        activeView.progress_bar.setProgress(newValue.toInt())
        Log.i("LIMIT1_CHECK", "value = ${supportPrecision(activeView, newValue.toString())}")
        activeView.textView.text = supportPrecision(activeView, newValue.toString())

        // update runtime variable value
        when (activeView) {
            includePressureUpperLimit -> {
                presserUpperLimit = newValue
            }
            includePressureLoweLimit -> {
                presserLowerLimit = newValue
            }
            includeVTeUpperLimit -> {
                vteUpperLimit = newValue
            }
            includeVTeLowerLimit -> {
                vteLowerLimit = newValue
            }
            includePeepUpperLimit -> {
                peepUpperLimit = newValue
            }
            includePeepLowerLimit -> {
                peepLowerLimit = newValue
            }
            includeRRUpperLimit -> {
                respiratoryUpperLimit = newValue
            }
            includeRRLowerLimit -> {
                respiratoryLowerLimit = newValue
            }
            includeMVIUpperLimit -> {
                mviUpperLimit = newValue
            }
            includeMVILowerLimit -> {
                mviLowerLimit = newValue
            }
        }
        /* mviToggle.id -> {
            setMviLimitState(isOn)
        }*/
    }

    private fun supportPrecision(v: View, value: String): String{
        val decimalSupportedViews = listOf<View>(
            includeMVILowerLimit,
            includeMVIUpperLimit
        )

         return if(v in decimalSupportedViews) {
             Log.i("LIMIT1_CHECK", "Decimal supported view")
             value
         }
        else {
             Log.i("LIMIT1_CHECK", "Decimal NOT supported view")
             value.toFloatOrNull()?.toInt()?.toString() ?: value
         }

    }

    private fun select(limitView: View?) {
        limitView?.progress_bar?.run {
            val progressValue = progress
            val progressMin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) min else 0
            val progressMax = max
            setValueOnProgressView(limitView, progressMin.toFloat(), progressMax.toFloat())
            setValueOnLimitView(limitView, progressValue.toFloat())
        }

        limitView?.textView?.setTextColor(Color.WHITE)
    }

    private infix fun LimitOneFragment.deSelect(limitView: View?) {
        limitView?.progress_bar?.progress
        limitView?.textView?.setTextColor(Color.BLACK)
    }

    override fun onClick(selectedView: View) {
        currentView = selectedView
        select(selectedView)
        // ToDO : Write labels for all parameters 5 x 2
        var activeLabel: String? = null
        var isUpperLimit: Boolean? = null
        when (selectedView) {
            includePressureLoweLimit -> {
                activeLabel = LBL_PIP
                isUpperLimit = false
            }
            includePressureUpperLimit -> {
                activeLabel = LBL_PIP
                isUpperLimit = true
            }
            includeVTeUpperLimit -> {
                activeLabel = LBL_VTE
                isUpperLimit = true
            }
            includeVTeLowerLimit -> {
                activeLabel = LBL_VTE
                isUpperLimit = false
            }
            includePeepUpperLimit -> {
                activeLabel = LBL_PEEP
                isUpperLimit = true
            }
            includePeepLowerLimit -> {
                activeLabel = LBL_PEEP
                isUpperLimit = false
            }
            includeRRUpperLimit -> {
                activeLabel = LBL_RR
                isUpperLimit = true
            }
            includeRRLowerLimit -> {
                activeLabel = LBL_RR
                isUpperLimit = false
            }
            includeMVIUpperLimit -> {
                activeLabel = LBL_MVI
                isUpperLimit = true
            }
            includeMVILowerLimit -> {
                activeLabel = LBL_MVI
                isUpperLimit = false
            }
        }
        val value = labelViewHolderMap[activeLabel]
        currentKey = activeLabel
        Log.i(
            "limit_one_array",
            "" + value?.defaultMax + " " + value?.defaultMin + " " + value?.actualMax + " " + value?.actualMin
        )
        if (activeLabel != null && isUpperLimit != null) {

            if (isUpperLimit) {
                value?.let {
                    parameterModel =
                        KnobParameterModel(
                            activeLabel,
                            activeLabel, 1, it.actualMax, getParameterUnit(
                                requireContext(),
                                activeLabel
                            )
                        )
                    Log.i("limit_one_array", "" + parameterModel?.name)
                    encoderOption =
                        ControlParameterLimit(
                            it.defaultMin,
                            it.defaultMax
                        )
                    encoderValue = encoderOption?.let { it.valuePerRotation }?.let { it1 ->
                        EncoderValue(
                            it.actualMin, it.defaultMax,
                            it1.toFloat()
                        )
                    }

                }
            }
            else {
                value?.let {
                    parameterModel =
                        KnobParameterModel(
                            activeLabel,
                            activeLabel, 1, it.actualMin, getParameterUnit(
                                requireContext(),
                                activeLabel
                            )
                        )

                    Log.i("limit_one_array", "" + parameterModel?.name)
                    encoderOption =
                        ControlParameterLimit(
                            it.defaultMin,
                            it.defaultMax
                        )
                    //                encoderValue = EncoderValue(it.minTileView,it.tvMax, encoderOption?.valuePerRotation?.toFloat() )
                    encoderValue = encoderOption?.valuePerRotation?.let { it1 ->
                        EncoderValue(
                            it.defaultMin, it.actualMax,
                            it1
                        )
                    }
                }
            }

            customProgressDialog = parameterModel?.let { it1 ->
                encoderValue?.let { it2 ->
                    KnobDialog.newInstance(
                        onKnobPressListener = this,
                        onTimeoutListener = this,
                        onCloseListener=object : OnDismissDialogListener{
                            override fun handleDialogClose() {
                                this@LimitOneFragment deSelect currentView
                                initUserSetLimits()
                            }
                        },
                        parameterModel = it1,
                        encoderValue = it2,
                        cancelableStatus = true,
                        onLimitChangeListener = this
                    )
                }
            }
            customProgressDialog?.let {
                it.show(childFragmentManager, LimitOneFragment.TAG)
                it.startTimeoutWithDebounce()
            }
//        hideSystemUI()
//        currentView?.textView?.text=presserUpperLimit!!.toString()
        }
    }

    /*override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {

                prefManager?.apply {
                    when (toggleableView?.id) {

                        presserToggle.id -> {
                            setPipLimitState(isOn)
                        }

                        vteToggle.id -> {
                            setVteLimitState(isOn)
                        }

                        peepToggle.id -> {
                            setPeepLimitState(isOn)
                        }

                        rrToggle.id -> {
                            setRRLimitState(isOn)
                        }

                        mviToggle.id -> {
                            setMviLimitState(isOn)
                        }
                    }
                }

            }*/





    override fun onPause() {
        super.onPause()

        Log.i("ACTIVITY_LIFECYCLE", "ON_PAUSE $javaClass")
    }

    override fun onStop() {
        super.onStop()
        limitOneObserver.apply {
            this?.let { this@LimitOneFragment.lifecycle.removeObserver(it) }
        }
        limitOneObserver=null
        Log.i("ACTIVITY_LIFECYCLE", "ON_STOP $javaClass")
    }
    //Views get destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("ACTIVITY_LIFECYCLE", "ON_DESTROY $javaClass")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("ACTIVITY_LIFECYCLE", "ON_STOP $javaClass")
    }
    inner class LimitOneObserver: DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            prefManager = PreferenceManager(requireContext())
            mEventViewModel = ViewModelProvider(this@LimitOneFragment).get(EventViewModel::class.java)
            hideSystemUI()
            setupClickListener()
            createViewBindingMap()
            Log.d("THELIFECYCLEOWNERSTART","START method invoked")
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            Log.d("THELIFECYCLEOWNERRESUME","RESUME method invoked")
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            includePressureLoweLimit.progress_bar.progress=0
            includePressureUpperLimit.progress_bar.progress=0
            includeVTeUpperLimit.progress_bar.progress=0
            includeVTeLowerLimit.progress_bar.progress=0
            includePeepUpperLimit.progress_bar.progress=0
            includePeepLowerLimit.progress_bar.progress=0
            includeRRUpperLimit.progress_bar.progress=0
            includeRRLowerLimit.progress_bar.progress=0
            includeMVIUpperLimit.progress_bar.progress=0
            includeMVILowerLimit.progress_bar.progress=0

            default_presserUpperLimit =null
            default_presserLowerLimit =  null
            default_vtiUpperLimit =  null
            default_vtiLowerLimit =  null
            default_vteUpperLimit = null
            default_vteLowerLimit =  null
            default_peepUpperLimit =  null
            default_peepLowerLimit =  null
            default_respiratoryUpperLimit= null
            default_respiratoryLowerLimit =  null
            default_mviUpperLimit =  null
            default_mviLowerLimit =  null

            currentView=null


            presserUpperLimit = null
            presserLowerLimit = null
            vtiUpperLimit = null
            vtiLowerLimit = null
            vteUpperLimit = null
            vteLowerLimit = null
            peepUpperLimit = null
            peepLowerLimit = null
            respiratoryUpperLimit = null
            respiratoryLowerLimit = null
            mviUpperLimit = null
            mviLowerLimit = null
            Log.d("THELIFECYCLEOWNERPAUSE","PAUSE method invoked")
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            Log.d("THELIFECYCLEOWNERSTOP","STOP method invoked")
        }
    }
}




