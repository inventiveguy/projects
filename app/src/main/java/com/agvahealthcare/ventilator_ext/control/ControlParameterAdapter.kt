package com.agvahealthcare.ventilator_ext.control.basic

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.LayoutInflaterCompat
import androidx.recyclerview.widget.RecyclerView
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.control.backup.BackupFragment
import com.agvahealthcare.ventilator_ext.dashboard.DashBoardActivity
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.model.ControlParameterModel
import com.agvahealthcare.ventilator_ext.service.CommunicationService
import com.agvahealthcare.ventilator_ext.utility.ToastFactory
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import kotlin.reflect.KClass


interface ControlParameterClickListener {
    fun onClick(position: Int, model: ControlParameterModel)
    fun onStateChange(isActive: Boolean, type: Configs.ControlSettingType)
}

class ControlParameterAdapter(val ctx: Context,  modelList: ArrayList<ControlParameterModel>, private val controlParamClickListener: ControlParameterClickListener?, private val type: Configs.ControlSettingType)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object{
        const val LAYOUT_DEFAULT_VIEW = 0
        const val LAYOUT_BACKUP_VIEW = 1
    }
    val context=ctx
    val prefManager = PreferenceManager(ctx)
    private var dataList : ArrayList<ControlParameterModel> = arrayListOf()

    init {
        if(type == Configs.ControlSettingType.BACKUP || type == Configs.ControlSettingType.ADVANCED) {
            dataList.add(0, ControlParameterModel.empty())  // for backup settings toggle
        }
        dataList.addAll(modelList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val clazz: Class<out RecyclerView.ViewHolder>
        @LayoutRes var layout: Int = if(viewType == LAYOUT_DEFAULT_VIEW){
            clazz = VHControlParameterAdapter::class.java
            R.layout.item_control_data
        } else {
            clazz = ToggleVhControlParameterAdapter::class.java
            R.layout.item_control_backup_data
        }
        val view = LayoutInflater.from(ctx).inflate(layout, parent, false)
        return clazz.getConstructor(View::class.java).newInstance(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tile = dataList[position]
        Log.i("PARAMETERADAPATER_CHECK", "Position = " + position);
        if ((type == Configs.ControlSettingType.BACKUP || type == Configs.ControlSettingType.ADVANCED) && tile.isEmpty){
            (holder as ToggleVhControlParameterAdapter).tgButton?.setOnCheckedChangeListener {_, isChecked ->
                controlParamClickListener?.onStateChange(isChecked, type)
            }
        } else {
            setSelection(holder as VHControlParameterAdapter, tile.isIsselected)

//            tile.upperLimit?.toInt()?.apply { (holder as VHControlParameterAdapter).circleProgressView?.max = this }
           // tile.lowerLimit?.toInt()?.apply { (holder as VHControlParameterAdapter).circleProgressView?.min = this }

            (holder as VHControlParameterAdapter).mainLayoutPanel?.setOnClickListener {
                controlParamClickListener?.onClick(if(type == Configs.ControlSettingType.BACKUP || type == Configs.ControlSettingType.ADVANCED) position - 1 else position ,tile)
                notifyItemChanged(position)
            }


            (holder as VHControlParameterAdapter).textView?.text = Configs.supportPrecision(tile.ventKey, tile.reading)
            //holder.circleProgressView?.setProgress(read.toInt())
            (holder as VHControlParameterAdapter).circleProgressView?.progress = getPercentage(tile)

            (holder as VHControlParameterAdapter).tvValue?.text = tile.title
            (holder as VHControlParameterAdapter).tvUnit?.text = tile.units

        }

    }

    override fun getItemViewType(position: Int): Int  = if ((type == Configs.ControlSettingType.BACKUP || type == Configs.ControlSettingType.ADVANCED) && dataList.get(position).isEmpty) LAYOUT_BACKUP_VIEW else LAYOUT_DEFAULT_VIEW

//To Do for the change of the control parameter tiles.

    private fun setSelection(holder: ControlParameterAdapter.VHControlParameterAdapter, isSelected: Boolean){
        if(isSelected){
            holder.circleProgressView?.progressDrawable = ContextCompat.getDrawable(ctx, R.drawable.progresscircle_with_selection)
            holder.textView?.setTextColor(Color.WHITE)
        } else {
            holder.circleProgressView?.progressDrawable = ContextCompat.getDrawable(ctx, R.drawable.progresscircle)
            holder.textView?.setTextColor(Color.BLACK)
        }
    }


    fun getItems() = dataList

    override fun getItemCount(): Int {
        return  dataList.size
    }

    fun addFilterData(filterList: MutableList<ControlParameterModel>) {
        dataList = filterList as ArrayList<ControlParameterModel>
    }

    private fun getPercentage(value: Double, min: Double, max: Double): Int {
        Log.i("PROGPRECENT_CHECK", "Min = ${min} Max = ${max} Value = ${value} Percent = ${(((value - min )/ (max - min)) *100).toInt()}")
        return (((value - min )/ (max - min)) *100).toInt()
    }
    private fun getPercentage(param: ControlParameterModel) : Int{
        try{
            return getPercentage(param.reading.toDouble(), param.lowerLimit , param.upperLimit);
        }catch (e: Exception) {
            return 0;
        }
    }

    class ToggleVhControlParameterAdapter(view: View):RecyclerView.ViewHolder(view){
        var tgButton: ToggleButton?=null
        init {
            tgButton=view.findViewById(R.id.toggleButton)

        }

    }


    class VHControlParameterAdapter (view: View) : RecyclerView.ViewHolder(view){
        var tvValue: TextView? = null
        var tvUnit: TextView? = null
        var circleProgressView : ProgressBar? = null
        var textView:TextView?=null
        var mainLayoutPanel:LinearLayoutCompat?=null

        init {

            tvValue = view.findViewById(R.id.textViewValue)
            tvUnit = view.findViewById(R.id.textViewUnit)
            circleProgressView = view.findViewById(R.id.progress_bar)
            textView=view.findViewById(R.id.textView)
            mainLayoutPanel=view.findViewById(R.id.mainLayoutPanel)
        }

    }


}


