package com.agvahealthcare.ventilator_ext.dashboard.quad_graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.dashboard.GraphLayoutFragment
import com.agvahealthcare.ventilator_ext.dashboard.chart.*
import com.agvahealthcare.ventilator_ext.utility.*

class QuadFragmentGraph : GraphLayoutFragment("QuadGraphFragment")  {


    private lateinit var pressureChartFragment: PressureChartFragment
    private lateinit var volumeChartFragment: VolumeChartFragment
    private lateinit var flowChartFragment: FlowChartFragment
    private lateinit var loopChartFragment: FlowVolumeChartFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_quad_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        initQuadGraph1()
        initQuadGraph2()
        initQuadGraph3()
        initQuadGraph4()

    }

    private fun initQuadGraph1() {
        pressureChartFragment = PressureChartFragment.newInstance(GraphType.PRESSURE,GRAPH_PRESSURE_MIN, GRAPH_PRESSURE_MAX)
        childFragmentManager.beginTransaction()
            .replace(R.id.containerQuadGraph1, pressureChartFragment, pressureChartFragment::class.java.javaClass.simpleName)
            .commit()
    }

    private fun initQuadGraph2() {
        volumeChartFragment = VolumeChartFragment.newInstance(GraphType.VOLUME,GRAPH_VOLUME_MIN, GRAPH_VOLUME_MAX)

        childFragmentManager.beginTransaction()
            .replace(R.id.containerQuadGraph2, volumeChartFragment, volumeChartFragment::class.java.javaClass.simpleName)
            .commit()
    }

    private fun initQuadGraph3() {
        flowChartFragment = FlowChartFragment.newInstance(GraphType.FLOW,GRAPH_FLOW_MIN, GRAPH_FLOW_MAX)
        childFragmentManager.beginTransaction()
            .replace(R.id.containerQuadGraph3, flowChartFragment, flowChartFragment::class.java.javaClass.simpleName)
            .commit()
    }
    private fun initQuadGraph4() {
        loopChartFragment = FlowVolumeChartFragment.newInstance(GraphType.FLOW_VOLUME)
        childFragmentManager.beginTransaction()
            .replace(R.id.containerQuadGraph4, loopChartFragment, loopChartFragment::class.java.javaClass.simpleName)
            .commit()
    }

    fun addGraphPressureData(x: Int, y: Float) {
        pressureChartFragment.addEntry(x, y)
    }

    fun addGraphVolumeData(x: Int, y: Float) {
        volumeChartFragment.addEntry(x, y)
    }

    fun addGraphFlowData(x: Int, y: Float) {
        flowChartFragment.addEntry(x, y)
    }

    fun clearSeries(){
        loopChartFragment.clearSeries()
    }


}
