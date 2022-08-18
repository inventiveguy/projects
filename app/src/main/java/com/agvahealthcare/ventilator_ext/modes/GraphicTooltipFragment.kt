package com.agvahealthcare.ventilator_ext.modes

import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import com.agvahealthcare.ventilator_ext.model.ControlParameterModel


abstract class GraphicTooltipFragment(val TAG: String): Fragment(){


    var visibilityTimeout: CountDownTimer? =null

    /*override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
        //startTimeoutWithDebounce()
    }*/

    init {
        Log.i("GRAPHDIALOGCHECK", "Created graph tool tip dialog")
    }

    override fun onDestroy() {
        cancelTimeout()
        Log.i("GRAPHDIALOGCHECK", "Destroyed graph tool tip dialog")
        super.onDestroy()
    }

    fun startTimeoutWithDebounce(){

        cancelTimeout()


        visibilityTimeout = object: CountDownTimer(10000, 2000) {
            override fun onTick(millisUntilFinished: Long) {

                Log.i("DEBOUNCEGRAPHICCHECK", "Debounce timeout runing  Graphic")

            }

            override fun onFinish() {
                Log.i("DEBOUNCEGRAPHICCHECK", "Debounce timeout completed Graphic")
                //dismiss()

                cancelTimeout()
            }
        }
        visibilityTimeout?.start()
    }

    fun cancelTimeout(){
        if(visibilityTimeout != null ){
            visibilityTimeout?.cancel()
            visibilityTimeout = null
            Log.i("DEBOUNCEGRAPHICCHECK", "Debounce destroying existing timeout Graphic ")
        }
    }


    abstract fun setDataOnViewViaPreferences()
    abstract fun updateDataOnView(parameter: ControlParameterModel)
}

/*fun GraphicTooltipFragment.setHeightWidth(){
    dialog?.window?.apply {
        setGravity(Gravity.START or Gravity.BOTTOM)
        decorView.apply {
            val params: WindowManager.LayoutParams = attributes

            params.x = 212
            params.y = 124
            params.dimAmount = 0.0F
            params.screenBrightness = 5.0F
//            params.width = resources.getDimension(R.dimen.graphic_tooltip_width).toInt()
//            params.height = heightDialog!!
            attributes = params
        }
    }

    isCancelable = false

    hideSystemUI()


}*/
