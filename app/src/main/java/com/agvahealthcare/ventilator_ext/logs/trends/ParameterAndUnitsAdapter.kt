package com.agvahealthcare.ventilator_ext.logs.trends

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agvahealthcare.ventilator_ext.R

class ParameterAndUnitsAdapter : RecyclerView.Adapter<ParameterAndUnitsAdapter.PUViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PUViewHolder {
       val itemView = LayoutInflater.from(parent.context).inflate(R.layout.parameter_units_item,parent,false)
        return PUViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PUViewHolder, position: Int) {
        when(position){
            0->{
                holder.tvParameter?.visibility=View.INVISIBLE
                holder.tvUnits?.visibility=View.INVISIBLE
                holder.viewLine?.visibility=View.GONE
                holder.viewLineTwo?.visibility=View.GONE
            }
            1->{
                holder.tvParameter?.setText("Parameter")
                holder.tvUnits?.setText("Unit")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#4E4E4E"))
                holder.tvUnits?.setTextColor(Color.parseColor("#FFFFFF"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#4E4E4E"))
                holder.tvParameter?.setTextColor(Color.parseColor("#FFFFFF"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
            2->{
                holder.tvParameter?.setText("PIP")
                holder.tvUnits?.setText("cmH20")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
            3->{
                holder.tvParameter?.setText("Support Pressure")
                holder.tvUnits?.setText("cmH20")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
        }
            4->{
                holder.tvParameter?.setText("PEEP")
                holder.tvUnits?.setText("cmH20")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
            5->{
                holder.tvParameter?.setText("Mean Airway")
                holder.tvUnits?.setText("cmH20")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
            6->{
                holder.tvParameter?.setText("Vti")
                holder.tvUnits?.setText("ml")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
            7->{
                holder.tvParameter?.setText("Vte")
                holder.tvUnits?.setText("ml")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
            8->{
                holder.tvParameter?.setText("MVe")
                holder.tvUnits?.setText("Litre")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
            9->{
                holder.tvParameter?.setText("MVi")
                holder.tvUnits?.setText("Litre")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
            10->{
                holder.tvParameter?.setText("FiO2")
                holder.tvUnits?.setText("%")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
            11->{
                holder.tvParameter?.setText("Respiratory Rate")
                holder.tvUnits?.setText("BPM")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
            12->{
                holder.tvParameter?.setText("I:E")
                holder.tvUnits?.setText("Ratio")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
            13->{
                holder.tvParameter?.setText("Tinsp")
                holder.tvUnits?.setText("sec")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
            14->{
                holder.tvParameter?.setText("Texp")
                holder.tvUnits?.setText("sec")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
            15->{
                holder.tvParameter?.setText("Average Leak")
                holder.tvUnits?.setText("%")
                holder.tvUnits?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvUnits?.setTextColor(Color.parseColor("#000000"))
                holder.tvParameter?.setBackgroundColor(Color.parseColor("#1A000000"))
                holder.tvParameter?.setTextColor(Color.parseColor("#000000"))
                holder.viewLine?.setBackgroundColor(Color.parseColor("#000000"))
                holder.viewLineTwo?.setBackgroundColor(Color.parseColor("#000000"))
            }
        }
    }

    override fun getItemCount(): Int {
        return 16
    }

    class PUViewHolder (view : View) : RecyclerView.ViewHolder(view){
             var tvParameter: TextView?=null
             var tvUnits: TextView?=null
             var viewLine: View?=null
             var viewLineTwo:View?=null
        init {
            tvParameter=view.findViewById(R.id.tv_parameter)
            tvUnits=view.findViewById(R.id.tv_units)
            viewLine=view.findViewById(R.id.lineView)
            viewLineTwo=view.findViewById(R.id.lineViewtwo)
        }
    }




}