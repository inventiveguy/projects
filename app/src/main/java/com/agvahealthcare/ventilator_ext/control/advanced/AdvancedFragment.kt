package com.agvahealthcare.ventilator_ext.control.advanced

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.GridLayoutManager
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.control.basic.ControlParameterAdapter
import com.agvahealthcare.ventilator_ext.control.basic.ControlParameterClickListener
import com.agvahealthcare.ventilator_ext.model.ControlParameterModel

import com.agvahealthcare.ventilator_ext.standby.StandbyControlSettingFragment
import com.agvahealthcare.ventilator_ext.utility.GridSpacingItemDecoration
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import kotlinx.android.synthetic.main.fragment_advanced.*
import kotlin.collections.ArrayList

class AdvancedFragment(private var dataList:MutableList<ControlParameterModel>,private var controlParamClickListener: ControlParameterClickListener?=null): StandbyControlSettingFragment(){

    private var controlParamsAdapter: ControlParameterAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_advanced, container, false )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setControlParameters()
    }


    override fun getControlParameters() = controlParamsAdapter?.getItems()

    override fun setControlParameters() {
        controlParamsAdapter =
            ControlParameterAdapter(
                requireContext(),
                dataList as ArrayList<ControlParameterModel>,
                controlParamClickListener,
                Configs.ControlSettingType.ADVANCED
            )

        recyclerViewAdvanced?.apply {
            layoutManager = object: GridLayoutManager(requireContext(), 6){
                override fun canScrollVertically(): Boolean = false
            }
            addItemDecoration(GridSpacingItemDecoration(6, 90))
            adapter = controlParamsAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")

    override fun notifyAdapter() = controlParamsAdapter?.notifyDataSetChanged()


    @SuppressLint("NotifyDataSetChanged")
    override fun dataList(filterVentParameterTiles: ArrayList<ControlParameterModel>) {
        dataList = filterVentParameterTiles
        controlParamsAdapter?.apply{
            addFilterData(dataList as ArrayList<ControlParameterModel>)
            notifyDataSetChanged()
        }
    }



}

