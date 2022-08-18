
package com.agvahealthcare.ventilator_ext.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.agvahealthcare.ventilator_ext.callback.OnDismissDialogListener
import com.agvahealthcare.ventilator_ext.logs.alarm.AlarmFragment
import com.agvahealthcare.ventilator_ext.logs.event.EventsFragment
import com.agvahealthcare.ventilator_ext.logs.trends.LogsTrendsFragment
import com.agvahealthcare.ventilator_ext.logs.trends.LogsTableFragment
import com.agvahealthcare.ventilator_ext.utility.replaceFragment
import com.agvahealthcare.ventilator_ext.utility.setHeightWidthPercent
import com.agvahealthcare.ventilator_ext.R
import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_logs_dialog.*
import kotlinx.android.synthetic.main.fragment_logs_table_demo.*

class LogsDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "LogsDialog"
        private const val KEY_HEIGHT = "KEY_HEIGHT"
        private const val KEY_WIDTH = "KEY_WIDTH"

        fun newInstance(height: Int?, width: Int? ,closeListener : OnDismissDialogListener?): LogsDialogFragment {
            val args = Bundle()
            height?.let { args.putInt(KEY_HEIGHT, it) }
            width?.let { args.putInt(KEY_WIDTH, it) }
            val fragment = LogsDialogFragment()
            fragment.arguments = args
            fragment.closeListener = closeListener
            return fragment
        }
    }
    private var closeListener: OnDismissDialogListener? = null
    private var eventsFragment : EventsFragment? = null
    private var alarmFragment : AlarmFragment? = null
    private var trendsFragment : LogsTableFragment? = null
    private var trendsOtherFragment : LogsTrendsFragment? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_logs_dialog, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
        setUpTrends()
        setupClickListener()
    }

    private fun setPaddingOnButton() {

        includeButtonTrends.buttonView.setPadding(35,10,35,10)
        includeButtonEvents.buttonView.setPadding(35,10,35,10)
        includeButtonAlarms.buttonView.setPadding(35,10,35,10)

    }

    // ClickListener on Buttons
    private fun setupClickListener() {

        includeButtonTrends.buttonView.text = getString(R.string.hint_trends)
        includeButtonEvents.buttonView.text = getString(R.string.hint_events)
        includeButtonAlarms.buttonView.text = getString(R.string.hint_alarms)


        imageViewCross.setOnClickListener {


            requireActivity().supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commitNow()
//            requireActivity().supportFragmentManager.popBackStack()

            closeListener?.handleDialogClose()

          /*  closeListener?.handleDialogClose()
            dismiss()*/
        }
        //ToDo:- paging functionality
        includeButtonTrends.buttonView.setOnClickListener {
            setUpTrends()
        }

        includeButtonEvents.buttonView.setOnClickListener {
            alarmFragment = null
            trendsFragment = null
            eventsFragment = EventsFragment()
            eventsFragment?.apply {
                replaceFragment(this,this::class.java.javaClass.simpleName, R.id.logs_nav_container )
            }

            includeButtonTrends.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonEvents.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
            includeButtonAlarms.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonEvents.buttonView.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
             includeButtonTrends.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
             includeButtonAlarms.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            setPaddingOnButton()

        }

        includeButtonAlarms.buttonView.setOnClickListener {
            trendsFragment = null
            eventsFragment = null
            alarmFragment = AlarmFragment()
            alarmFragment?.apply {
                replaceFragment(this,this::class.java.javaClass.simpleName, R.id.logs_nav_container )
            }
            includeButtonTrends.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonEvents.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
            includeButtonAlarms.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
            includeButtonAlarms.buttonView.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            includeButtonTrends.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            includeButtonEvents.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        }
    }

    private fun setUpTrends() {
        alarmFragment = null
        eventsFragment = null
        trendsOtherFragment = LogsTrendsFragment()
        trendsOtherFragment?.apply {
            replaceFragment(this,this::class.java.javaClass.simpleName,R.id.logs_nav_container)
        }
        /*trendsFragment = LogsTableFragment()
        trendsFragment?.apply {
            replaceFragment(this,this::class.java.javaClass.simpleName, R.id.logs_nav_container)
        }*/

        includeButtonTrends.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded_selected_green)
        includeButtonEvents.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonAlarms.buttonView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        includeButtonTrends.buttonView.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
        includeButtonEvents.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        includeButtonAlarms.buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        setPaddingOnButton()
    }

    override fun onStart() {
        super.onStart()
        val heightDialog = arguments?.getInt(KEY_HEIGHT)
        val widthDialog = arguments?.getInt(KEY_WIDTH)

        setHeightWidthPercent(heightDialog , widthDialog , true)

    }


    fun scrollTrendsAt(offset: Int) = trendsOtherFragment?.takeIf { isVisible }?.scrollAt(offset)
    fun scrollTrendsForward() = trendsOtherFragment?.takeIf { isVisible }?.scrollForward()
    fun scrollTrendsBack() = trendsOtherFragment?.takeIf { isVisible }?.scrollBack()


}