package com.agvahealthcare.ventilator_ext.logs.trends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agvahealthcare.ventilator_ext.R

class DateAdapter : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.parameter_units_date,parent,false)
        return DateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        when(position){
            0->{
                holder.tvDate?.setText("22-7-2022")
            }
            1->{
                holder.tvDate?.setText("21-7-2022")
            }
            2->{
                holder.tvDate?.setText("20-7-2022")
            }
            3->{
                holder.tvDate?.setText("19-7-2022")
            }
            4->{
                holder.tvDate?.setText("18-7-2022")
            }
            5->{
                holder.tvDate?.setText("17-7-2022")
            }
            6->{
                holder.tvDate?.setText("16-7-2022")
            }
        }
    }

    override fun getItemCount(): Int {
        return 7
    }

    class DateViewHolder (view: View) : RecyclerView.ViewHolder(view){
        var tvDate: TextView?=null
        init {
            tvDate=view.findViewById(R.id.tv_date)
        }
    }
}