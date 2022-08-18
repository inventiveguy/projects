package com.agvahealthcare.ventilator_ext.logs.trends.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.logs.trends.DataFromDataBaseAdapter
import com.agvahealthcare.ventilator_ext.model.DataStoreModel

object DataDifferntiator : DiffUtil.ItemCallback<ArrayList<DataStoreModel>>() {

    override fun areItemsTheSame(oldItem: ArrayList<DataStoreModel>, newItem: ArrayList<DataStoreModel>): Boolean {
        return oldItem[0].id == newItem[0].id
    }

    override fun areContentsTheSame(oldItem: ArrayList<DataStoreModel>, newItem: ArrayList<DataStoreModel>): Boolean {
        return oldItem == newItem
    }
}
class TrendsPagingAdapter(val ctx:Context) : PagingDataAdapter<ArrayList<DataStoreModel>, TrendsPagingAdapter.TPViewHolder>(DataDifferntiator)  {
    private val context=ctx
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TPViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.parameter_units_unnesteditem,parent,false)
        return TPViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TPViewHolder, position: Int) {
        var currentItem= getItem(position)
        var dataFromDataBaseAdapter= currentItem?.let { DataFromDataBaseAdapter(context, it) }
        dataFromDataBaseAdapter?.setHasStableIds(true);
        holder.rvUnNested.adapter=dataFromDataBaseAdapter

        /*   val splited=currentItem?.get(0)?.time?.split(" ")

           holder.columnonetv_date?.text=currentItem?.get(0)?.time
           holder.columnonetv_time?.text= currentItem?.get(0)?.time
           holder.columnonetv_pip?.text=currentItem?.get(0)?.peep.toString()
           holder.columnonetv_sp?.text=currentItem?.get(0)?.pressure.toString()
           holder.columnonetv_peep?.text=currentItem?.get(0)?.peep.toString()
           holder.columnonetv_ma?.text=currentItem?.get(0)?.meanAirwayPressure.toString()
           holder.columnonetv_vti?.text=currentItem?.get(0)?.vte.toString()
           holder.columnonetv_vte?.text=currentItem?.get(0)?.vte.toString()
           holder.columnonetv_mve?.text=currentItem?.get(0)?.mve.toString()
           holder.columnonetv_mvi?.text=currentItem?.get(0)?.mvi.toString()
           holder.columnonetv_fio?.text=currentItem?.get(0)?.fiO2.toString()
           holder.columnonetv_rr?.text=currentItem?.get(0)?.rr.toString()
           holder.columnonetv_ie?.text=currentItem?.get(0)?.ieRatio.toString()
           holder.columnonetv_tinsp?.text=currentItem?.get(0)?.tinsp.toString()
           holder.columnonetv_texp?.text=currentItem?.get(0)?.texp.toString()
           holder.columnonetv_leak?.text=currentItem?.get(0)?.leak.toString()


           holder.columntwotv_date?.text=currentItem?.get(0)?.time
           holder.columntwotv_time?.text=currentItem?.get(0)?.time
           holder.columntwotv_pip?.text=currentItem?.get(0)?.peep.toString()
           holder.columntwotv_sp?.text=currentItem?.get(0)?.pressure.toString()
           holder.columntwotv_peep?.text=currentItem?.get(0)?.peep.toString()
           holder.columntwotv_ma?.text=currentItem?.get(0)?.meanAirwayPressure.toString()
           holder.columntwotv_vti?.text=currentItem?.get(0)?.vte.toString()
           holder.columntwotv_vte?.text=currentItem?.get(0)?.vte.toString()
           holder.columntwotv_mve?.text=currentItem?.get(0)?.mve.toString()
           holder.columntwotv_mvi?.text=currentItem?.get(0)?.mvi.toString()
           holder.columntwotv_fio?.text=currentItem?.get(0)?.fiO2.toString()
           holder.columntwotv_rr?.text=currentItem?.get(0)?.rr.toString()
           holder.columntwotv_ie?.text=currentItem?.get(0)?.ieRatio.toString()
           holder.columntwotv_tinsp?.text=currentItem?.get(0)?.tinsp.toString()
           holder.columntwotv_texp?.text=currentItem?.get(0)?.texp.toString()
           holder.columntwotv_leak?.text=currentItem?.get(0)?.leak.toString()

           holder.columnthreetv_date?.text=currentItem?.get(0)?.time
           holder.columnthreetv_time?.text=currentItem?.get(0)?.time
           holder.columnthreetv_pip?.text=currentItem?.get(0)?.peep.toString()
           holder.columnthreetv_sp?.text=currentItem?.get(0)?.pressure.toString()
           holder.columnthreetv_peep?.text=currentItem?.get(0)?.peep.toString()
           holder.columnthreetv_ma?.text=currentItem?.get(0)?.meanAirwayPressure.toString()
           holder.columnthreetv_vti?.text=currentItem?.get(0)?.vte.toString()
           holder.columnthreetv_vte?.text=currentItem?.get(0)?.vte.toString()
           holder.columnthreetv_mve?.text=currentItem?.get(0)?.mve.toString()
           holder.columnthreetv_mvi?.text=currentItem?.get(0)?.mvi.toString()
           holder.columnthreetv_fio?.text=currentItem?.get(0)?.fiO2.toString()
           holder.columnthreetv_rr?.text=currentItem?.get(0)?.rr.toString()
           holder.columnthreetv_ie?.text=currentItem?.get(0)?.ieRatio.toString()
           holder.columnthreetv_tinsp?.text=currentItem?.get(0)?.tinsp.toString()
           holder.columnthreetv_texp?.text=currentItem?.get(0)?.texp.toString()
           holder.columnthreetv_leak?.text=currentItem?.get(0)?.leak.toString()


           holder.columnfourtv_date?.text=currentItem?.get(0)?.time
           holder.columnfourtv_time?.text=currentItem?.get(0)?.time
           holder.columnfourtv_pip?.text=currentItem?.get(0)?.peep.toString()
           holder.columnfourtv_sp?.text=currentItem?.get(0)?.pressure.toString()
           holder.columnfourtv_peep?.text=currentItem?.get(0)?.peep.toString()
           holder.columnfourtv_ma?.text =currentItem?.get(0)?.meanAirwayPressure.toString()
           holder.columnfourtv_vti?.text=currentItem?.get(0)?.vte.toString()
           holder.columnfourtv_vte?.text=currentItem?.get(0)?.vte.toString()
           holder.columnfourtv_mve?.text=currentItem?.get(0)?.mve.toString()
           holder.columnfourtv_mvi?.text=currentItem?.get(0)?.mvi.toString()
           holder.columnfourtv_rr?.text=currentItem?.get(0)?.fiO2.toString()
           holder.columnfourtv_rr?.text=currentItem?.get(0)?.rr.toString()
           holder.columnfourtv_ie?.text=currentItem?.get(0)?.ieRatio.toString()
           holder.columnfourtv_tinsp?.text=currentItem?.get(0)?.tinsp.toString()
           holder.columnfourtv_texp?.text=currentItem?.get(0)?.texp.toString()
           holder.columnfourtv_leak?.text=currentItem?.get(0)?.leak.toString()


           holder.columnfivetv_date?.text=currentItem?.get(0)?.time
           holder.columnfivetv_time?.text=currentItem?.get(0)?.time
           holder.columnfivetv_pip?.text=currentItem?.get(0)?.peep.toString()
           holder.columnfivetv_sp?.text=currentItem?.get(0)?.pressure.toString()
           holder.columnfivetv_peep?.text=currentItem?.get(0)?.peep.toString()
           holder.columnfivetv_ma?.text=currentItem?.get(0)?.meanAirwayPressure.toString()
           holder.columnfivetv_vti?.text=currentItem?.get(0)?.vte.toString()
           holder.columnfivetv_vte?.text=currentItem?.get(0)?.vte.toString()
           holder.columnfivetv_mve?.text=currentItem?.get(0)?.mve.toString()
           holder.columnfivetv_mvi?.text=currentItem?.get(0)?.mvi.toString()
           holder.columnfivetv_fio?.text=currentItem?.get(0)?.fiO2.toString()
           holder.columnfivetv_rr?.text=currentItem?.get(0)?.rr.toString()
           holder.columnfivetv_ie?.text=currentItem?.get(0)?.ieRatio.toString()
           holder.columnfivetv_tinsp?.text=currentItem?.get(0)?.tinsp.toString()
           holder.columnfivetv_texp?.text=currentItem?.get(0)?.texp.toString()
           holder.columnfivetv_leak?.text=currentItem?.get(0)?.leak.toString()*/
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    class TPViewHolder(view: View) : RecyclerView.ViewHolder(view){
        lateinit var   rvUnNested:RecyclerView
        //Column one
       /* var columnonetv_date: TextView?=null
        var columnonetv_time : TextView?= null
        var columnonetv_pip : TextView?= null
        var columnonetv_sp : TextView?= null
        var columnonetv_peep : TextView?= null
        var columnonetv_ma : TextView?= null
        var columnonetv_vti : TextView?= null
        var columnonetv_vte : TextView?= null
        var columnonetv_mve : TextView?= null
        var columnonetv_mvi : TextView?= null
        var columnonetv_fio : TextView?= null
        var columnonetv_rr : TextView?= null
        var columnonetv_ie : TextView?= null
        var columnonetv_tinsp : TextView?= null
        var columnonetv_texp : TextView?= null
        var columnonetv_leak : TextView?= null

        //Column two
        var columntwotv_date: TextView?=null
        var columntwotv_time : TextView?= null
        var columntwotv_pip : TextView?= null
        var columntwotv_sp : TextView?= null
        var columntwotv_peep : TextView?= null
        var columntwotv_ma : TextView?= null
        var columntwotv_vti : TextView?= null
        var columntwotv_vte : TextView?= null
        var columntwotv_mve : TextView?= null
        var columntwotv_mvi : TextView?= null
        var columntwotv_fio : TextView?= null
        var columntwotv_rr : TextView?= null
        var columntwotv_ie : TextView?= null
        var columntwotv_tinsp : TextView?= null
        var columntwotv_texp : TextView?= null
        var columntwotv_leak : TextView?= null

        //column three
        var columnthreetv_date: TextView?=null
        var columnthreetv_time : TextView?= null
        var columnthreetv_pip : TextView?= null
        var columnthreetv_sp : TextView?= null
        var columnthreetv_peep : TextView?= null
        var columnthreetv_ma : TextView?= null
        var columnthreetv_vti : TextView?= null
        var columnthreetv_vte : TextView?= null
        var columnthreetv_mve : TextView?= null
        var columnthreetv_mvi : TextView?= null
        var columnthreetv_fio : TextView?= null
        var columnthreetv_rr : TextView?= null
        var columnthreetv_ie : TextView?= null
        var columnthreetv_tinsp : TextView?= null
        var columnthreetv_texp : TextView?= null
        var columnthreetv_leak : TextView?= null
        //column four
        var columnfourtv_date: TextView?=null
        var columnfourtv_time : TextView?= null
        var columnfourtv_pip : TextView?= null
        var columnfourtv_sp : TextView?= null
        var columnfourtv_peep : TextView?= null
        var columnfourtv_ma : TextView?= null
        var columnfourtv_vti : TextView?= null
        var columnfourtv_vte : TextView?= null
        var columnfourtv_mve : TextView?= null
        var columnfourtv_mvi : TextView?= null
        var columnfourtv_fio : TextView?= null
        var columnfourtv_rr : TextView?= null
        var columnfourtv_ie : TextView?= null
        var columnfourtv_tinsp : TextView?= null
        var columnfourtv_texp : TextView?= null
        var columnfourtv_leak : TextView?= null
        //column five
        var columnfivetv_date: TextView?=null
        var columnfivetv_time : TextView?= null
        var columnfivetv_pip : TextView?= null
        var columnfivetv_sp : TextView?= null
        var columnfivetv_peep : TextView?= null
        var columnfivetv_ma : TextView?= null
        var columnfivetv_vti : TextView?= null
        var columnfivetv_vte : TextView?= null
        var columnfivetv_mve : TextView?= null
        var columnfivetv_mvi : TextView?= null
        var columnfivetv_fio : TextView?= null
        var columnfivetv_rr : TextView?= null
        var columnfivetv_ie : TextView?= null
        var columnfivetv_tinsp : TextView?= null
        var columnfivetv_texp : TextView?= null
        var columnfivetv_leak : TextView?= null*/

        init {
            rvUnNested=view.findViewById(R.id.rv_unnested)
            rvUnNested?.layoutManager = LinearLayoutManager(view.context,
                LinearLayoutManager.HORIZONTAL,false)
            rvUnNested?.setNestedScrollingEnabled(false)
            ViewCompat.setNestedScrollingEnabled(rvUnNested, false)
            rvUnNested.setHasFixedSize(true)
            rvUnNested.setItemViewCacheSize(10);

            //column one views
        /*    columnonetv_date  =view.findViewById (R.id.columnone_tv_date)
            columnonetv_time  =view.findViewById (R.id.columnone_tv_time)
            columnonetv_pip   =view.findViewById  (R.id.columnone_tv_pip)
            columnonetv_sp    =view.findViewById   (R.id.columnonetv_sp)
            columnonetv_peep  =view.findViewById (R.id.columnonetv_peep)
            columnonetv_ma    =view.findViewById   (R.id.columnonetv_ma)
            columnonetv_vti   =view.findViewById  (R.id.columnonetv_vti)
            columnonetv_vte   =view.findViewById  (R.id.columnonetv_vte)
            columnonetv_mve   =view.findViewById  (R.id.columnonetv_mve)
            columnonetv_mvi   =view.findViewById  (R.id.columnonetv_mvi)
            columnonetv_fio   =view.findViewById  (R.id.columnonetv_fio)
            columnonetv_rr    =view.findViewById   (R.id.columnonetv_rr)
            columnonetv_ie    =view.findViewById   (R.id.columnonetv_ie)
            columnonetv_tinsp =view.findViewById(R.id.columnonetv_tinsp)
            columnonetv_texp  =view.findViewById (R.id.columnonetv_texp)
            columnonetv_leak  =view.findViewById (R.id.columnonetv_leak)

            //column two views
            columntwotv_date  =view.findViewById (R.id.columntwotv_date)
            columntwotv_time  =view.findViewById (R.id.columntwotv_time)
            columntwotv_pip   =view.findViewById  (R.id.columntwotv_pip)
            columntwotv_sp    =view.findViewById   (R.id.columntwotv_sp)
            columntwotv_peep  =view.findViewById (R.id.columntwotv_peep)
            columntwotv_ma    =view.findViewById   (R.id.columntwotv_ma)
            columntwotv_vti   =view.findViewById  (R.id.columntwotv_vti)
            columntwotv_vte   =view.findViewById  (R.id.columntwotv_vte)
            columntwotv_mve   =view.findViewById  (R.id.columntwotv_mve)
            columntwotv_mvi   =view.findViewById  (R.id.columntwotv_mvi)
            columntwotv_fio   =view.findViewById  (R.id.columntwotv_fio)
            columntwotv_rr    =view.findViewById   (R.id.columntwotv_rr)
            columntwotv_ie    =view.findViewById   (R.id.columntwotv_ie)
            columntwotv_tinsp =view.findViewById(R.id.columntwotv_tinsp)
            columntwotv_texp  =view.findViewById (R.id.columntwotv_texp)
            columntwotv_leak  =view.findViewById (R.id.columntwotv_leak)

            //column three views
            columnthreetv_date  =view.findViewById (R.id.columnthreetv_date)
            columnthreetv_time  =view.findViewById (R.id.columnthreetv_time)
            columnthreetv_pip   =view.findViewById  (R.id.columnthreetv_pip)
            columnthreetv_sp    =view.findViewById   (R.id.columnthreetv_sp)
            columnthreetv_peep  =view.findViewById (R.id.columnthreetv_peep)
            columnthreetv_ma    =view.findViewById   (R.id.columnthreetv_ma)
            columnthreetv_vti   =view.findViewById  (R.id.columnthreetv_vti)
            columnthreetv_vte   =view.findViewById  (R.id.columnthreetv_vte)
            columnthreetv_mve   =view.findViewById  (R.id.columnthreetv_mve)
            columnthreetv_mvi   =view.findViewById  (R.id.columnthreetv_mvi)
            columnthreetv_fio   =view.findViewById  (R.id.columnthreetv_fio)
            columnthreetv_rr    =view.findViewById   (R.id.columnthreetv_rr)
            columnthreetv_ie    =view.findViewById   (R.id.columnthreetv_ie)
            columnthreetv_tinsp =view.findViewById(R.id.columnthreetv_tinsp)
            columnthreetv_texp  =view.findViewById (R.id.columnthreetv_texp)
            columnthreetv_leak  =view.findViewById (R.id.columnthreetv_leak)

            columnfourtv_date  =view.findViewById (R.id.columnfourtv_date)
            columnfourtv_time  =view.findViewById (R.id.columnfourtv_time)
            columnfourtv_pip   =view.findViewById  (R.id.columnfourtv_pip)
            columnfourtv_sp    =view.findViewById   (R.id.columnfourtv_sp)
            columnfourtv_peep  =view.findViewById (R.id.columnfourtv_peep)
            columnfourtv_ma    =view.findViewById   (R.id.columnfourtv_ma)
            columnfourtv_vti   =view.findViewById  (R.id.columnfourtv_vti)
            columnfourtv_vte   =view.findViewById  (R.id.columnfourtv_vte)
            columnfourtv_mve   =view.findViewById  (R.id.columnfourtv_mve)
            columnfourtv_mvi   =view.findViewById  (R.id.columnfourtv_mvi)
            columnfourtv_fio   =view.findViewById  (R.id.columnfourtv_fio)
            columnfourtv_rr    =view.findViewById   (R.id.columnfourtv_rr)
            columnfourtv_ie    =view.findViewById   (R.id.columnfourtv_ie)
            columnfourtv_tinsp =view.findViewById(R.id.columnfourtv_tinsp)
            columnfourtv_texp  =view.findViewById (R.id.columnfourtv_texp)
            columnfourtv_leak  =view.findViewById (R.id.columnfourtv_leak)

            columnfivetv_date  =view.findViewById (R.id.columnfivetv_date)
            columnfivetv_time  =view.findViewById (R.id.columnfivetv_time)
            columnfivetv_pip   =view.findViewById  (R.id.columnfivetv_pip)
            columnfivetv_sp    =view.findViewById   (R.id.columnfivetv_sp)
            columnfivetv_peep  =view.findViewById (R.id.columnfivetv_peep)
            columnfivetv_ma    =view.findViewById   (R.id.columnfivetv_ma)
            columnfivetv_vti   =view.findViewById  (R.id.columnfivetv_vti)
            columnfivetv_vte   =view.findViewById  (R.id.columnfivetv_vte)
            columnfivetv_mve   =view.findViewById  (R.id.columnfivetv_mve)
            columnfivetv_mvi   =view.findViewById  (R.id.columnfivetv_mvi)
            columnfivetv_fio   =view.findViewById  (R.id.columnfivetv_fio)
            columnfivetv_rr    =view.findViewById   (R.id.columnfivetv_rr)
            columnfivetv_ie    =view.findViewById   (R.id.columnfivetv_ie)
            columnfivetv_tinsp =view.findViewById(R.id.columnfivetv_tinsp)
            columnfivetv_texp  =view.findViewById (R.id.columnfivetv_texp)
            columnfivetv_leak  =view.findViewById (R.id.columnfivetv_leak)*/
        }
    }


}