package com.agvahealthcare.ventilator_ext.standby

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.model.ControlParameterModel

abstract class StandbyControlSettingFragment : Fragment() {

    abstract fun notifyAdapter() : Unit?;
    abstract fun setControlParameters();
    abstract fun getControlParameters() : ArrayList<ControlParameterModel>?;
    abstract fun dataList(filterVentParameterTiles: ArrayList<ControlParameterModel>);
}