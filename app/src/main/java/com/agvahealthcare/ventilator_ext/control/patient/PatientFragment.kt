package com.agvahealthcare.ventilator_ext.control.patient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.utility.CustomCountDownTimer
import com.agvahealthcare.ventilator_ext.utility.PATIENT_ADULT_HEIGHT_UPPER
import com.agvahealthcare.ventilator_ext.utility.PATIENT_ADULT_WEIGHT_UPPER
import com.agvahealthcare.ventilator_ext.utility.PATIENT_AGE_UPPER
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.content_female_layout.view.*
import kotlinx.android.synthetic.main.content_male_layout.view.*
import kotlinx.android.synthetic.main.fragment_patient.*
import kotlinx.android.synthetic.main.knob_progress_view_red.view.*

class PatientFragment : Fragment() {

    private var prefManager: PreferenceManager? = null
    private var customCountDownTimer: CustomCountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_patient, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(requireContext())

        initViewViaPreference()
        setOnClickListener()

    }

    private fun initViewViaPreference() {

        prefManager?.apply {

            readBodyHeight()?.toDouble()?.toInt()
                ?.let { includeProgressHeight.progress_bar.setProgress(it) }
            readBodyWeight()?.toDouble()?.toInt()
                ?.let { includeProgressWeight.progress_bar.setProgress(it) }
            readAge()?.toDouble()?.toInt()?.let { includeProgressAge.progress_bar.setProgress(it) }

            includeProgressAge.progress_bar.max = PATIENT_AGE_UPPER
            includeProgressHeight.progress_bar.max = PATIENT_ADULT_HEIGHT_UPPER
            includeProgressWeight.progress_bar.max = PATIENT_ADULT_WEIGHT_UPPER

            includeProgressAge.textView.text = readAge()?.toDouble()?.toInt().toString()
            includeProgressHeight.textView.text = readBodyHeight()?.toDouble()?.toInt().toString()
            includeProgressWeight.textView.text = readBodyWeight()?.toDouble()?.toInt().toString()

            readCurrentUid()?.let {
                when(it){
                    Configs.PatientProfile.TYPE_ADULT -> {
                        patientType.setText("ADULT")
                    }
                    Configs.PatientProfile.TYPE_PED -> {
                        patientType.setText("Pediatric")
                    }
                }
            }

            if (Configs.Gender.TYPE_MALE == readGender()) setDataMale()
            else setDataFemale()


        }
    }

    private fun setOnClickListener() {

        includeButtonReset.buttonView.text = getString(R.string.hint_reset)
        includeButtonReset.buttonView.setTextColor(getResources().getColor(R.color.white))
        includeButtonReset.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonReset.buttonView.setPadding(50, 0, 50, 0)


        includeButtonReset.buttonView.setOnClickListener {
            customCountDownTimer?.count=0L
           // Toast.makeText(context,"click",Toast.LENGTH_LONG).show()

        }
        /*progressBarHeight.layoutPanel.setOnClickListener {

        }*/
    }


    private fun setDataMale() {
        layoutMale.imageViewMale.setImageResource(R.drawable.ic_male_select)
        layoutMale.buttonMale.setBackgroundResource(R.drawable.background_green_border)
        layoutMale.buttonMale.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        layoutFemale.imageViewFemale.setImageResource(R.drawable.ic_female_unselect)
        layoutFemale.buttonFemale.setBackgroundResource(R.drawable.background_medium_grey)
        layoutFemale.buttonFemale.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )
    }

    private fun setDataFemale() {
        layoutMale.imageViewMale.setImageResource(R.drawable.ic_male_unselect)
        layoutMale.buttonMale.setBackgroundResource(R.drawable.background_medium_grey)
        layoutMale.buttonMale.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        layoutFemale.imageViewFemale.setImageResource(R.drawable.ic_female_select)
        layoutFemale.buttonFemale.setBackgroundResource(R.drawable.background_green_border)
        layoutFemale.buttonFemale.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
    }

    fun setVentilationTime(counterState: String, customCountDownTimer: CustomCountDownTimer) {
        this.customCountDownTimer=customCountDownTimer
        textViewTime?.text = counterState
    }
}