
package com.agvahealthcare.ventilator_ext.dashboard.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.model.ObservedParameterModel
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import com.agvahealthcare.ventilator_ext.utility.utils.Configs.*

interface PrimaryObservedParameterClickListener {
    fun onClick(position: Int, model: ObservedParameterModel)
}


class PrimaryObservedParameterAdapter(
    private val ctx: Context,
    private val parameters: ArrayList<ObservedParameterModel>,
    private val primaryObservedParameterClickListener: PrimaryObservedParameterClickListener? = null
    ) :
    RecyclerView.Adapter<PrimaryObservedParameterAdapter.VHPrimaryObservedParameterAdapter>() {

    companion object{
        val MAX_LIMIT = 5
    }

    val preferenceManager = PreferenceManager(ctx)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): VHPrimaryObservedParameterAdapter {
        Log.i("ObserveValueAdapter", "Calling onCreateViewHolder")
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_observed_parameter, parent, false)
        return VHPrimaryObservedParameterAdapter(itemView)
    }

    override fun onBindViewHolder(holder: VHPrimaryObservedParameterAdapter, position: Int) {
        holder.bind(position, parameters)

        // COLOR OF TILE
        parameters.apply {
            updateGraphics(holder, position, this[position])
        }

    }

    private fun updateGraphics(holder: VHPrimaryObservedParameterAdapter, position: Int, model: ObservedParameterModel) {
        if (!model.actualValue.equals("-".trim())) {

            holder.layoutPanel?.setOnClickListener {
                //onclick event of the click of the observed parameter tile
                primaryObservedParameterClickListener?.onClick(position, model)
            }

            if (isNotValid(model)) {
                if(model.isSelected) {
                    holder.apply {
                        layoutPanel?.setBackgroundResource(R.drawable.background_red_border_red)
                        tvActualValue?.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                    }
                }

                else {
                    holder.apply {
                        layoutPanel?.setBackgroundResource(R.drawable.background_red_border_red)
                        tvActualValue?.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                    }
                }

            } else {
                if(model.isSelected) {
                    holder.apply {
                        layoutPanel?.setBackgroundResource(R.drawable.background_black_border_white)
                        tvActualValue?.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                    }
                }

                    //holder.layoutPanel?.setBackgroundResource(R.drawable.background_black_border_white)
                else holder.layoutPanel?.setBackgroundResource(R.drawable.background_black_border_black)
            }

        } else holder.layoutPanel?.setBackgroundResource(R.drawable.background_black_border_black)


    }
   /*private fun updateGraphics(holder: VHPrimaryObservedParameterAdapter, position: Int, model: ObservedParameterModel) {
       if (!model.actualValue.equals("-".trim())) {

           holder.layoutPanel?.setOnClickListener {
               primaryObservedParameterClickListener?.onClick(position,model)
           }

if (isNotValid(model)) {
               if(model.isSelected) holder.tvActualValue?.setTextColor(ContextCompat.getColor(ctx,R.color.white))//holder.layoutPanel?.setBackgroundResource(R.drawable.background_red_border_white)
               else  holder.tvActualValue?.setTextColor(ContextCompat.getColor(ctx,R.color.red))    //holder.layoutPanel?.setBackgroundResource(R.drawable.background_red_border_red)
           } else {
               if(model.isSelected) holder.tvActualValue?.setTextColor(ContextCompat.getColor(ctx,R.color.white)) //holder.layoutPanel?.setBackgroundResource(R.drawable.background_black_border_white)
               else holder.tvActualValue?.setTextColor(ContextCompat.getColor(ctx,R.color.black))//holder.layoutPanel?.setBackgroundResource(R.drawable.background_black_border_black)

           }

       } else holder.tvActualValue?.setTextColor(ContextCompat.getColor(ctx,R.color.black))//holder.layoutPanel?.setBackgroundResource(R.drawable.background_black_border_black)


   }*/


    private fun isNotValid(model: ObservedParameterModel): Boolean{
        val actualValue = model.actualValue.toDoubleOrNull()
        Log.i("TILECHECK", "${model.label} has actualValue = ${model.actualValue}")

        return if(actualValue != null) {
            if (model.lowerLimitValue != null && model.upperLimitValue != null) {
                val lowerLimit: Double = model.lowerLimitValue.toDouble()
                val upperLimit: Double = model.upperLimitValue.toDouble()
                Log.i(
                    "OBSERVER_VALUE",
                    (model.label + model.labelSubscript) + " ---> ACTUAL_VALUE" + actualValue + "LOWER_LIMIT" + lowerLimit + "UPPER_LIMIT" + upperLimit
                )


                val isWarningActivated = (model.label + model.labelSubscript).run {
                    when (this) {
                        LBL_PIP -> preferenceManager.readPipLimitState()
                        LBL_PEEP -> preferenceManager.readPeepLimitState()
                        LBL_VTE -> preferenceManager.readVteLimitState()
                        LBL_FIO2 -> preferenceManager.readFio2LimitState()
                        LBL_RR -> preferenceManager.readRRLimitState()
                        LBL_VTI -> preferenceManager.readVtiLimitState()
                        LBL_AVERAGE_LEAK -> preferenceManager.readLeakLimitState()
                        LBL_MVI -> preferenceManager.readMviLimitState()
                        LBL_SPO2 -> preferenceManager.readSpO2LimitState()
                        else -> false
                    }
                }

                val isParamOutofLimit = actualValue < lowerLimit || actualValue > upperLimit;
                isWarningActivated && isParamOutofLimit
            } else  false
        } else  false
    }

    override fun getItemCount(): Int {
        return parameters.size.takeIf { it < MAX_LIMIT } ?: MAX_LIMIT
    }


    open class VHPrimaryObservedParameterAdapter(row: View) : RecyclerView.ViewHolder(row) {


        var tvValue: TextView? = null
        var tvUnit: TextView? = null
        var tvUpperLimitValue: TextView? = null
        var tvLowerLimitValue: TextView? = null
        var tvActualValue: TextView? = null
        var layoutPanel: ConstraintLayout? = null

        init {
            // Define click listener for the ViewHolder's View.
            tvValue = row.findViewById(R.id.textViewValue)
            tvUnit = row.findViewById(R.id.textViewUnit)
            tvUpperLimitValue = row.findViewById(R.id.textViewUpperLimitValue)
            tvLowerLimitValue = row.findViewById(R.id.textViewDownLimitValue)
            tvActualValue = row.findViewById(R.id.textViewActualValue)
            layoutPanel = row.findViewById(R.id.mainLayoutPanel)


        }

        fun bind(position: Int, dataTiles: ArrayList<ObservedParameterModel>?) {
            val tile = dataTiles?.get(position)
            // bind data
            if (tile != null) {
                if (tile.labelSubscript != null) tvValue?.text = tile.label + tile.labelSubscript
                else tvValue?.text = tile.label
                tvActualValue?.text = if( Configs.LBL_TRIGGER.equals(tile.label) ) tile.actualValue?.get(0)?.toString() else tile.actualValue

                tvUnit?.text = tile.units
                tvLowerLimitValue?.text = tile.lowerLimitValue?.toDouble()?.toInt()?.toString()
                tvUpperLimitValue?.text = tile.upperLimitValue?.toDouble()?.toInt()?.toString()
            }
        }
    }
}

