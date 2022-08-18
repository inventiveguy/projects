
package com.agvahealthcare.ventilator_ext.dashboard.trio_graph

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agvahealthcare.ventilator_ext.dashboard.GraphLayoutFragment
import com.agvahealthcare.ventilator_ext.dashboard.chart.FlowPressureChartFragment
import com.agvahealthcare.ventilator_ext.dashboard.chart.GraphType
import com.agvahealthcare.ventilator_ext.dashboard.chart.PressureChartFragment
import com.agvahealthcare.ventilator_ext.dashboard.chart.PressureVolumeChartFragment
import com.agvahealthcare.ventilator_ext.utility.GRAPH_PRESSURE_MAX
import com.agvahealthcare.ventilator_ext.utility.GRAPH_PRESSURE_MIN
import com.agvahealthcare.ventilator_ext.R

class DivideTrioFragmentGraph : GraphLayoutFragment("DivideTrioGraphFragment") {

    private lateinit var pressureChartFragment: PressureChartFragment
    private lateinit var flowPressureChartFragment: FlowPressureChartFragment

    //    private lateinit var flowVolumeChartFragment: FlowVolumeChartFragment
    private lateinit var pressureVolumeChartFragment: PressureVolumeChartFragment


    companion object {
        const val TAG = "DivideTrioGraphFragment"
        private const val KEY_GRAPH_DATA = "KEY_GRAPH_DATA"

        fun newInstance(titleView: String?): DivideTrioFragmentGraph {
            val args = Bundle()
            args.putString(KEY_GRAPH_DATA, titleView)
            val fragment = DivideTrioFragmentGraph()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_divide_trio_graph, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        initDivideTrioGraph1()
        initDivideTrioGraph2()
        initDivideTrioGraph3()
    }


    private fun initDivideTrioGraph1() {

//        pressureVolumeChartFragment = PressureVolumeChartFragment.newInstance(GraphType.PRESSURE_VOLUME)
//        childFragmentManager.beginTransaction()
//            .replace(R.id.containerDivideTrioGraph1,
//                pressureVolumeChartFragment,
//                pressureVolumeChartFragment::class.java.javaClass.simpleName)
//            .commit()

        pressureChartFragment = PressureChartFragment.newInstance(GraphType.PRESSURE, GRAPH_PRESSURE_MIN, GRAPH_PRESSURE_MAX)
        childFragmentManager.beginTransaction()
            .replace(
                R.id.containerDivideTrioGraph1,
                pressureChartFragment,
                pressureChartFragment::class.java.javaClass.simpleName
            )
            .commit()
    }

    private fun initDivideTrioGraph2() {
        pressureVolumeChartFragment =
            PressureVolumeChartFragment.newInstance(GraphType.PRESSURE_VOLUME)
        childFragmentManager.beginTransaction()
            .replace(
                R.id.containerDivideTrioGraph2,
                pressureVolumeChartFragment,
                pressureVolumeChartFragment::class.java.javaClass.simpleName
            )
            .commit()
    }

    private fun initDivideTrioGraph3() {
        flowPressureChartFragment = FlowPressureChartFragment.newInstance(GraphType.FLOW_PRESSURE)

        childFragmentManager.beginTransaction()
            .replace(
                R.id.containerDivideTrioGraph3,
                flowPressureChartFragment,
                flowPressureChartFragment::class.java.javaClass.simpleName
            )
            .commit()
//        flowVolumeChartFragment = FlowVolumeChartFragment.newInstance(GraphType.FLOW_VOLUME)
//        childFragmentManager.beginTransaction()
//            .replace(R.id.containerDivideTrioGraph3, flowVolumeChartFragment, flowVolumeChartFragment::class.java.javaClass.simpleName)
//            .commit()
    }

    fun addGraphPressureData(x: Int, y: Float) = pressureChartFragment.addEntry(x, y)


    fun addGraphVolumeData(x: Int, y: Float) {}

    fun addGraphFlowData(x: Int, y: Float) {}


    fun addGraphFlowVolumeData(x: Float?, y: Float?, isRedrawRequired: Boolean) {
//        if (isRedrawRequired) flowVolumeChartFragment.clearSeries()
//        else {
//            if (x != null && y != null) flowVolumeChartFragment.addEntry(x, y)
//            Log.i("DIVIDE_QUAD_LOOP_GRAPH", "Invalid Value Of x = $x , Y = $y")
//        }
    }

    fun addGraphFlowPressureData(x: Float?, y: Float?, isRedrawRequired: Boolean) {

        if (isRedrawRequired) flowPressureChartFragment.clearSeries()
        else {
            if (x != null && y != null) flowPressureChartFragment.addEntry(x, y)
            Log.i("DIVIDE_QUAD_LOOP_GRAPH", "Invalid Value Of x = $x , Y = $y")
        }
    }

    fun addGraphPressureVolumeData(x: Float?, y: Float?, isRedrawRequired: Boolean) {
        if (isRedrawRequired) pressureVolumeChartFragment.clearSeries()
        else {
            if (x != null && y != null) pressureVolumeChartFragment.addEntry(x, y)
            Log.i("DIVIDE_QUAD_LOOP_GRAPH", "Invalid Value Of x = $x , Y = $y")

        }
    }

    fun clearSeries() {

    }
}