package com.agvahealthcare.ventilator_ext.dashboard.pent_graph

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agvahealthcare.ventilator_ext.dashboard.GraphLayoutFragment
import com.agvahealthcare.ventilator_ext.dashboard.chart.*
import com.agvahealthcare.ventilator_ext.utility.*
import com.agvahealthcare.ventilator_ext.R

class DividePentFragmentGraph : GraphLayoutFragment("DividePentGraphFragment") {

    private lateinit var pressureChartFragment: PressureChartFragment
    private lateinit var volumeChartFragment: VolumeChartFragment
    private lateinit var flowChartFragment: FlowChartFragment
    private lateinit var flowVolumeChartFragment: FlowVolumeChartFragment
    private lateinit var flowPressureChartFragment: FlowPressureChartFragment
    private lateinit var pressureVolumeChartFragment: PressureVolumeChartFragment

    companion object {
        const val TAG = "DividePentGraphFragment"
        private const val KEY_GRAPH_DATA = "KEY_GRAPH_DATA"

        fun newInstance(titleView: String?): DividePentFragmentGraph {
            val args = Bundle()
            args.putString(KEY_GRAPH_DATA, titleView)
            val fragment = DividePentFragmentGraph()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_divide_pent_graph, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        initDividePentGraph1()
        initDividePentGraph2()
        initDividePentraph3()
        initDividePentGraph4()
        initDividePentGraph5()
    }

    private fun initDividePentGraph1() {

        pressureChartFragment= PressureChartFragment.newInstance(GraphType.PRESSURE,
            GRAPH_PRESSURE_MIN, GRAPH_PRESSURE_MAX
        )
        childFragmentManager.beginTransaction()
            .replace(R.id.containerDividePentGraph1,
                pressureChartFragment,
                pressureChartFragment::class.java.javaClass.simpleName)
            .commit()
    }


    private fun initDividePentGraph2() {
        volumeChartFragment = VolumeChartFragment.newInstance(GraphType.VOLUME,
            GRAPH_VOLUME_MIN, GRAPH_VOLUME_MAX
        )
        childFragmentManager.beginTransaction()
            .replace(R.id.containerDividePentGraph2, volumeChartFragment, volumeChartFragment::class.java.javaClass.simpleName)
            .commit()
    }

    private fun initDividePentraph3() {
        flowChartFragment = FlowChartFragment.newInstance( GraphType.FLOW,
            GRAPH_FLOW_MIN, GRAPH_FLOW_MAX
        )
        childFragmentManager.beginTransaction()
            .replace(R.id.containerDividePentGraph3,
                flowChartFragment,
                flowChartFragment::class.java.javaClass.simpleName)
            .commit()
    }

    private fun initDividePentGraph4() {
        pressureVolumeChartFragment = PressureVolumeChartFragment.newInstance(GraphType.PRESSURE_VOLUME)
        childFragmentManager.beginTransaction()
            .replace(R.id.containerDividePentGraph4,
                pressureVolumeChartFragment,
                pressureVolumeChartFragment::class.java.javaClass.simpleName)
            .commit()
    }

    private fun initDividePentGraph5() {
       flowPressureChartFragment = FlowPressureChartFragment.newInstance(GraphType.FLOW_PRESSURE)
        childFragmentManager.beginTransaction()
            .replace(R.id.containerDividePentGraph5,
                flowPressureChartFragment,
                flowPressureChartFragment::class.java.javaClass.simpleName)
            .commit()
    }

    fun addGraphPressureData(x: Int, y: Float) {
        Log.i("PRESSURE_GRAPH", "x = $x , Y = $y")
        pressureChartFragment.addEntry(x, y)
    }

    fun addGraphVolumeData(x: Int, y: Float ) {
        Log.i("VOLUME_GRAPH", "x = $x , Y = $y")
        volumeChartFragment.addEntry(x, y)
    }

    fun addGraphFlowData(x: Int, y: Float) {
        Log.i("FLOW_GRAPH", "x = $x , Y = $y")
        flowChartFragment.addEntry(x, y)

    }

    fun addGraphPressureVolumeData(x: Float?, y: Float?, isRedrawRequired: Boolean) {
        if (isRedrawRequired) pressureVolumeChartFragment.clearSeries()
        else {
            if (x != null && y != null) pressureVolumeChartFragment.addEntry(x, y)
            Log.i("DIVIDE_QUAD_LOOP_GRAPH", "Invalid Value Of x = $x , Y = $y")

        }
    }

/*    fun addGraphFlowVolumeData(x: Float?, y: Float?, isRedrawRequired: Boolean) {
        if(isRedrawRequired) flowVolumeChartFragment.clearSeries()
        else{
            if(x != null && y != null) flowVolumeChartFragment.addEntry(x, y)
            Log.i("DIVIDE_QUAD_LOOP_GRAPH", "Invalid Value Of x = $x , Y = $y")
        }
    }*/

    fun addGraphFlowPressureData(x: Float?, y: Float?, isRedrawRequired: Boolean) {

        if(isRedrawRequired) flowPressureChartFragment.clearSeries()
        else{
            if(x != null && y != null) flowPressureChartFragment.addEntry(x, y)
            Log.i("DIVIDE_QUAD_LOOP_GRAPH", "Invalid Value Of x = $x , Y = $y")
        }
    }
    fun clearSeries() {

    }

}