package com.agvahealthcare.ventilator_ext.logs.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.database.entities.AlarmDBModel
import kotlinx.android.synthetic.main.fragment_logs_alarm.*

class AlarmFragment : Fragment() {
    private lateinit var mAlarmViewModel: AlarmViewModel
    private var dataList =  emptyList<AlarmDBModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_logs_alarm, container, false)
        mAlarmViewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)

        mAlarmViewModel.readAllAlarms().observe(viewLifecycleOwner, {
            dataList = it
            setUpAlarmsData()
        })
        return  view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAlarmsData()
    }

    private fun setUpAlarmsData() {
        var mAdapter: AlarmAdapter?=null
        if (dataList.isEmpty()){
            tv_emptyAlarm.visibility=View.VISIBLE
        } else {
            if (tv_emptyAlarm.visibility==View.VISIBLE){
                tv_emptyAlarm.visibility=View.GONE
            }
            mAdapter = AlarmAdapter(dataList)
        }
        recyclerViewAlarm?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }
}
