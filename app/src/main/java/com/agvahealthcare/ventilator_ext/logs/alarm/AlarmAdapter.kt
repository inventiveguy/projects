package com.agvahealthcare.ventilator_ext.logs.alarm

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.paging.Config
import androidx.recyclerview.widget.RecyclerView
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.database.entities.AlarmDBModel
import com.agvahealthcare.ventilator_ext.utility.utils.AlarmConfiguration
import com.agvahealthcare.ventilator_ext.utility.utils.Configs

class AlarmAdapter(private var dataList: List<AlarmDBModel>) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm_data, parent, false)

        return AlarmViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val data = dataList[position]
        if (position % 2 == 0){
            holder.layoutId?.setBackgroundColor(Color.parseColor("#eeeeee"))
        } else {
            holder.layoutId?.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        holder.tvAlarmType?.text = data.message
        holder.tvStartAlarmDate?.text = data.createdAt
        //holder.tvEndAlarmDate?.text = data.ackcode
        if (data.key != "0"){
            val ackPriority = AlarmConfiguration.getPriority(data.key)
            when(ackPriority){
                Configs.AlarmType.ALARM_HIGH_LEVEL -> {
                    holder.tvEndAlarmDate?.text="High"
                    holder.tvEndAlarmDate?.setTextColor(Color.parseColor("#E57373"));
                }
                Configs.AlarmType.ALARM_MEDIUM_LEVEL -> {
                    holder.tvEndAlarmDate?.text="Medium"
                    holder.tvEndAlarmDate?.setTextColor(Color.parseColor("#FBC02D"));
                }
                Configs.AlarmType.ALARM_LOW_LEVEL -> {
                    holder.tvEndAlarmDate?.text="Low"
                    holder.tvEndAlarmDate?.setTextColor(Color.parseColor("#9E9E9E"));
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return  dataList.size
    }

    class AlarmViewHolder (view: View) : RecyclerView.ViewHolder(view){
        var tvAlarmType: TextView? = null
        var tvStartAlarmDate: TextView? = null
        var tvEndAlarmDate: TextView? = null
        var layoutId: LinearLayoutCompat?= null
        init {
            tvAlarmType = view.findViewById(R.id.textViewAlarmType)
            tvStartAlarmDate = view.findViewById(R.id.textStartAlarmDate)
            tvEndAlarmDate = view.findViewById(R.id.textEndAlarmDate)
            layoutId = view.findViewById(R.id.layoutid)
        }

    }
}