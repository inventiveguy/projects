package com.agvahealthcare.ventilator_ext.alarm.limit_two

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
import com.agvahealthcare.ventilator_ext.alarm.limit_one.EncoderValue
import com.agvahealthcare.ventilator_ext.alarm.limit_one.KnobParameterModel
import com.agvahealthcare.ventilator_ext.alarm.limit_one.ViewHolder
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
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import com.agvahealthcare.ventilator_ext.utility.utils.Configs.*
import com.github.angads25.toggle.interfaces.OnToggledListener
import com.github.angads25.toggle.model.ToggleableView
import kotlinx.android.synthetic.main.fragment_limit_two.*
import kotlinx.android.synthetic.main.knob_progress_view.view.*

class LimitTwoFragment(private val communicationService: CommunicationService?, private  val limitChangeListener: OnAlarmLimitChangeListener) : Fragment(), OnKnobPressListener, OnToggledListener,
    OnDismissDialogListener, View.OnClickListener,OnLimitChangeListener {

    companion object{
        val TAG = "LimitTwoFragment"
    }

    private var fio2UpperLimit: Float? = null
    private var fio2LowerLimit: Float? = null

    //setting the default value from the Strings for fio2
    //lazy initialization variable will not be initialized unless you call it and the variable is initialized
    // when you call or use the variable once and
    //and that value is used throughout
    private var default_fio2UpperLimit: Float? =null
    private var default_fio2LowerLimit: Float? =null

    private var spo2UpperLimit: Float? =null
    private var spo2LowerLimit: Float? =null
    //setting the default value from the strings for spo2
    private var default_spo2UpperLimit: Float? = null
    private var default_spo2LowerLimit: Float? = null

    private var knobDialog: KnobDialog? = null
    private var labelViewHolderMap: MutableMap<String, ViewHolder> = LinkedHashMap()
    private var parameterModel: KnobParameterModel? = null
    private var encoderOption: ControlParameterLimit? = null
    private var encoderValue: EncoderValue? = null
    private var currentView: View? = null
    private var currentKey:String?=null

    private var prefManager: PreferenceManager? = null
    private lateinit var mEventViewModel: EventViewModel
    private var limitTwoObserver:LimitTwoObserver?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        limitTwoObserver=LimitTwoObserver()
        limitTwoObserver.apply {
            this?.let { this@LimitTwoFragment.lifecycle.addObserver(it) }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        default_fio2UpperLimit= requireActivity().getString(R.string.default_max_fio2_limit).toFloat()
        default_fio2LowerLimit=requireActivity().getString(R.string.default_min_fio2_limit).toFloat()
        default_spo2UpperLimit=requireActivity().getString(R.string.default_max_spo2_limit).toFloat()
        default_spo2LowerLimit=requireActivity().getString(R.string.default_min_spo2_limit).toFloat()
        return inflater.inflate(R.layout.fragment_limit_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        /*prefManager = PreferenceManager(requireContext())
        mEventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        hideSystemUI()
        //fio2Toggale.setOnToggledListener(this)

        setupClickListener()
        createViewBindingMap()*/
    }
    override fun onPause() {
        super.onPause()
    }
    override fun onResume() {
        super.onResume()

    }
    private fun createViewBindingMap() {
        createViewHolderMapping()
        initUserSetLimits()
        initToggalState()
    }
    private fun initToggalState() {
        prefManager?.apply {
            fio2Toggale.isOn = readFio2LimitState()
            spO2Toggle.isOn = readSpO2LimitState()
        }
    }

    fun updateKnob(value: String) {
        knobDialog?.takeIf { it.isVisible }?.apply {
            updateWithTimeoutDebounce(value)
        }
    }


    private fun createViewHolderMapping() {

        labelViewHolderMap.clear()




        labelViewHolderMap.put(
            LBL_FIO2,
            ViewHolder(
                default_fio2LowerLimit,
                default_fio2UpperLimit,
                fio2LowerLimit,
                fio2UpperLimit
            )
        )
        labelViewHolderMap.put(
            LBL_SPO2,
            ViewHolder(
                default_spo2LowerLimit,
                default_spo2UpperLimit,
                spo2LowerLimit,
                spo2UpperLimit
            )
        )

    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        knobDialog?.takeIf { it.isVisible }?.apply {
            hideSystemUI()
        }
        Log.i("testingHideBar", isInMultiWindowMode.toString())
    }

    private fun setupClickListener() {

      /*  includeButtonDefaults.buttonView.text = getString(R.string.hint_defaults)
        includeButtonDefaults.buttonView.setPadding(20, 10, 20, 10)

        includeButtonDefaults.buttonView.setOnClickListener {
            prefManager?.clearLimitTwoPreferences()
            prefManager?.clearLimitTwoStatePreferences()
            initUserSetLimits()
            createViewBindingMap()
        }*/
        //ToDo:- includefio2UpperLimit
        includefio2Upperlimit.setOnClickListener(this)
        includefio2lowerlimit.setOnClickListener(this)

        //ToDo:- includefio2UpperLimit
        includeSpO2Upperlimit.setOnClickListener(this)
        includeSpO2lowerlimit.setOnClickListener(this)
    }

    private fun initUserSetLimits() {
        Log.i("USER_LIMIT", "Init user limit called")
        prefManager?.apply {
            renderUserLimits(LBL_FIO2, readFiO2Limits())
            renderUserLimits(LBL_SPO2, readSpO2Limits())
        }

    }


    private fun renderUserLimits(lbl: String, limit: Array<Float?>) {
        val isLimitValid = limit.size == 2 && limit[0] != null && limit[1] != null
        if (isLimitValid) {
            val minUserLimit = limit[0]!!
            val maxUserLimit = limit[1]!!
            //from the limit one fragment code
            //current inselection variable to store default upper limit value
            var defaultUpperLimit:Float?=null
            //current inselection variable to store default lower limit value
            var defaultLowerLimit:Float?=null
            //current inselection variable to store upperlimit view
            var upperLimitView:View?=null
            //current inselection variable to store lowerlimit view
            var lowerLimitView:View?=null

            when (lbl) {
                LBL_FIO2 -> {
                    upperLimitView=includefio2Upperlimit
                    lowerLimitView=includefio2lowerlimit
                    defaultUpperLimit=default_fio2UpperLimit
                    defaultLowerLimit=default_fio2LowerLimit
                }
                LBL_SPO2 -> {
                    upperLimitView=includeSpO2Upperlimit
                    lowerLimitView=includeSpO2lowerlimit
                    defaultUpperLimit=default_spo2UpperLimit
                    defaultLowerLimit=default_spo2LowerLimit
                }
            }
            //ToDo:- the points of integration
            //set the lower limit of the currently selected item in the view
            lowerLimitView?.apply {
                if (defaultLowerLimit!=null && defaultUpperLimit!=null) setValueOnProgressView(
                    this,
                    defaultLowerLimit,
                    defaultUpperLimit
                )
                setValueOnLimitView(this,minUserLimit)
            }
            //set the upper limit of the currently selected item in the view
            upperLimitView?.apply {
                if (defaultLowerLimit!=null && defaultUpperLimit!=null) setValueOnProgressView(
                    this,
                    defaultLowerLimit,
                    defaultUpperLimit
                )
                setValueOnLimitView(this,maxUserLimit)
            }
            createViewHolderMapping()
        }
    }
    private fun setValueOnProgressView(activeView: View,lowerLimit:Float,upperLimit:Float){
        activeView.progress_bar.max=upperLimit.toInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activeView.progress_bar.min=lowerLimit.toInt()

        }
    }
    //method to set the progress bar value of the individual view in selection
    private fun setValueOnLimitView(activeView:View,newValue: Float){
        //update the view
        activeView.progress_bar.setProgress(newValue.toInt())
        Log.i("LIMIT2_CHECK", "value = ${nonDecimal(activeView, newValue.toString())}")
        activeView.textView.text = nonDecimal(activeView,newValue.toString())

        //update runtime variable value
        when(activeView){
            includefio2Upperlimit->{
                fio2UpperLimit=newValue
            }
            includefio2lowerlimit->{
                fio2LowerLimit=newValue
            }
            includeSpO2lowerlimit->{
                spo2LowerLimit=newValue
            }
            includeSpO2Upperlimit->{
                spo2UpperLimit=newValue
            }
        }
    }

    private fun nonDecimal(v: View,value: String): String{
        val decimalSupportedValues = listOf<View>(
            includefio2Upperlimit,
            includefio2lowerlimit,
            includeSpO2Upperlimit,
            includeSpO2lowerlimit
        )
        return if(v in decimalSupportedValues){
            Log.i("LIMIT2_CHECK", "Decimal supported view")
            value.toFloatOrNull()?.toInt()?.toString() ?: value
        }
        else {
            Log.i("LIMIT2_CHECK", "Decimal NOT supported view")
            value.toFloatOrNull()?.toInt()?.toString() ?: value
        }
    }

    override fun onLimitChange(previousValue: Float, newValue: Float) {
        currentView?.apply { setValueOnLimitView(this,newValue) }
    }



    override fun onKnobPress(previousValue: Float, newValue: Float) {
        currentView?.let {
            this deSelect it

            prefManager?.apply {
                when (it) {
                    includefio2lowerlimit -> {
                        setFiO2Limits(fio2LowerLimit, fio2UpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            fio2LowerLimit!!,
                            fio2UpperLimit
                        )
                    }
                    includefio2Upperlimit -> {
                        setFiO2Limits(fio2LowerLimit, fio2UpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            fio2LowerLimit!!,
                            fio2UpperLimit
                        )
                    }
                    includeSpO2lowerlimit -> {
                        setSpO2Limits(spo2LowerLimit, spo2UpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            spo2LowerLimit!!,
                            spo2UpperLimit
                        )
                    }
                    includeSpO2Upperlimit -> {
                        setSpO2Limits(spo2LowerLimit, spo2UpperLimit)
                        limitChangeListener.onChangeAlarmLimit(
                            currentKey,
                            spo2LowerLimit!!,
                            spo2UpperLimit
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
    override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
        when (toggleableView?.id) {
            fio2Toggale.id -> prefManager?.setFio2LimitState(isOn)
            spO2Toggle.id -> prefManager?.setSpO2LimitState(isOn)
        }
    }

    override fun handleDialogClose() {
        knobDialog?.takeIf { it.isVisible }?.dismiss()
        this deSelect currentView
        initUserSetLimits()
    }

    override fun onClick(selectedView: View?) {
        currentView = selectedView
        select(selectedView)
        //setValueOnProgressView &  setValueOnLimitView
        var isUpperLimit: Boolean? = null
        var activeLabel: String? = null


        when(selectedView){
            includeSpO2lowerlimit -> {
                isUpperLimit = false
                activeLabel=LBL_SPO2
            }

            includeSpO2Upperlimit -> {
                isUpperLimit = true
                activeLabel=LBL_SPO2
            }

            includefio2lowerlimit -> {
                isUpperLimit = false
                activeLabel=LBL_FIO2
            }

            includefio2Upperlimit -> {
                isUpperLimit = true
                activeLabel=LBL_FIO2
            }

        }

        val value = labelViewHolderMap[activeLabel]

        currentKey = activeLabel
        Log.i(
            "limit_one_array",
            "" + value?.defaultMax + " " + value?.defaultMin + " " + value?.actualMax + " " + value?.actualMin
        )


      if(activeLabel != null && isUpperLimit != null){
          if(isUpperLimit){
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

                  encoderOption = ControlParameterLimit(
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
                              activeLabel, 1, it.actualMin, Configs.getParameterUnit(
                                  requireContext(),
                                  activeLabel
                              )
                          )


                  Log.i("limit_one_array", "" + parameterModel?.name)

                  encoderOption = ControlParameterLimit(
                      it.defaultMin,
                      it.defaultMax
                  )

                  encoderValue = encoderOption?.let { it.valuePerRotation }?.let { it1 ->
                      EncoderValue(
                          it.defaultMin, it.actualMax,
                          it1.toFloat()

                      )
                  }

              }
          }
          knobDialog = parameterModel?.let { it1 ->
              encoderValue?.let { it2 ->
                  KnobDialog.newInstance(
                      onKnobPressListener = this,
                      onTimeoutListener = this,
                      onCloseListener= object :OnDismissDialogListener{
                          override fun handleDialogClose() {
                              this@LimitTwoFragment deSelect currentView
                              initUserSetLimits()
                          }
                      },
                      parameterModel = it1,
                      encoderValue = it2,
                      cancelableStatus =true,
                      onLimitChangeListener = this
                  )
              }
          }



        knobDialog?.let {
            it.show(childFragmentManager, LimitTwoFragment.TAG)
            it.startTimeoutWithDebounce()
        }
        }
    }

    private fun select(limitView: View?){
        //limitView?.progress_bar?.progressDrawable=ContextCompat.getDrawable(requireContext(),R.drawable.progresscircle_with_selection)
      //  limitView?.textView?.setTextColor(Color.WHITE)
        limitView?.progress_bar?.run {
            val progressValue = progress
            val progressMin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) min else 0
            val progressMax = max
            setValueOnProgressView(limitView, progressMin.toFloat(), progressMax.toFloat())
            setValueOnLimitView(limitView, progressValue.toFloat())
        }

        limitView?.textView?.setTextColor(Color.WHITE)
    }

    private infix fun LimitTwoFragment.deSelect(limitView:View?){
        val progressValue = limitView?.progress_bar?.progress
        limitView?.textView?.setTextColor(Color.BLACK)
    }

    override fun onStop() {
        super.onStop()
        limitTwoObserver.apply {
            this?.let { this@LimitTwoFragment.lifecycle.removeObserver(it) }
        }
        limitTwoObserver=null
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
    override fun onDestroy() {
        super.onDestroy()
    }
    inner class LimitTwoObserver: DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)

        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            prefManager = PreferenceManager(requireContext())
            mEventViewModel = ViewModelProvider(this@LimitTwoFragment).get(EventViewModel::class.java)
            hideSystemUI()
            //fio2Toggale.setOnToggledListener(this)

            setupClickListener()
            createViewBindingMap()
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            includeSpO2lowerlimit.progress_bar.progress=0

            includeSpO2Upperlimit.progress_bar.progress=0

            includefio2lowerlimit.progress_bar.progress=0

            includefio2Upperlimit.progress_bar.progress=0

            default_fio2UpperLimit=null

            default_fio2LowerLimit=null

            default_spo2UpperLimit=null

            default_spo2LowerLimit=null

            currentView=null
            fio2UpperLimit = null
            fio2LowerLimit = null
            spo2UpperLimit =null
            spo2LowerLimit =null
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)

        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
        }
    }

}
