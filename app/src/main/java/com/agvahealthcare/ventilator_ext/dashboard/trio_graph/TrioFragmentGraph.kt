package com.agvahealthcare.ventilator_ext.dashboard.trio_graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.dashboard.GraphLayoutFragment
import com.agvahealthcare.ventilator_ext.dashboard.chart.FlowChartFragment
import com.agvahealthcare.ventilator_ext.dashboard.chart.GraphType
import com.agvahealthcare.ventilator_ext.dashboard.chart.PressureChartFragment
import com.agvahealthcare.ventilator_ext.dashboard.chart.VolumeChartFragment
import com.agvahealthcare.ventilator_ext.utility.*

class TrioFragmentGraph :  GraphLayoutFragment("TrioGraphFragment") {


    private lateinit var pressureChartFragment: PressureChartFragment
    private lateinit var volumeChartFragment: VolumeChartFragment
    private lateinit var flowChartFragment: FlowChartFragment

    companion object {
        const val TAG = "TrioGraphFragment"
        private const val KEY_GRAPH_DATA = "KEY_GRAPH_DATA"

        fun newInstance(titleView: String?): TrioFragmentGraph {
            val args = Bundle()
            args.putString(KEY_GRAPH_DATA, titleView)
            val fragment = TrioFragmentGraph()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_trio_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        initTrioGraph1()
        initTrioGraph2()
        initTrioGraph3()
    }


    private fun initTrioGraph1() {
        pressureChartFragment = PressureChartFragment.newInstance(GraphType.PRESSURE,GRAPH_PRESSURE_MIN, GRAPH_PRESSURE_MAX)
        childFragmentManager.beginTransaction()
            .replace(R.id.containerTrioGraph1, pressureChartFragment, pressureChartFragment::class.java.javaClass.simpleName)
            .commit()
    }

    private fun initTrioGraph2() {
        volumeChartFragment = VolumeChartFragment.newInstance(GraphType.VOLUME,GRAPH_VOLUME_MIN, GRAPH_VOLUME_MAX)
        childFragmentManager.beginTransaction()
            .replace(R.id.containerTrioGraph2, volumeChartFragment, volumeChartFragment::class.java.javaClass.simpleName)
            .commit()
    }

    private fun initTrioGraph3() {
        flowChartFragment = FlowChartFragment.newInstance(GraphType.FLOW,GRAPH_FLOW_MIN, GRAPH_FLOW_MAX)
        childFragmentManager.beginTransaction()
            .replace(R.id.containerTrioGraph3, flowChartFragment, flowChartFragment::class.java.javaClass.simpleName)
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
    }
}