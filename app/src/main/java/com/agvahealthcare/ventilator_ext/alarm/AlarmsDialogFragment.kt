package com.agvahealthcare.ventilator_ext.alarm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.alarm.buffer.BufferFragment
import com.agvahealthcare.ventilator_ext.alarm.limit_one.LimitOneFragment
import com.agvahealthcare.ventilator_ext.alarm.limit_two.LimitTwoFragment
import com.agvahealthcare.ventilator_ext.callback.OnAlarmLimitChangeListener
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.service.CommunicationService
import com.agvahealthcare.ventilator_ext.utility.hideSystemUI
import com.agvahealthcare.ventilator_ext.utility.replaceFragment
import com.agvahealthcare.ventilator_ext.utility.setHeightWidthPercent
import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_alarm_dialog.*


class AlarmDialogFragment : DialogFragment(){

    companion object {
        const val TAG = "AlarmDialog"
        private const val KEY_HEIGHT = "KEY_HEIGHT"
        private const val KEY_WIDTH = "KEY_WIDTH"


        fun newInstance(
            height: Int?,
            width: Int?,
            communicationService: CommunicationService?,
            closeListener: OnDismissDialogListener?,
            onAlarmLimitChangeListener:OnAlarmLimitChangeListener?,
        ): AlarmDialogFragment {
            val args = Bundle()
            height?.let { args.putInt(KEY_HEIGHT, it) }
            width?.let { args.putInt(KEY_WIDTH, it) }
            val fragment = AlarmDialogFragment()
            fragment.arguments = args
            fragment.service = communicationService
            fragment.closeListener = closeListener
            fragment.onAlarmLimitChangeListener = onAlarmLimitChangeListener

            return fragment
        }
    }

    private var closeListener: OnDismissDialogListener? = null
    private var onAlarmLimitChangeListener: OnAlarmLimitChangeListener? = null

    private var limitOneFragment: LimitOneFragment? = null
    private var limitTwoFragment: LimitTwoFragment? = null
    private var bufferFragment:BufferFragment?=null
    private var heightDialog:Int?=null
    private var widthDialog:Int?=null
    private var bundle:Bundle?=null

    var service: CommunicationService? = null
    private var alarmsDialogFragmentObserver:AlarmsDialogFragmentObserver?=null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        alarmsDialogFragmentObserver=AlarmsDialogFragmentObserver()
        alarmsDialogFragmentObserver.apply {
            this?.let { this@AlarmDialogFragment.lifecycle.addObserver(it) }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_alarm_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)

        /*dashBoardViewModel=ViewModelProvider(requireActivity()).get(DashBoardViewModel::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            //Do something after 100ms
            dashBoardViewModel?.select(0)
        }, 2000)
        dashBoardViewModel?.ackListViewModel?.observe(viewLifecycleOwner, Observer { it->
            ackListFragment=it
        })*/

