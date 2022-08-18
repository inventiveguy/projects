package com.agvahealthcare.ventilator_ext.logs.trends

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.model.DataStoreModel
import java.text.SimpleDateFormat
import java.util.*

class DataFromDataBaseAdapter(ctx:Context, dataList:ArrayList<DataStoreModel>) : RecyclerView.Adapter<DataFromDataBaseAdapter.DFDViewHolder>() {
    var context:Context = ctx
    var list:ArrayList<DataStoreModel> =  dataList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DFDViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.parameter_units_itemtwo,parent,false)
        return DFDViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DFDViewHolder, position: Int) {
        val dataStore:DataStoreModel=list.get(position)
        val splited=dataStore.time.split(" ")
        val simpleDateFormat=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val start= simpleDateFormat.parse(dataStore.time)?.date
        if (position==0){
            holder.tv_date?.text  = splited[0].split("-")[2] + "-" + splited[0].split("-")[1] +"-"+splited[0].split("-")[0]
            holder.tv_date?.visibility=View.VISIBLE
            separatorLineDeHightLight(holder)
        } else if (position<list.size-1){
            val end= simpleDateFormat.parse(list.get(position+1).time)?.date
            if (start != end){
                holder.tv_date?.text = splited[0].split("-")[2] + "-" + splited[0].split("-")[1] +"-"+splited[0].split("-")[0]
                holder.tv_date?.visibility=View.VISIBLE
                separatorLineHightLight(holder)
            } else {
                val previous = simpleDateFormat.parse(list.get(position-1).time)?.date
                if (previous != start) {
                    holder.tv_date?.text = splited[0].split("-")[2] + "-" + splited[0].split("-")[1] +"-"+splited[0].split("-")[0]
                    holder.tv_date?.visibility=View.VISIBLE
                } else {
                    holder.tv_date?.visibility=View.INVISIBLE
                    separatorLineDeHightLight(holder)
                }
            }
        }

        holder.tv_time?.text = splited[1].split(":")[0]+ " : "+ splited[1].split(":")[1]
        holder.tv_pip?.text = String.format("%.1f",dataStore.peep)
        holder.tv_sp?.text =  String.format("%.1f",dataStore.pressure)
        holder.tv_peep?.text = String.format("%.1f",dataStore.peep)
        holder.tv_ma?.text = String.format("%.1f",dataStore.meanAirwayPressure)
        holder.tv_vti?.text = String.format("%.1f",dataStore.vte)
        holder.tv_vte?.text = String.format("%.1f",dataStore.vte)
        holder.tv_mve?.text = String.format("%.1f",dataStore.mve)
        holder.tv_mvi?.text = String.format("%.1f",dataStore.mvi)
        holder.tv_fio?.text = String.format("%.1f",dataStore.fiO2)
        holder.tv_rr?.text = String.format("%.1f",dataStore.rr)
        //IE ratio
        holder.tv_ie?.text = "1 : "+String.format("%.1f",dataStore.ieRatio)
        //tinsp
        holder.tv_tinsp?.text = String.format("%.1f",dataStore.tinsp)
        //texp
        holder.tv_texp?.text = String.format("%.1f",dataStore.texp)
        holder.tv_leak?.text = String.format("%.1f",dataStore.leak)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun separatorLineHightLight(holder: DFDViewHolder){
        holder.linezero?.setBackgroundColor(R.color.racing_green)
        holder.lineone?.setBackgroundColor(R.color.racing_green)
        holder.linetwo?.setBackgroundColor(R.color.racing_green)
        holder.linethree?.setBackgroundColor(R.color.racing_green)
        holder.linefour?.setBackgroundColor(R.color.racing_green)
        holder.linefive?.setBackgroundColor(R.color.racing_green)
        holder.linesix?.setBackgroundColor(R.color.racing_green)
        holder.lineseven?.setBackgroundColor(R.color.racing_green)
        holder.lineeight?.setBackgroundColor(R.color.racing_green)
        holder.linenine?.setBackgroundColor(R.color.racing_green)
        holder.lineten?.setBackgroundColor(R.color.racing_green)
        holder.lineeleven?.setBackgroundColor(R.color.racing_green)
        holder.linetwelve?.setBackgroundColor(R.color.racing_green)
        holder.linethirteen?.setBackgroundColor(R.color.racing_green)
        holder.linefourteen?.setBackgroundColor(R.color.racing_green)
        holder.linefifteen?.setBackgroundColor(R.color.racing_green)
    }
    fun separatorLineDeHightLight(holder: DFDViewHolder){
        holder.linezero?.visibility=View.INVISIBLE
        holder.lineone?.setBackgroundColor(Color.parseColor("#FFFFFF"))
        holder.linetwo?.setBackgroundColor(Color.parseColor("#000000"))
        holder.linethree?.setBackgroundColor(Color.parseColor("#000000"))
        holder.linefour?.setBackgroundColor(Color.parseColor("#000000"))
        holder.linefive?.setBackgroundColor(Color.parseColor("#000000"))
        holder.linesix?.setBackgroundColor(Color.parseColor("#000000"))
        holder.lineseven?.setBackgroundColor(Color.parseColor("#000000"))
        holder.lineeight?.setBackgroundColor(Color.parseColor("#000000"))
        holder.linenine?.setBackgroundColor(Color.parseColor("#000000"))
        holder.lineten?.setBackgroundColor(Color.parseColor("#000000"))
        holder.lineeleven?.setBackgroundColor(Color.parseColor("#000000"))
        holder.linetwelve?.setBackgroundColor(Color.parseColor("#000000"))
        holder.linethirteen?.setBackgroundColor(Color.parseColor("#000000"))
        holder.linefourteen?.setBackgroundColor(Color.parseColor("#000000"))
        holder.linefifteen?.setBackgroundColor(Color.parseColor("#000000"))
    }

    class DFDViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var tv_date:TextView?=null
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
            tv_date=view.findViewById(R.id.tv_date)
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
             linezero =view.findViewById(R.id.lineViewzero)
             lineone=view.findViewById(R.id.lineViewone)
             linetwo =view.findViewById(R.id.lineViewtwo)
             linethree =view.findViewById(R.id.lineViewThree)
             linefour  =view.findViewById(R.id.lineViewFour)
             linefive  =view.findViewById(R.id.lineViewFive)
             linesix =view.findViewById(R.id.lineViewSix)
             lineseven =view.findViewById(R.id.lineViewSeven)
             lineeight =view.findViewById(R.id.lineViewEight)
             linenine =view.findViewById(R.id.lineViewNine)
             lineten=view.findViewById(R.id.lineViewTen)
             lineeleven =view.findViewById(R.id.lineViewEleven)
             linetwelve =view.findViewById(R.id.lineViewTwelve)
             linethirteen =view.findViewById(R.id.lineViewThirteen)
             linefourteen=view.findViewById(R.id.lineViewFourteen)
             linefifteen=view.findViewById(R.id.lineViewFifteen)
        }
    }
}
