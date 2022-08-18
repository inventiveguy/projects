package com.agvahealthcare.ventilator_ext.logs.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.database.entities.EventDataModel
import kotlinx.android.synthetic.main.fragment_events.*

class EventsFragment : Fragment() {

    private lateinit var mEventViewModel: EventViewModel
    private var dataList =  emptyList<EventDataModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view =  inflater.inflate(R.layout.fragment_events, container, false)
        mEventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        mEventViewModel.readAllEvents().observe(viewLifecycleOwner, {
            dataList = it
            setDataForEvents()
        })
        return view
    }

    private fun setDataForEvents() {
        var mAdapter:EventAdapter?=null
        if (dataList.isEmpty()){
            tv_emptyError.visibility=View.VISIBLE
        } else {
            if (tv_emptyError.visibility==View.VISIBLE){
                tv_emptyError.visibility=View.GONE
            }
            mAdapter = EventAdapter(dataList)
        }

        recyclerViewEvents?.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isVerticalScrollBarEnabled = true
        }

    }


}
