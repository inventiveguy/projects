
package com.agvahealthcare.ventilator_ext.system.info

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.agvahealthcare.ventilator_ext.MainActivityViewModel
import com.agvahealthcare.ventilator_ext.api.BatteryHealthStaus
import com.agvahealthcare.ventilator_ext.dashboard.DashBoardViewModel
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.service.CommunicationService
import com.agvahealthcare.ventilator_ext.utility.DialogBoxFactory
import com.agvahealthcare.ventilator_ext.R

import kotlinx.android.synthetic.main.content_button_layout.view.*
import kotlinx.android.synthetic.main.fragment_info.*
import java.text.DecimalFormat

class InfoFragment(private var communicationService: CommunicationService?) : Fragment() ,View.OnClickListener{

    private var batteryLevel: Int? = null
    private var batteryHealth: Int? = null
    private var batteryRemainingTime: Int? = null
    private var isBatteryConnected:Boolean?=false

    var prefManager: PreferenceManager? = null
    private var activityViewModel:AndroidViewModel?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(context)
        if (tag=="MainActivity"){
            activityViewModel=ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
            (activityViewModel as MainActivityViewModel).ventBatteryRemainingTime.observe(viewLifecycleOwner, Observer { it->
                setBatteryTTEUpdate(it)
            })
            (activityViewModel as MainActivityViewModel).ventBatteryHealth.observe(viewLifecycleOwner, Observer { it->
                setBatteryHealthUpdate(it)
            })
            (activityViewModel as MainActivityViewModel).ventBatteryLevel.observe(viewLifecycleOwner, Observer { it->
                setBatteryLevelUpdate(it)
            })
            (activityViewModel as MainActivityViewModel).isBatteryConnected.observe(viewLifecycleOwner, Observer { it->
                isBatteryConnected=it
                //point where the value  of battery status has to be resend
                if (it==false){setBatteryTTEUpdate(-1)}
            })
        }else if (tag=="DashBoardActivity"){
            activityViewModel=ViewModelProvider(requireActivity()).get(DashBoardViewModel::class.java)
            (activityViewModel as DashBoardViewModel).ventBatteryRemainingTime.observe(viewLifecycleOwner, Observer { it->
                setBatteryTTEUpdate(it)
            })
            (activityViewModel as DashBoardViewModel).ventBatteryHealth.observe(viewLifecycleOwner, Observer { it->
                setBatteryHealthUpdate(it)
            })
            (activityViewModel as DashBoardViewModel).ventBatteryLevel.observe(viewLifecycleOwner, Observer { it->
                setBatteryLevelUpdate(it)
            })
            (activityViewModel as DashBoardViewModel).isBatteryConnected.observe(viewLifecycleOwner, Observer { it->
                isBatteryConnected=it
                //point where the value  of battery status has to be resend
                if (it==false){setBatteryTTEUpdate(-1)}
            })
        }

        prefManager?.apply {
            try {
                setSoftWareUpdate(readVentilatorSoftwareVersion())
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "" + e.printStackTrace(), Toast.LENGTH_LONG).show()
            }

        }

        setOnClickListener()

        textViewVersionData.setOnClickListener {

            DialogBoxFactory.showCommandDialog(requireContext()) { command ->
                sendRawCommandToVentilator(
                    java.lang.String.valueOf(
                        command
                    )
                )
            }
        }

        try {
            val pInfo =
                requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val version = pInfo.versionName
            Log.i("version name", version + "  " + pInfo.versionCode)
            textViewVersionData.text = version
            textViewModelData.text = "AgVa Pro".uppercase()
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(requireContext(), "" + e.printStackTrace(), Toast.LENGTH_LONG).show()
        }

    }



    private fun sendRawCommandToVentilator(command: String?) {
        communicationService?.takeIf { it.isPortsConnected }?.apply {
            command?.takeIf { it.isNotEmpty() }?.apply {
                send(this)
            }
        }
    }

    // ClickListener on Buttons

    // TODO : WRONG - WRITE IN DASHBOARD ACTIVITY
    private fun setBatteryLevelUpdate(btryLevel: Int) {
        if (btryLevel < 0 && btryLevel > 100){
            textViewBattery1Data.text = "-"
        } else {
            textViewBattery1Data.text = "$btryLevel %"
        }
        this.batteryLevel = btryLevel
    }



    private  fun setBatteryHealthUpdate(health: Int) {
        when (health) {
            in 0..50 -> {
                textViewBatteryHealthData.text = "${BatteryHealthStaus.Bad}"
                textViewBatteryHealthData.setTextColor(Color.RED)
            }
            in 51..70 -> {
                textViewBatteryHealthData.text = "${BatteryHealthStaus.Marginal}"
                textViewBatteryHealthData.setTextColor(Color.YELLOW)
            }
            in 71..85 -> {
                textViewBatteryHealthData.text = "${BatteryHealthStaus.Good}"
                textViewBatteryHealthData.setTextColor(resources.getColor(R.color.racing_green,null))
            }
            in 86..100 -> {
                textViewBatteryHealthData.text = "${BatteryHealthStaus.Excellent}"
                textViewBatteryHealthData.setTextColor(resources.getColor(R.color.racing_green,null))
            }
            !in 0..100-> {
                textViewBatteryHealthData.text = "-"
                textViewBatteryHealthData.setTextColor(Color.BLACK)
            }
            else -> {
                textViewBatteryHealthData.text = "-"
                textViewBatteryHealthData.setTextColor(Color.BLACK)
            }
        }

        this.batteryHealth = health
    }

    private fun  roundMinutes(value:Int):Int{
        val returnmod=value%5
        return (value-returnmod)
    }

    private fun setBatteryTTEUpdate(timeInMins: Int) {
        if(timeInMins < 0 || isBatteryConnected==false){
            textViewBatteryRemTimeData.text = "-"
        }  else {
            var formatter=DecimalFormat("00")
            val hour = timeInMins / 60
            var mins = timeInMins % 60
            mins= roundMinutes(mins)
            textViewBatteryRemTimeData.text = if (hour==0) "${formatter.format(mins)} m" else "$hour h ${formatter.format(mins)} m"
        }
        this.batteryRemainingTime = timeInMins
    }

    fun setSoftWareUpdate(softwareUpdate: String?) {
        softwareUpdate?.apply {
            textViewOperatingHoursData.text = this
        }
    }

    override fun onClick(p0: View?) {
        when(p0){
            includeButtonShutDown.buttonView ->{
            }

        }
    }

    private fun shutDownBtnAppearance(btnView:Button){
        btnView.text = getString(R.string.hint_shutdown)
        btnView.setTextColor(getResources().getColor(R.color.white,null))
        btnView.setBackgroundResource(R.drawable.background_primary_btn_rounded)
        btnView.setPadding(50, 0, 50, 0)
    }
    private fun setOnClickListener() {
        shutDownBtnAppearance(includeButtonShutDown.buttonView)
        includeButtonShutDown.buttonView.setOnClickListener(this)
    }
}