        //setUpLimitOne()
        setupClickListener()
        //setPaddingOnButton()

    }

    override fun onStart() {
        super.onStart()
        Log.i("ACTIVITY_LIFECYCLE", "ON_START $javaClass")
        bundle=this.arguments
    }

    fun update(ackValue: String) {

        limitOneFragment?.takeIf { it.isVisible }?.apply {
            updateKnob(ackValue)
        }
        limitTwoFragment?.takeIf { it.isVisible }?.apply {
            updateKnob(ackValue)
        }
    }


    // setPadding on Buttons
    private fun setPaddingOnButton() {
        //includeButtonLimit1.buttonView.setPadding(35, 0, 35, 0)
        //includeButtonLimit2.buttonView.setPadding(35, 0, 35, 0)
        // includeButtonBuffer.buttonView.setPadding(40, 0, 40, 0)
        // includeButtonLimit1.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        // includeButtonLimit2.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))


        includeButtonBuffer.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        includeButtonBuffer.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonBuffer.buttonView.alpha=0.3f

    }

    //By Default Fragment
    private fun setUpLimitOne() {

        if(limitOneFragment == null ) onAlarmLimitChangeListener?.let {
            limitOneFragment = LimitOneFragment(service, it)
        }

        limitOneFragment?.apply {
            replaceFragment(this,this::class.java.javaClass.simpleName, R.id.alarm_nav_container )
        }
        includeButtonLimit2.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonLimit1.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)

        includeButtonLimit2.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonLimit1.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        includeButtonBuffer.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonBuffer.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        //setPaddingOnButton()

    }

    // ClickListener on Buttons
    private fun setupClickListener() {

        includeButtonLimit1.buttonView.text = getString(R.string.hint_limit_1)
        includeButtonLimit2.buttonView.text = getString(R.string.hint_limit_2)
        includeButtonBuffer.buttonView.text = getString(R.string.hint_buffer)

        includeButtonBuffer.buttonView.isEnabled = true
        includeButtonBuffer.buttonView.isFocusable = false
        includeButtonBuffer.buttonView.isClickable = false

        includeButtonBuffer.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonBuffer.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)


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

        includeButtonLimit1.buttonView.setOnClickListener {
            setUpLimitOne()
        }

        includeButtonLimit2.buttonView.setOnClickListener {

            if(limitTwoFragment == null ) onAlarmLimitChangeListener?.let {
                limitTwoFragment = LimitTwoFragment(service, it)
            }

            limitTwoFragment?.apply {
                replaceFragment(this,this::class.java.javaClass.simpleName, R.id.alarm_nav_container )
            }



            includeButtonLimit2.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
            includeButtonLimit1.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)

            includeButtonLimit2.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            includeButtonLimit1.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            includeButtonBuffer.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            includeButtonBuffer.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            //setPaddingOnButton()

        }

        includeButtonBuffer.buttonView.setOnClickListener {
            //var fragment:BufferFragment?=null

            bufferFragment=BufferFragment.newInstance()

            //val fragment:BufferFragment?=BufferFragment(ackListFragment)


            if (bufferFragment != null) {
                childFragmentManager.beginTransaction()
                    .replace(
                        R.id.alarm_nav_container,
                        bufferFragment!!,
                        bufferFragment!!::class.java.javaClass.simpleName,
                        )
                    .commit()
            }


            includeButtonLimit1.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonLimit2.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonBuffer.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
            //setPaddingOnButton()
        }
    }

    //ToDo:- Notifying bufferAlarmRecyclerAdapter about the dataset changed from the dashboard activity

     fun notifyBufferRecyclerAdapter(){
        if (bufferFragment?.isVisible==true){
            bufferFragment?.notifyBufferAlarmAdapter()
        }
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        hideSystemUI()
    }

    override fun onStop() {
        super.onStop()
        alarmsDialogFragmentObserver.apply {
            this?.let { this@AlarmDialogFragment.lifecycle.removeObserver(it) }
        }
        alarmsDialogFragmentObserver=null
    }

    inner class AlarmsDialogFragmentObserver:DefaultLifecycleObserver{
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            heightDialog = arguments?.getInt(KEY_HEIGHT)
            widthDialog = arguments?.getInt(KEY_WIDTH)
            setHeightWidthPercent(heightDialog, widthDialog, true)
            if (bundle?.getString("fragment_val")=="BufferFragment"){
                //button background color specification
                includeButtonLimit1.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
                includeButtonLimit2.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
                includeButtonBuffer.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
                //button text color specification
                includeButtonLimit2.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                includeButtonLimit1.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                includeButtonBuffer.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                bufferFragment=BufferFragment.newInstance()

                if (bufferFragment != null) {
                    bufferFragment?.let {
                            childFragmentManager.beginTransaction()
                                .replace(
                                    R.id.alarm_nav_container,
                                    it,
                                    it::class.java.javaClass.simpleName,
                                )
                                .commit()
                        }
                }
            } else if (bundle?.getString("fragment_val")=="FromButton"){
                setUpLimitOne()
            }

        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            limitOneFragment = null
            limitTwoFragment = null
            bufferFragment =null

            heightDialog=null
            widthDialog=null
            bundle=null
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
        }
    }

}