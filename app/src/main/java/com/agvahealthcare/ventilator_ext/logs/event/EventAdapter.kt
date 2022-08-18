package com.agvahealthcare.ventilator_ext.logs.event

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.database.entities.EventDataModel

class EventAdapter( private var dataList: List<EventDataModel>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_data, parent, false)

        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {

        if (!dataList.isEmpty()){
            val data = dataList[position]
            if (position % 2 == 0){
                holder.parentLayout?.setBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                holder.parentLayout?.setBackgroundColor(Color.parseColor("#eeeeee"))

            }
            holder.tvDateTime?.text = data.timeStamp
            holder.tvEventData?.text = data.event

        } else {
            holder.tvEventData?.text="There are no events yet generated"
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    class EventViewHolder (view: View) : RecyclerView.ViewHolder(view){

        var tvDateTime: TextView? = null
        var tvEventData: TextView? = null
        var parentLayout : LinearLayoutCompat? = null
        init {
            tvDateTime = view.findViewById(R.id.textViewDateTime)
            tvEventData = view.findViewById(R.id.textViewEventsData)
            parentLayout = view.findViewById(R.id.layoutid)

        }
    }
}