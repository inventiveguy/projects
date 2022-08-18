package com.agvahealthcare.ventilator_ext.monitoring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.model.ObservedParameterModel
import com.agvahealthcare.ventilator_ext.monitoring.general.GeneralFragment
import com.agvahealthcare.ventilator_ext.monitoring.plateau.PlateauFragment
import com.agvahealthcare.ventilator_ext.monitoring.spo.SpO2Fragment
import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_monitoring_dialog.*


class MonitoringDialogFragment : DialogFragment() {


    companion object {
        const val TAG = "MonitoringDialog"
        private const val KEY_HEIGHT = "KEY_HEIGHT"
        private const val KEY_WIDTH = "KEY_WIDTH"
        private  var  observedList = ArrayList<ObservedParameterModel>()
        private  var  observedSpHList = ArrayList<ObservedParameterModel>()

        fun newInstance(
            height: Int?,
            width: Int?,
            observedValueList: ArrayList<ObservedParameterModel>,
            observedValueSpHList: ArrayList<ObservedParameterModel>,
            closeListener: OnDismissDialogListener?
        ): MonitoringDialogFragment {
            val args = Bundle()
            height?.let { args.putInt(KEY_HEIGHT, it) }
            width?.let { args.putInt(KEY_WIDTH, it) }
            observedList = observedValueList
            observedSpHList = observedValueSpHList
            val fragment = MonitoringDialogFragment()
            fragment.arguments = args
            fragment.closeListener = closeListener
            return fragment
        }
    }
    private var closeListener: OnDismissDialogListener? = null
    private var generalFragment : GeneralFragment ? = null
    private var spO2Fragment : SpO2Fragment ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val layout =inflater.inflate(R.layout.fragment_monitoring_dialog, container, false)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
        setUpGeneral()
        setupClickListener()
    }



    // ClickListener on Button
    private fun setupClickListener() {

        includeButtonGeneral.buttonView.text = getString(R.string.hint_general)
        includeButtonSPO2.buttonView.text = getString(R.string.hint_spo2)
        includeButtonCO2.buttonView.text = getString(R.string.hint_co2)
        includeButtonManeuvers.buttonView.text = getString(R.string.hint_maneuvers)

        includeButtonCO2.buttonView.isEnabled    = false
        includeButtonCO2.buttonView.isFocusable  = false
        includeButtonCO2.buttonView.isClickable = false

        includeButtonCO2.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.disable_grey))
        includeButtonCO2.buttonView.setBackgroundResource(R.drawable.background_light_grey)
        includeButtonCO2.buttonView.alpha=0.3f

        imageViewCross.setOnClickListener {



            requireActivity().supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commitNow()
//            requireActivity().supportFragmentManager.popBackStack()

            closeListener?.handleDialogClose()


//            dismiss()
        }

        includeButtonGeneral.buttonView.setOnClickListener {
            setUpGeneral()
        }


        includeButtonSPO2.buttonView.setOnClickListener {

            spO2Fragment = SpO2Fragment()
            spO2Fragment?.apply {
               val bundle = Bundle()
                bundle.putSerializable("observedSpHList", observedSpHList)
                this.arguments = bundle


                this@MonitoringDialogFragment.childFragmentManager.beginTransaction()
                    .replace(R.id.monitoring_nav_container, spO2Fragment!!,tag ).commit()

            }


            includeButtonGeneral.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonSPO2.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
            includeButtonManeuvers.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)


            includeButtonGeneral.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            includeButtonSPO2.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            setPaddingOnButtons()

        }

        includeButtonManeuvers.buttonView.setOnClickListener {
            generalFragment = null
            spO2Fragment = null
            val fragment = PlateauFragment()
            fragment.apply {
               // replaceFragment(this,this::class.java.javaClass.simpleName, R.id.monitoring_nav_container )

                this@MonitoringDialogFragment.childFragmentManager.beginTransaction()
                    .replace(R.id.monitoring_nav_container, fragment!!,tag ).commit()

            }

            includeButtonGeneral.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonSPO2.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
            includeButtonManeuvers.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected)

            setPaddingOnButtons()

        }
    }

    //By Default Fragment
    private fun setUpGeneral() {
       // spO2Fragment = null
        generalFragment = GeneralFragment()

        generalFragment?.apply {
            val bundle = Bundle()
            bundle.putSerializable("observedList", observedList)
            this.arguments = bundle


            this@MonitoringDialogFragment.childFragmentManager.beginTransaction()
                .replace(R.id.monitoring_nav_container, generalFragment!!,tag ).commit()

        }

        includeButtonGeneral.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
        includeButtonGeneral.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        includeButtonSPO2.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonManeuvers.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)


        includeButtonSPO2.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))



        setPaddingOnButtons()
    }

    private fun setPaddingOnButtons() {
       // includeButtonGeneral.buttonView.setPadding(30,0,30,0)
       // includeButtonCO2.buttonView.setPadding(50,0,50,0)
       // includeButtonSPO2.buttonView.setPadding(50,0,50,0)
        //includeButtonManeuvers.buttonView.setPadding(20,0,20,0)
        includeButtonManeuvers.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))


    }

    override fun onStart() {
        super.onStart()
        val heightDialog = arguments?.getInt(KEY_HEIGHT)
        val widthDialog = arguments?.getInt(KEY_WIDTH)

//        setHeightWidthPercent(heightDialog , widthDialog , true)

    }


    fun setModeList(observedValueList: ArrayList<ObservedParameterModel>) {
        generalFragment?.takeIf { it.isVisible }?.apply {
            setUpModeData(observedValueList)
        }
    }


    fun setSpo2DataList(observedValueSpo2List: ArrayList<ObservedParameterModel>) {
        spO2Fragment?.takeIf { it.isVisible }?.apply {
            setData(observedValueSpo2List)
        }
    }

}