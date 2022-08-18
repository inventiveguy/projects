package com.agvahealthcare.ventilator_ext.logs.trends.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.model.DataStoreModel


object DataDifferntiatorOther : DiffUtil.ItemCallback<DataStoreModel>() {

    override fun areItemsTheSame(oldItem: DataStoreModel, newItem: DataStoreModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataStoreModel, newItem: DataStoreModel): Boolean {
        return false
    }
}
class PagingAAdapter(val ctx:Context) :PagingDataAdapter<DataStoreModel,PagingAAdapter.MyViewHolder>(DataDifferntiatorOther){

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currentItems=getItem(position)
        val splited=currentItems?.time?.split(" ")
        if (currentItems?.id!! %9==0 || position==0){
            var dateString= splited?.get(0)?.split("-")
            holder.tv_date?.text=dateString?.get(2)+"-"+dateString?.get(1)+"-"+dateString?.get(0)
            holder.tv_date?.visibility=View.VISIBLE
            //separatorLineHightLight(holder)
        } else {
            //separatorLineDeHightLight(holder)
        }

        holder.tv_time?.text = splited!![1]?.split(":")[0]+ " : "+ splited[1]?.split(":")[1]
        holder.tv_pip?.text = String.format("%.1f",currentItems.peep)
        holder.tv_sp?.text =  String.format("%.1f",currentItems.pressure)
        holder.tv_peep?.text = String.format("%.1f",currentItems.peep)
        holder.tv_ma?.text = String.format("%.1f",currentItems.meanAirwayPressure)
        holder.tv_vti?.text = String.format("%.1f",currentItems.vte)
        holder.tv_vte?.text = String.format("%.1f",currentItems.vte)
        holder.tv_mve?.text = String.format("%.1f",currentItems.mve)
        holder.tv_mvi?.text = String.format("%.1f",currentItems.mvi)
        holder.tv_fio?.text = String.format("%.1f",currentItems.fiO2)
        holder.tv_rr?.text = String.format("%.1f",currentItems.rr)
        //IE ratio
        holder.tv_ie?.text = "1:"+String.format("%.2f",currentItems.ieRatio)
        //tinsp
        holder.tv_tinsp?.text = String.format("%.2f",currentItems.tinsp)
        //texp
        holder.tv_texp?.text = String.format("%.2f",currentItems.texp)
        holder.tv_leak?.text = String.format("%.1f",currentItems.leak)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.parameter_units_itemtwo,parent,false)
        return PagingAAdapter.MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    class MyViewHolder(view:View) : RecyclerView.ViewHolder(view){
        var tv_date: TextView?=null
        var tv_time : TextView?= null
        var tv_pip : TextView?= null
        var tv_sp : TextView?= null
        var tv_peep : TextView?= null
        var tv_ma : TextView?= null
        var tv_vti : TextView?= null
        var tv_vte : TextView?= null
        var tv_mve : TextView?= null
        var tv_mvi : TextView?= null
        var tv_fio : TextView?= null
        var tv_rr : TextView?= null
        var tv_ie : TextView?= null
        var tv_tinsp : TextView?= null
        var tv_texp : TextView?= null
        var tv_leak : TextView?= null

        var linezero: View?=null
        var lineone: View?=null
        var linetwo: View?=null
        var linethree: View?=null
        var linefour: View?=null
        var linefive: View?=null
        var linesix: View?=null
        var lineseven: View?=null
        var lineeight: View?=null
        var linenine: View?=null
        var lineten: View?=null
        var lineeleven: View?=null
        var linetwelve: View?=null
        var linethirteen: View?=null
        var linefourteen: View?=null
        var linefifteen: View?=null
        init {
            tv_date = view.findViewById(R.id.tv_date)
            tv_time = view.findViewById(R.id.tv_time)
            tv_pip = view.findViewById(R.id.tv_pip)
            tv_sp = view.findViewById(R.id.tv_sp)
            tv_peep = view.findViewById(R.id.tv_peep)
            tv_ma = view.findViewById(R.id.tv_ma)
            tv_vti = view.findViewById(R.id.tv_vti)
            tv_vte = view.findViewById(R.id.tv_vte)
            tv_mve = view.findViewById(R.id.tv_mve)
            tv_mvi = view.findViewById(R.id.tv_mvi)
            tv_fio = view.findViewById(R.id.tv_fio)
            tv_rr = view.findViewById(R.id.tv_rr)
            tv_ie = view.findViewById(R.id.tv_ie)
            tv_tinsp = view.findViewById(R.id.tv_tinsp)
            tv_texp = view.findViewById(R.id.tv_texp)
            tv_leak = view.findViewById(R.id.tv_leak)

            //the side line separator views
            linezero = view.findViewById(R.id.lineViewzero)
            lineone = view.findViewById(R.id.lineViewone)
            linetwo = view.findViewById(R.id.lineViewtwo)
            linethree = view.findViewById(R.id.lineViewThree)
            linefour = view.findViewById(R.id.lineViewFour)
            linefive = view.findViewById(R.id.lineViewFive)
            linesix = view.findViewById(R.id.lineViewSix)
            lineseven = view.findViewById(R.id.lineViewSeven)
            lineeight = view.findViewById(R.id.lineViewEight)
            linenine = view.findViewById(R.id.lineViewNine)
            lineten = view.findViewById(R.id.lineViewTen)
            lineeleven = view.findViewById(R.id.lineViewEleven)
            linetwelve = view.findViewById(R.id.lineViewTwelve)
            linethirteen = view.findViewById(R.id.lineViewThirteen)
            linefourteen = view.findViewById(R.id.lineViewFourteen)
            linefifteen = view.findViewById(R.id.lineViewFifteen)
        }
    }
}