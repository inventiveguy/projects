package com.agvahealthcare.ventilator_ext.dashboard.chart

import androidx.fragment.app.Fragment
import com.scichart.extensions.builders.SciChartBuilder

enum class GraphType {
    PRESSURE,
    VOLUME,
    FLOW,
    PRESSURE_VOLUME,
    FLOW_PRESSURE,
    FLOW_VOLUME
}
open class GraphFragment(val type: GraphType) : Fragment() {
    companion object {
    }
    val sciChartBuilder: SciChartBuilder = SciChartBuilder.instance()



}