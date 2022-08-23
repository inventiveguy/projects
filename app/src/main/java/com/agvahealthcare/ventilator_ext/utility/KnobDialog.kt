
package com.agvahealthcare.ventilator_ext.utility

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.agvahealthcare.ventilator_ext.alarm.limit_one.EncoderValue
import com.agvahealthcare.ventilator_ext.alarm.limit_one.KnobParameterModel
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.callback.OnKnobPressListener
import com.agvahealthcare.ventilator_ext.callback.OnLimitChangeListener
import com.agvahealthcare.ventilator_ext.utility.utils.Configs.*
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import kotlinx.android.synthetic.main.progress_dialog_view.view.*
import java.lang.Error

class KnobDialog : DialogFragment() {

    private  var onKnobPressListener: OnKnobPressListener? = null
    private  var onTimeoutListener: OnDismissDialogListener? = null
    private  var onCloseListener: OnDismissDialogListener? = null
    private  var onLimitChangeListener: OnLimitChangeListener? = null
    private lateinit var parameterModel: KnobParameterModel
    private lateinit var encoderValue: EncoderValue
    private var isCloseListenerAvoided = false

    private var upperLimit: Int = 0
    private var lowerlimit: Int = 0
    private var currentValue: Float = 0f
    private var actualValue: Int = 0
    public var cancelableStatus: Boolean = false;

    private var prefManager: PreferenceManager? = null


    companion object {

        const val TAG = "SimpleDialog"
        private const val KEY_STATUS = "KEY_STATUS"
        private const val KEY_WIDTH = "KEY_WIDTH"



        fun newInstance(
            onKnobPressListener: OnKnobPressListener,
            parameterModel: KnobParameterModel,
            encoderValue: EncoderValue,
            cancelableStatus: Boolean = false,
            height: Int? = 0,
            width: Int? = 975,
            // position: UnitPosition? =
            onTimeoutListener: OnDismissDialogListener? = null,
            onCloseListener: OnDismissDialogListener? = null,
            onLimitChangeListener: OnLimitChangeListener? =  null
        ): KnobDialog {
            return KnobDialog().apply {
                this.onKnobPressListener = onKnobPressListener
                this.onTimeoutListener = onTimeoutListener
                this.onCloseListener = onCloseListener
                this.onLimitChangeListener = onLimitChangeListener
                this.parameterModel = parameterModel
                this.encoderValue = encoderValue
                this.cancelableStatus = cancelableStatus
            }
        }


    }


    init {
        Log.i("KNOBDIALOGCHECK", "Created knob dialog")

    }
    var visibilityTimeout:CountDownTimer? =null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        prefManager = PreferenceManager(requireContext());
        return inflater.inflate(R.layout.progress_dialog_view, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }



//    override fun show(manager: FragmentManager, tag: String?) {
//        super.show(manager, tag)
//        startTimeoutWithDebounce()
//    }

    override fun onStart() {
        super.onStart()

        setHeightWidth()
    }

    override fun onDestroy() {
        Log.i("KNOBDIALOGCHECK", "Destroyed knob dialog")
        cancelTimeout()

        if(!isCloseListenerAvoided) onCloseListener?.handleDialogClose()

        super.onDestroy()
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        hideSystemUI()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    private fun setupView(view: View) {

        view.seekBar.setOnTouchListener { _, _ -> true }
        currentValue = parameterModel.reading


        if(isDecimalSupported(parameterModel.key)) {
            Log.i("CONTROLPARAMCHECK", "Handling Tinsp with value = $currentValue , min = ${(encoderValue.lowerLimit*10).toInt()}, max = ${(encoderValue.upperLimit*10).toInt()}")
            view.seekBar.min = (encoderValue.lowerLimit*10).toInt()
            view.seekBar.max = (encoderValue.upperLimit*10).toInt()
            view.seekBar?.progress = (currentValue*10).toInt()
            view.textRange.text = String.format("%.1f", currentValue)
            Log.i("TIMECHECK", "value of tinsp/tlow = ${currentValue}")

        }else{
            Log.i("CONTROLPARAMCHECK", "Handling Tinsp with value = $currentValue , min = ${(encoderValue.lowerLimit).toInt()}, max = ${(encoderValue.upperLimit).toInt()}")
            view.seekBar.min = encoderValue.lowerLimit.toInt()
            view.seekBar.max = encoderValue.upperLimit.toInt()
            view.textRange.text = currentValue.toInt().toString()
            view.seekBar?.progress = currentValue.toInt()
        }

        view.imageViewSubtract.setOnClickListener {

            subtraction()
        }

        view.imageViewAddition.setOnClickListener {

            addition()
        }
        view.okButton.setOnClickListener {
            ok()
        }

    }

    fun updateWithTimeoutDebounce(value: String) {


        when (value) {
            PREFIX_PLUS -> addition()
            PREFIX_MINUS -> subtraction()
            PREFIX_AND -> ok()

        }

    }

    fun startTimeoutWithDebounce(){

        cancelTimeout()

        visibilityTimeout = object: CountDownTimer(10000, 2000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i("DEBOUNCEKNOBCHECK", "Debounce timeout completed knob runing")

            }

            override fun onFinish() {
                Log.i("DEBOUNCEKNOBCHECK", "Debounce timeout completed knob")
//                dismiss()
                onTimeoutListener?.handleDialogClose()
                cancelTimeout()

            }
        }
        visibilityTimeout?.start()
    }

