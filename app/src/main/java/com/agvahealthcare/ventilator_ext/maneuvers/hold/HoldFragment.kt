package com.agvahealthcare.ventilator_ext.maneuvers.hold

import android.os.Build
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import java.time.LocalDateTime

import java.time.format.DateTimeFormatter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.callback.SimpleCallbackListener
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager

import com.agvahealthcare.ventilator_ext.service.CommunicationService
import com.agvahealthcare.ventilator_ext.utility.utils.Configs

import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_hold.*
import java.util.*


class HoldFragment(
    private val communicationService: CommunicationService?,
    private val onCloseListener: SimpleCallbackListener?,
    private val isFromKnob: String?,

    ) : Fragment() {
    private var pPlat: Float? = null
    private var staticCompliance: Float? = null
    private var autoPeep: Float? = null
    private var preferenceManager: PreferenceManager? = null
    private var minTimeLimit: Float = 0.0f
    private var maxTimeLimit: Float = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        return inflater.inflate(R.layout.fragment_hold, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager = PreferenceManager(requireContext())
        setupData()

        preferenceManager?.apply {
            staticCompliance = readManeuversStaticComplianceValue()
            pPlat = readManeuversPplatValue()
            autoPeep = readManeuversAutoPeepValue()
            val limits = readManeuversPplatLimits()

            // null safety & condition check
            if(limits.size>1){
                minTimeLimit = limits[0]!!
                maxTimeLimit = limits[1]!!
                tvTime.text = minTimeLimit.toString()
            }

            /* limits?.takeIf { it.size > 2 && it[0] != null && it[1] != null }?.apply {
                 minTimeLimit = this[0]!!
                 maxTimeLimit = this[1]!!
                 tvTime.text = minTimeLimit.toString()

             }*/
        }

        aftrackUpdate.visibility = View.GONE
        setOnClickListener()
        btnInc.setOnClickListener {
            clickAdd()

        }

        btnDec.setOnClickListener {

            clickSubtract()
        }

        btnInspHoldStart.setOnClickListener {
            inspiraterClicView()
        }


        btnExpiratHoldStart.setOnClickListener {
            expiratoryClickView()
        }

        ok.setOnClickListener {
            aftrackUpdate.visibility = View.GONE
            expirataryHoldLayout.visibility = View.VISIBLE
            inspirataryHoldLayout.visibility = View.VISIBLE
            preferenceManager?.apply {
                setManeuversPplatLimits(minTimeLimit, maxTimeLimit)
            }

            setupData()

        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    @RequiresApi(Build.VERSION_CODES.O)
    val formatted = current.format(formatter)
    @RequiresApi(Build.VERSION_CODES.O)
    val inspTime = current.format(formatter)

    private fun expiratoryClickView() {
        aftrackUpdate.visibility = View.VISIBLE
        setDurationBoxLayout.visibility = View.GONE


        try {
            val timePeriod = String.format("%.1f", tvTime.text.toString().toFloat())
            communicationService?.takeIf { it.isPortsConnected }?.apply {
                send(resources.getString(R.string.prefix_expiratory_hold) + timePeriod)
                onCloseListener?.doAction()

            }

            preferenceManager?.apply {
                setMeasureTime(timePeriod)
            }
        } catch (e: Exception) {
            Log.e("PARSE_ERROR", "Error in parsing expiratory time period")
            e.printStackTrace()
        }
    }

    private fun inspiraterClicView() {
        aftrackUpdate.visibility = View.VISIBLE
        setDurationBoxLayout.visibility = View.GONE

        try {
            val timePeriod = String.format("%.1f", tvTime.text.toString().toFloat())

            communicationService?.takeIf { it.isPortsConnected }?.apply {
                send(resources.getString(R.string.prefix_inspiratory_hold) + timePeriod)
                onCloseListener?.doAction()

            }

            preferenceManager?.apply {
                setMeasureTime(timePeriod)
            }
        } catch (e: Exception) {
            Log.e("PARSE_ERROR", "Error in parsing expiratory time period")
            e.printStackTrace()
        }
    }

    // setUp Data
    private fun setupData() {
        includeButtonInspirationHold.buttonView.text = getString(R.string.hint_inspiration_hold)
        includeButtonExpirationHold.buttonView.text = getString(R.string.hint_expiration_hold)
        includeButtonInspirationHold.buttonView.setPaddingRelative(40, 10, 40, 10)
        includeButtonExpirationHold.buttonView.setPaddingRelative(42, 10, 42, 10)


        preferenceManager?.apply {

            if (readManeuversPplatValue().toString() == "0.0") {
                textViewInspirationHoldValue.text = "N/A"
            } else {
                textViewInspirationHoldValue.text = readManeuversPplatValue().toString()
            }


            if (readManeuversAutoPeepValue().toString() == "0.0") {
                textViewExpirationHoldValue.text = "N/A"
            } else {
                textViewExpirationHoldValue.text = readManeuversAutoPeepValue().toString()
            }

            textViewInspirationHoldTime.text = readInspiratoryDate()
            textViewExpirationHoldTime.text = readExpiratoryDate()

        }


    }


    // ClickListener on Buttons
    private fun setOnClickListener() {

        if(isFromKnob.equals(Configs.EXPIRATORY_HOLD)){
            setDurationBoxLayout.visibility = View.VISIBLE
            currentTextView.setText(Configs.EXPIRATORY_HOLD)
            btnExpiratHoldStart.visibility = View.VISIBLE
            btnInspHoldStart.visibility = View.GONE

            expirataryHoldLayout.visibility = View.GONE
            inspirataryHoldLayout.visibility = View.GONE
        }else if(isFromKnob.equals(Configs.INSPIRATORY_HOLD)){
            setDurationBoxLayout.visibility = View.VISIBLE
            currentTextView.setText(Configs.INSPIRATORY_HOLD)


            btnInspHoldStart.visibility = View.VISIBLE
            btnExpiratHoldStart.visibility = View.GONE


            inspirataryHoldLayout.visibility = View.GONE
            expirataryHoldLayout.visibility = View.GONE
        }
        else{
            setDurationBoxLayout.visibility = View.GONE
            includeButtonInspirationHold.buttonView.setOnClickListener {

                currentTextView.setText(Configs.INSPIRATORY_HOLD)

                setDurationBoxLayout.visibility = View.VISIBLE

                btnInspHoldStart.visibility = View.VISIBLE
                btnExpiratHoldStart.visibility = View.GONE


                inspirataryHoldLayout.visibility = View.GONE
                expirataryHoldLayout.visibility = View.GONE

            }

            includeButtonExpirationHold.buttonView.setOnClickListener {

                setDurationBoxLayout.visibility = View.VISIBLE
                currentTextView.setText(Configs.EXPIRATORY_HOLD)


                btnExpiratHoldStart.visibility = View.VISIBLE
                btnInspHoldStart.visibility = View.GONE

                expirataryHoldLayout.visibility = View.GONE
                inspirataryHoldLayout.visibility = View.GONE


            }
        }



    }


    fun updateInspiratory(observedPlat: Float, compliance: Float?) {
        ok.visibility = View.VISIBLE
        pPlat = observedPlat
        staticCompliance = compliance
        pPlatText.text = observedPlat.toString()
        staticComplince.text = compliance.toString()

        preferenceManager?.apply {
            setManeuversPplatValue(observedPlat)
            setManeuversStaticComplianceValue(compliance)
            // setInspiratoryDate(AppUtils.getCurrentDateTime())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setExpiratoryDate("$formatted")
            }
        }

    }

    fun updateExpiratory(cachedPeep: Float) {
        ok.visibility = View.VISIBLE
        autoPeep = cachedPeep
        pPlatText.text = cachedPeep.toString()
        preferenceManager?.apply {
            setManeuversAutoPeepValue(cachedPeep)
            //setExpiratoryDate(AppUtils.getCurrentDateTime())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setExpiratoryDate("$formatted")
            }
        }


    }

    fun updateKnobData(knobData: String?) {
        when (knobData) {
            Configs.PREFIX_PLUS -> clickAdd()
            Configs.PREFIX_MINUS -> clickSubtract()
            Configs.PREFIX_AND -> clickOk()

        }


    }

    private fun clickOk() {
        //TODO("Not yet implemented")
    }

    private fun clickAdd() {
        try {
            var currentTime = tvTime.text.toString().toFloat()
            currentTime = currentTime.plus(0.5f)
            if (currentTime <= maxTimeLimit && currentTime >= minTimeLimit)
                tvTime.text = String.format("%.1f", currentTime);
        } catch (e: Exception) {
            Log.e("PARSE_ERROR", "Error in parsing expiratory time period")
            e.printStackTrace()
        }
    }

    private fun clickSubtract() {
        try {
            var currentTime = tvTime.text.toString().toFloat()
            currentTime -= 0.5f
            if (currentTime <= maxTimeLimit && currentTime >= minTimeLimit)
                tvTime.text = String.format("%.1f", currentTime)
        } catch (e: Exception) {
            Log.e("PARSE_ERROR", "Error in parsing expiratory time period")
            e.printStackTrace()
        }
    }

}