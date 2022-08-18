
package com.agvahealthcare.ventilator_ext.system.settings

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.alarm.limit_one.EncoderValue
import com.agvahealthcare.ventilator_ext.alarm.limit_one.KnobParameterModel
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.callback.OnKnobPressListener
import com.agvahealthcare.ventilator_ext.callback.OnLimitChangeListener
import com.agvahealthcare.ventilator_ext.callback.OnLoudnessAdjustmentListener
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.utility.KnobDialog
import com.agvahealthcare.ventilator_ext.utility.VOLUME_MAX_VALUE
import com.agvahealthcare.ventilator_ext.utility.VOLUME_MIN_VALUE
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_logs_dialog.view.textView
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.knob_progress_view.view.*
import kotlinx.android.synthetic.main.knob_progress_view.view.progress_bar
import kotlinx.android.synthetic.main.knob_progress_view_red.view.*


class SettingFragment : Fragment(), OnKnobPressListener, OnDismissDialogListener,
                        OnLimitChangeListener{

    private var customProgressDialog : KnobDialog?= null
    private var prefManager: PreferenceManager? = null
    var onLoudnessAdjustmentListener: OnLoudnessAdjustmentListener? = null
    var isViewClicked:Boolean=false

    companion object {
        const val TAG = "SettingFragment"


        fun newInstance(
            onLoudnessAdjustmentListener: OnLoudnessAdjustmentListener?
        ): SettingFragment {
            val args = Bundle()
            val fragment = SettingFragment()
            fragment.arguments = args
            fragment.onLoudnessAdjustmentListener = onLoudnessAdjustmentListener
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(requireContext())

        setDataViaPreference()
        setUpLoudness()
        setOnClickListener()
    }

    private fun setDataViaPreference() {
        prefManager?.apply {
            progressBarLoudness.progress_bar.max= VOLUME_MAX_VALUE

            progressBarLoudness.progress_bar.setProgress(readVolume().toInt())

            progressBarLoudness.textView.setText(""+readVolume().toInt())

        }
    }

//For the click listener the buttons need to be added in a view invoking the method
    // ClickListener on Buttons
    private fun setOnClickListener() {


        includeButtonLoudness.buttonView.text = getString(R.string.hint_loudness)
        includeButtonDayNight.buttonView.text = getString(R.string.hint_day_night)
        includeButtonDateTime.buttonView.text = getString(R.string.hint_date_time)

        includeButtonApply.buttonView.text = getString(R.string.hint_apply)

        includeButtonTest.buttonView.text = getString(R.string.hint_test)
        includeButtonDay.buttonView.text = getString(R.string.hint_day)
        includeButtonNight.buttonView.text = getString(R.string.hint_night)
        includeButtonAutomatic.buttonView.text = getString(R.string.hint_automatic)


        includeButtonTest.buttonView.setBackgroundResource(R.drawable.background_dark_grey)
        includeButtonDay.buttonView.setBackgroundResource(R.drawable.background_dark_grey)
        includeButtonNight.buttonView.setBackgroundResource(R.drawable.background_dark_grey)
        includeButtonAutomatic.buttonView.setBackgroundResource(R.drawable.background_dark_grey)
        includeButtonApply.buttonView.setBackgroundResource(R.drawable.background_dark_grey)

        includeButtonTest.buttonView.setPadding(50, 0, 50, 0)


        includeButtonLoudness.buttonView.setOnClickListener {
            setUpLoudness()
        }

        includeButtonDayNight.buttonView.setOnClickListener {
            includeButtonLoudness.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonDayNight.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected)
            includeButtonDateTime.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            layoutPanelLoudness.visibility = View.GONE
            layoutPanelDayNight.visibility = View.VISIBLE
            layoutPanelDateTime.visibility = View.GONE
            setPaddingData()

        }

        includeButtonDateTime.buttonView.setOnClickListener {

            includeButtonLoudness.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonDayNight.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonDateTime.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected)
            layoutPanelLoudness.visibility = View.GONE
            layoutPanelDayNight.visibility = View.GONE
            layoutPanelDateTime.visibility = View.VISIBLE
            setPaddingData()

        }



        progressBarLoudness.progress_bar.setOnClickListener {

            var encoder=EncoderValue(VOLUME_MIN_VALUE.toFloat(), VOLUME_MAX_VALUE.toFloat(),2.0f)
            prefManager?.readVolume()?.let { it1 ->
                KnobParameterModel(
                    Configs.LBL_Volume_KEY,
                    Configs.LBL_Volume_KEY,
                    1,
                    it1,
                    ""
                ).also {
                    customProgressDialog = KnobDialog.newInstance(
                        onKnobPressListener = this,
                        onTimeoutListener = this,
                        parameterModel =  it,
                        encoderValue = encoder,
                        onLimitChangeListener = this
                    )

                    customProgressDialog?.let { dialog ->
                        dialog.show(childFragmentManager, SettingFragment.TAG)
                        dialog.startTimeoutWithDebounce()
                    }
                }
            }
        }
    //Need to be replaced and the

        includeButtonTest.buttonView.setOnClickListener {

            if(!isViewClicked){
                isViewClicked = true;
                onLoudnessAdjustmentListener?.onCheckLoudness()
                startTimer()
            } else {

            }

        }
    }

    private fun startTimer() {


        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                isViewClicked = false
            }
        }.start()


    }

//Needs to be cleared out here and the method will contain the normalization and the highlighting mechanism.

    private fun setPaddingData() {

        includeButtonLoudness.buttonView.setPadding(15, 10, 15, 10)
        includeButtonDayNight.buttonView.setPadding(15, 10, 15, 10)
        includeButtonDateTime.buttonView.setPadding(15, 10, 15, 10)
        includeButtonTest.buttonView.setPadding(50, 0, 50, 0)
        includeButtonDay.buttonView.setPadding(55, 0, 55, 0)
        includeButtonNight.buttonView.setPadding(55, 0, 55, 0)
        includeButtonAutomatic.buttonView.setPadding(35, 0, 35, 0)
        includeButtonApply.buttonView.setPadding(55, 10, 55, 10)

    }

    private fun setUpLoudness() {
        includeButtonLoudness.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected)
        includeButtonDayNight.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonDateTime.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        layoutPanelLoudness.visibility = View.VISIBLE
        layoutPanelDayNight.visibility = View.GONE
        layoutPanelDateTime.visibility = View.GONE
        setPaddingData()


    }

    fun updateKnobSetting(data: String) {
        customProgressDialog?.updateWithTimeoutDebounce(data)

    }



    override fun onKnobPress(previousValue: Float, newValue: Float) {
        Log.i("LOUDNESSCHECK", "Set value = $newValue")
        progressBarLoudness.progress_bar.max= VOLUME_MAX_VALUE
        progressBarLoudness.progress_bar.setProgress(newValue.toInt())

        progressBarLoudness.textView.setText(""+newValue.toInt())
        prefManager?.setVolume(newValue)
        onLoudnessAdjustmentListener?.onCheckLoudness()
        startTimer()

    }


    override fun handleDialogClose() {
        customProgressDialog?.takeIf { it.isVisible }?.dismiss()
    }
//In the limit change the interface will call the on Limit change method the previous value is swapped with the new value.


    override fun onLimitChange(previousValue: Float, newValue: Float) {
        view.let {
            (it?.progress_bar as? ProgressBar)?.apply {
                this.progress = newValue.toInt()
            }
            //View needs to be updated with the sync in knob and the text view will also be in sync
            (it?.textView as? TextView)?.apply {
                this.text = newValue.toInt().toString()
            }
        }
    }
}