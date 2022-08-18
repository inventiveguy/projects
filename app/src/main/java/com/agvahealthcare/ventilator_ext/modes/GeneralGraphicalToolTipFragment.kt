package com.agvahealthcare.ventilator_ext.modes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.dashboard.DashBoardActivity
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.model.ControlParameterModel
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import kotlinx.android.synthetic.main.fragment_pcv_setting_dialog.*

class GeneralGraphicalToolTipFragment : GraphicTooltipFragment("SettingPcv") {

    private var preferenceManager: PreferenceManager? = null
    private var closeListener: OnDismissDialogListener? = null

    companion object {
        var fragment: GeneralGraphicalToolTipFragment? = null
        fun newInstance(
            closeListener: OnDismissDialogListener?,
        ): GeneralGraphicalToolTipFragment {
            if(fragment == null) {
                fragment = GeneralGraphicalToolTipFragment()
                fragment?.closeListener = closeListener
            }

            return fragment!!
        }
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_pcv_setting_dialog, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager = PreferenceManager(requireContext())
       // setHeightWidth()
        setDataOnViewViaPreferences()

        imageViewCross.setOnClickListener {
          /*  requireActivity().supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commitNow()*/
            // listener which closes both knob dialog and this dialog
            closeListener?.handleDialogClose()

        }
    }

    override fun setDataOnViewViaPreferences() {
        preferenceManager?.apply {

            val buff ="${getString(R.string.ti)} = ${readTinsp()} ${getString(R.string.hint_s)}"
            textViewTinsp.text = "${getString(R.string.ti)} = ${readTinsp()} ${getString(R.string.hint_s)}"

            val setTexp = Configs.calculateTexp(readRR().toInt(),readTinsp() )
            textViewTexp.text = "${getString(R.string.te)} = $setTexp ${getString(R.string.hint_s)}"

            textViewPeepPi.text =  "${getString(R.string.peep_plus_ps)} = ${readPEEP().toInt() + readPplat().toInt()} ${getString(R.string.hint_cmH2o)}"

            val setTot = Configs.calculateTtot(readRR().toInt())
            textViewTtot.text = "${getString(R.string.titot)} = $setTot"

            textViewPeepPiSign.text="${getString(R.string.pinsp)} = ${readSupportPexp()}"
            //String.format("%02d",read)String.format("%02d",readSupportPressure())}
            //textViewPi.text="${getString(R.string.ps)} = "+String.format("%02f",readSupportPressure())
            textViewPi.text="${getString(R.string.ps)}=${readSupportPressure()}"

            textViewIERatio.text= "${getString(R.string.ieratio)}=${Configs.calculateIERatio(readRR().toInt(), readTinsp())}"
        }
    }


    override fun updateDataOnView(parameter: ControlParameterModel) {

        preferenceManager?.apply {
            when(parameter.ventKey){
                Configs.LBL_TINSP -> {
                    try {
                        val setTexp = Configs.calculateTexp(readRR().toInt(), parameter.reading.toFloat() )
                        textViewTexp.text = "${getString(R.string.te)} = $setTexp ${getString(R.string.hint_s)}"
                    } catch (e: Exception){
                        e.printStackTrace()
                    }

                    textViewIERatio.text= "${getString(R.string.ieratio)}=${Configs.calculateIERatio(readRR().toInt(), parameter.reading.toFloat())}"
                    textViewTinsp.text = "${getString(R.string.ti)} = ${String.format("%.1f", parameter.reading.toFloat())} ${getString(R.string.hint_s)}"
                }
                Configs.LBL_RR -> {
                    try {
                        val setTexp = Configs.calculateTexp(parameter.reading.toInt(), readTinsp())
                        textViewTexp.text = "${getString(R.string.te)} = $setTexp ${getString(R.string.hint_s)}"

                        val setTot = Configs.calculateTtot(parameter.reading.toInt())
                        textViewTtot.text = "${getString(R.string.titot)} = $setTot"
                        textViewIERatio.text= "${getString(R.string.ieratio)}=${Configs.calculateIERatio(parameter.reading.toInt(), readTinsp())}"

                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }

                Configs.LBL_PPLAT -> {
                    textViewPeepPi.text = "${getString(R.string.peep_plus_ps)} = ${readPEEP().toInt() + parameter.reading.toInt()} ${getString(R.string.hint_cmH2o)}"
                }

                Configs.LBL_PEEP -> {
                    textViewPeepPi.text = "${getString(R.string.peep_plus_ps)} = ${parameter.reading.toInt() + readPplat().toInt()} ${getString(R.string.hint_cmH2o)}"
                }

                Configs.LBL_SUPPORT_PRESSURE-> {
                    textViewPi.text = "${getString(R.string.ps)} = ${
                        String.format(
                            "%.1f",
                            parameter.reading.toFloat()
                        )
                    }"
                }

            }
        }
    }
}