    fun cancelTimeout(){
        if(visibilityTimeout != null ){
            visibilityTimeout?.cancel()
            visibilityTimeout = null
            Log.i("DEBOUNCEKNOBCHECK", "Debounce destroying existing timeout knob")
        }
    }

    private fun isIRVActive() = prefManager?.readIRVStatus() ?: false

    private fun getIERatioLimits() : Pair<Int, Int>{
        val max = requireContext().getString(R.string.max_ie_ratio).toInt();
        val min = requireContext().getString( if(isIRVActive()) R.string.min_ie_ratio_irv else  R.string.min_ie_ratio).toInt();
        return Pair<Int, Int>(min, max);
    }

    private fun isIERatioValid(): Boolean{
        val rr = if(parameterModel.key == LBL_RR) currentValue.toInt() else prefManager?.readRR()?.toInt()
        val tinsp = if(parameterModel.key == LBL_TINSP) currentValue else prefManager?.readTinsp()

        if(rr != null && tinsp != null) {
            val (minIERatio, maxIERatio) = getIERatioLimits()
            val calculatedIERatio = Configs.calculateIERatio(rr, tinsp)
            return try {
                val eiRatio = calculatedIERatio.split(":")[1].toInt()
                eiRatio in minIERatio..maxIERatio
            } catch (err: Error) {
                Log.i("IE_CHECK", "IE Ratio verification failed")
                false
            }
        } else return false
    }

    private fun addition() {
        startTimeoutWithDebounce()
        val newValue = floatingPointFix(currentValue + encoderValue.step)
        var isNewValueValid = newValue <= encoderValue.upperLimit
        if(parameterModel.key == LBL_TINSP || parameterModel.key == LBL_RR) isNewValueValid = isNewValueValid && isIERatioValid()

        if (isNewValueValid) {
//            if (currentValue != encoderValue.upperLimit) {
            currentValue = newValue
            Log.i("CONTROLPARAMCHECK", "Increment = " + currentValue.toString() + " step = " + encoderValue.step)
//            }
        }
        // display value
        val displayValue = if(isDecimalSupported(parameterModel.key))  String.format("%.1f", currentValue) else currentValue.toInt().toString()
        view?.textRange?.text = displayValue

        // notify change in value
        onLimitChangeListener?.onLimitChange(parameterModel.reading, currentValue)


        // seekbar
        if(isDecimalSupported(parameterModel.key)) view?.seekBar?.progress = (currentValue*10).toInt()
        else view?.seekBar?.progress = currentValue.toInt()



    }

    private fun subtraction() {
        startTimeoutWithDebounce()
        val newValue = floatingPointFix(currentValue - encoderValue.step)
        var isNewValueValid = newValue >= lowerlimit
        if(parameterModel.key == LBL_TINSP || parameterModel.key == LBL_RR) isNewValueValid = isNewValueValid && isIERatioValid()

        if ( isNewValueValid) {
            if (currentValue > 0.0f) currentValue = newValue // TODO : why > 0.0f
            Log.i("CONTROLPARAMCHECK", "Decrement = " + currentValue.toString() + " step = " + encoderValue.step)
        }

        val displayValue = if(isDecimalSupported(parameterModel.key))  String.format("%.1f", currentValue) else currentValue.toInt().toString()
        view?.textRange?.text = displayValue
        // notify change in value
        onLimitChangeListener?.onLimitChange(parameterModel.reading, currentValue)


        if(isDecimalSupported(parameterModel.key)) view?.seekBar?.progress = (currentValue*10).toInt()
        else view?.seekBar?.progress = currentValue.toInt()




    }



    private fun ok() {
        onKnobPressListener?.onKnobPress(parameterModel.reading, currentValue)
        Log.i("Current_value ", "" + currentValue)
        isCloseListenerAvoided = true
        dialog?.dismiss()

    }


}



fun KnobDialog.setHeightWidth() {
/*
    dialog?.window?.apply {
        setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        decorView.apply {
            val params: WindowManager.LayoutParams = attributes
            params.x = -62
            params.y = 159
            val width = (resources.displayMetrics.widthPixels * 0.63).toInt()
            val height = dip2px(requireContext(), 805f).toInt()
            params.dimAmount = 0.0F
            params.screenBrightness = 1.0F
            params.width = width
            params.height = height
            attributes = params;
        }
    }
    hideSystemUI()
*/



    dialog?.window?.apply {
        //setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


//        if (status == true) {
        setGravity(Gravity.BOTTOM)
        isCancelable = cancelableStatus
        decorView.apply {
            val params: WindowManager.LayoutParams = attributes

            params.x = 600
            params.y = 102
            params.dimAmount = 0.0F
            params.screenBrightness = 5.0F
            params.width = resources.getDimension(R.dimen.knob_width).toInt()
            params.height = resources.getDimension(R.dimen.knob_height).toInt()

            attributes = params
        }

//        } else {
//            setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
//            isCancelable = true
//
//            decorView.apply {
//                val params: WindowManager.LayoutParams = attributes
//
//                params.y = 100
//                params.dimAmount = 0.0F
//                params.screenBrightness = 1.0F
//
//                params.width = widthDialog!! - 100
//                params.height = heightDialog!! + 20
//                attributes = params
//            }
//        }

    }

//    isCancelable = KnobDialog.cancelableStatus

    hideSystemUI()
}



