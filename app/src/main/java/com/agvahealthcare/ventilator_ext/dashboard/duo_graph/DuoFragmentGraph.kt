package com.agvahealthcare.ventilator_ext.dashboard.duo_graph

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.dashboard.GraphLayoutFragment
import com.agvahealthcare.ventilator_ext.dashboard.chart.GraphType
import com.agvahealthcare.ventilator_ext.dashboard.chart.PressureChartFragment
import com.agvahealthcare.ventilator_ext.dashboard.chart.VolumeChartFragment
import com.agvahealthcare.ventilator_ext.utility.GRAPH_PRESSURE_MAX
import com.agvahealthcare.ventilator_ext.utility.GRAPH_PRESSURE_MIN
import com.agvahealthcare.ventilator_ext.utility.GRAPH_VOLUME_MAX
import com.agvahealthcare.ventilator_ext.utility.GRAPH_VOLUME_MIN

class DuoFragmentGraph : GraphLayoutFragment("DuoGraphFragment")  {

    private lateinit var pressureChartFragment: PressureChartFragment
    private lateinit var volumeChartFragment: VolumeChartFragment


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_duo_graph, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        initDuoGraph1()
        initDuoGraph2()

    }

//    private val pressureChartOptionSelectListener = object: ChartOptionListener {
//        override fun onSelectChart(olderChartType: GraphType, newChartType: GraphType) {
//            val newFragment = when(newChartType){
//                GraphType.PRESSURE -> {
//                    PressureChartFragment.newInstance(
//                        GraphType.PRESSURE,
//                        GRAPH_PRESSURE_MIN,
//                        GRAPH_PRESSURE_MAX
//                    )
//                }
//
//                GraphType.VOLUME -> {
//                    VolumeChartFragment.newInstance(
//                        GraphType.VOLUME,
//                        GRAPH_VOLUME_MIN,
//                        GRAPH_VOLUME_MAX
//                    )
//                }
//
//                GraphType.FLOW -> {
//                    FlowChartFragment.newInstance(
//                        GraphType.FLOW,
//                        GRAPH_FLOW_MIN,
//                        GRAPH_FLOW_MAX
//                    )
//                }
//                else -> throw InvalidModeException()
//            }
//
//            Log.i("CHARTSWAPCHECK", "Swapping chart from $olderChartType to ${newFragment.type} (${newFragment::class.java.javaClass.simpleName})")
//
//            childFragmentManager.beginTransaction()
//                .replace(R.id.containerDuoGraph1,
//                    newFragment,
//                    newFragment::class.java.javaClass.simpleName)
//                .commit()
//        }
//
//    }

    private fun initDuoGraph1() {

        pressureChartFragment = PressureChartFragment.newInstance(GraphType.PRESSURE, GRAPH_PRESSURE_MIN, GRAPH_PRESSURE_MAX,
//                pressureChartOptionSelectListener
        )

        childFragmentManager.beginTransaction()
            .replace(R.id.containerDuoGraph1,
                pressureChartFragment,
                pressureChartFragment::class.java.javaClass.simpleName)
            .commit()
    }

    private fun initDuoGraph2() {
        volumeChartFragment =
            VolumeChartFragment.newInstance(GraphType.VOLUME, GRAPH_VOLUME_MIN, GRAPH_VOLUME_MAX)
        childFragmentManager.beginTransaction()
            .replace(R.id.containerDuoGraph2,
                volumeChartFragment,
                volumeChartFragment::class.java.javaClass.simpleName)
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

    }

    fun clearSeries(){

    }

}