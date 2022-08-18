package com.agvahealthcare.ventilator_ext.maneuvers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.callback.SimpleCallbackListener
import com.agvahealthcare.ventilator_ext.maneuvers.hold.HoldFragment
import com.agvahealthcare.ventilator_ext.maneuvers.utilities.UtilitiesFragment
import com.agvahealthcare.ventilator_ext.service.CommunicationService
import com.agvahealthcare.ventilator_ext.utility.replaceFragment
import com.agvahealthcare.ventilator_ext.utility.setHeightWidthPercent
import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_maneuvers_dialog.*


class ManeuversDialogFragment : DialogFragment(), SimpleCallbackListener {

    companion object {

        const val TAG = "ManeuversDialog"
        private const val KEY_HEIGHT = "KEY_HEIGHT"
        private const val KEY_WIDTH = "KEY_WIDTH"
        private const val KEY_IS_KNOB = "KEY_KNOB"
        fun newInstance(
            height: Int?,
            width: Int?,
            isKnob:String?,
            closeListener: OnDismissDialogListener?,
            communicationService: CommunicationService?
        ): ManeuversDialogFragment {
            val args = Bundle()
            height?.let { args.putInt(KEY_HEIGHT, it) }
            width?.let { args.putInt(KEY_WIDTH, it) }
            isKnob?.let{ args.putString(KEY_IS_KNOB,it)}

            val fragment = ManeuversDialogFragment()
            fragment.arguments = args
            fragment.closeListener = closeListener
            fragment.communicationService=communicationService
            return fragment
        }
    }

    private var closeListener: OnDismissDialogListener? = null
    private var communicationService: CommunicationService? = null
    var fragmentHold:HoldFragment?=null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_maneuvers_dialog, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
        setUpHold()
        setOnClickListener()
    }


    // clickListener on Buttons
    private fun setOnClickListener() {

        includeButtonHold.buttonView.text = getString(R.string.hint_hold)
        includeButtonUtilities.buttonView.text = getString(R.string.hint_utilities)

        includeButtonUtilities.buttonView.isClickable = false
        includeButtonUtilities.buttonView.isFocusable = false
        includeButtonUtilities.buttonView.isEnabled = false
        includeButtonUtilities.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        includeButtonUtilities.buttonView.setBackgroundResource(R.drawable.background_white_border_white)
        includeButtonUtilities.buttonView.alpha=0.3f

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


        includeButtonHold.buttonView.setOnClickListener {
            setUpHold()
        }

        includeButtonUtilities.buttonView.setOnClickListener {
            val fragment = UtilitiesFragment()
            fragment.apply {
                replaceFragment(this,this::class.java.javaClass.simpleName, R.id.maneuvers_nav_container )
            }

            includeButtonHold.buttonView.setBackgroundResource(R.drawable.background_white_border_white)
            includeButtonHold.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            setPaddingButton()
        }
    }

    //By Default Fragment
    private fun setUpHold() {
        fragmentHold = HoldFragment(communicationService, this ,arguments?.getString(KEY_IS_KNOB))
        fragmentHold?.apply {
            replaceFragment(this,this::class.java.javaClass.simpleName, R.id.maneuvers_nav_container )
        }

        includeButtonHold.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
        includeButtonHold.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        setPaddingButton()
    }

    private fun setPaddingButton() {
        includeButtonHold.buttonView.setPadding(45, 10, 45, 10)
        includeButtonUtilities.buttonView.setPadding(35, 10, 35, 10)
    }

    override fun onStart() {
        super.onStart()
        val heightDialog = arguments?.getInt(KEY_HEIGHT)
        val widthDialog = arguments?.getInt(KEY_WIDTH)

        setHeightWidthPercent(heightDialog , widthDialog , true)
    }


    fun updateInspiratory(observedPlat: Float, staticCompliance: Float?) {
        fragmentHold?.takeIf { it.isVisible }?.apply {
            updateInspiratory(observedPlat,staticCompliance)
        }


    }
    fun updateExpiratory(cachedPeep: Float) {
        fragmentHold?.takeIf { it.isVisible }?.apply {
            updateExpiratory(cachedPeep)
        }

    }

    fun updateKnob(data: String?) {
        fragmentHold?.takeIf { it.isVisible }?.apply {
            updateKnobData(data)
        }
    }




    override fun doAction() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .remove(this)
            .commitNow()
//            requireActivity().supportFragmentManager.popBackStack()

        closeListener?.handleDialogClose()
    }

}