package com.agvahealthcare.ventilator_ext.dashboard.chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.utility.*
import com.scichart.charting.layoutManagers.DefaultLayoutManager
import com.scichart.charting.layoutManagers.ILayoutManager
import com.scichart.charting.layoutManagers.LeftAlignmentInnerAxisLayoutStrategy
import com.scichart.charting.layoutManagers.TopAlignmentInnerAxisLayoutStrategy
import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.modifiers.RolloverModifier
import com.scichart.charting.modifiers.ZoomPanModifier
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.axes.IAxis
import com.scichart.core.IServiceContainer
import com.scichart.core.common.Size
import com.scichart.core.framework.UpdateSuspender
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.utility.ColorUtil
import kotlinx.android.synthetic.main.fragment_chart.*
import java.util.*


class FlowVolumeChartFragment private constructor(type: GraphType): GraphFragment(type) {

    private lateinit var dataSeries: IXyDataSeries<Float, Float>

    companion object {
        const val TAG = "FlowVolumeChartFragment"

        fun newInstance(
            type: GraphType
        ): FlowVolumeChartFragment {
            val args = Bundle()
            val fragment = FlowVolumeChartFragment(type)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_chart, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textViewChartType.text = requireContext().getString(R.string.flow_vol_l_min)
        initGraph()

    }


    private fun initGraph() {
        // val horizontalLine = HorizontalLineAnnotation(context)

        /* val xAxis: IAxis = sciChartBuilder.newNumericAxis()
             .withVisibleRange(DoubleRange(0.0, GRAPH_THRESHOLD.toDouble()))
             .withMaxAutoTicks(30)
             .withAutoRangeMode(AutoRange.Never)
             .build()

         val yAxis: IAxis = sciChartBuilder.newNumericAxis()
             .withAxisAlignment(AxisAlignment.Left)
             .withVisibleRange(minValue?.toDouble()!!, maxValue?.toDouble()!!)
             .withMaxAutoTicks(10)
             .withAutoRangeMode(AutoRange.Never)
             .build()*/


        val zoomPanModifier = ZoomPanModifier()
        val rolloverModifier = RolloverModifier()
        zoomPanModifier.isEnabled=false
        rolloverModifier.isEnabled=false
        val xAxis: IAxis = sciChartBuilder.newNumericAxis()
            .withVisibleRange(DoubleRange(GRAPH_VOLUME_MIN.toDouble(), GRAPH_VOLUME_MAX.toDouble()))
            .withMaxAutoTicks(30)
            .withAutoRangeMode(AutoRange.Always).build()

        val yAxis: IAxis = sciChartBuilder.newNumericAxis()
            .withVisibleRange(DoubleRange(GRAPH_FLOW_MIN.toDouble(), GRAPH_FLOW_MAX.toDouble()))
            .withAxisAlignment(AxisAlignment.Left)
            .withMaxAutoTicks(2)
            .withAutoRangeMode(AutoRange.Always).build()
        // horizontalLine.y1= 80.0


        xAxis.drawMajorGridLines = false
        xAxis.drawMinorGridLines = false
        xAxis.drawMajorBands = false
        xAxis.drawMajorTicks = false
        xAxis.drawMinorTicks = false

        yAxis.drawMajorGridLines = false
        yAxis.drawMinorGridLines = false
        yAxis.drawMajorBands = false
        yAxis.drawMajorTicks = false
        yAxis.drawMajorTicks = false
        // chartSurface.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))

        chartSurface.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
        chartSurface.isHorizontalScrollBarEnabled=false
        chartSurface.isVerticalFadingEdgeEnabled=false
        chartSurface.isClickable=false
        chartSurface.renderableSeriesAreaBorderStyle = sciChartBuilder.newPen().withColor(ColorUtil.Transparent).build();

        dataSeries = sciChartBuilder.newXyDataSeries(
            Float::class.javaObjectType,
            Float::class.javaObjectType
        ).withAcceptsUnsortedData().build()


        val rSeries = sciChartBuilder.newLineSeries().withDataSeries(dataSeries)
            .withStrokeStyle(sciChartBuilder.newPen().withColor(ColorUtil.White).withThickness(1f).build())
            .build()


        val verticalLine = sciChartBuilder.newLineAnnotation()
            .withPosition(0.0, 0.0, 0.0, 1.0)
            .withCoordinateMode(AnnotationCoordinateMode.Relative)
            .withStroke(1f, Color.WHITE)
            .build()

        val horizontalLine = sciChartBuilder.newLineAnnotation()
            .withPosition(0.0, 1.0, 1.0, 1.0)
            .withCoordinateMode(AnnotationCoordinateMode.Relative)
            .withStroke(1f, Color.WHITE)
            .build()




        Log.i("Horizantlelinecheck","Horizantal Line")

        Collections.addAll(chartSurface.annotations, horizontalLine, verticalLine)

        UpdateSuspender.using(chartSurface) {

            chartSurface.layoutManager = CenterLayoutManager(xAxis, yAxis)
            Collections.addAll(chartSurface.xAxes, xAxis)
            Collections.addAll(chartSurface.yAxes, yAxis)
            Collections.addAll(chartSurface.renderableSeries, rSeries)




            //Collections.addAll(chartSurface.annotations,horizontalLine)

            Collections.addAll(
                chartSurface.chartModifiers,
                sciChartBuilder.newModifierGroup().build(),
                zoomPanModifier,
                rolloverModifier,


                )

            sciChartBuilder.newAnimator(rSeries).withSweepTransformation().withDuration(20000)
                .withStartDelay(GRAPH_THRESHOLD.toLong()).start()
        }


    }


    fun clearSeries() =  dataSeries.clear()


    fun addEntry(x: Float, y: Float) {
        Log.i("LoopData" , "x $x  y $y ")
        dataSeries.append(x,y)
    }

    private class CenterLayoutManager(xAxis: IAxis, yAxis: IAxis) :
        ILayoutManager {
        private val defaultLayoutManager: DefaultLayoutManager = DefaultLayoutManager.Builder()
            .setLeftInnerAxesLayoutStrategy(
                CenteredLeftAlignmentInnerAxisLayoutStrategy(
                    xAxis
                )
            )
            .setTopInnerAxesLayoutStrategy(
                CenteredTopAlignmentInnerAxisLayoutStrategy(
                    yAxis
                )
            )
            .build()

        private var isFirstLayout = false
        override fun attachAxis(axis: IAxis, isXAxis: Boolean) {
            defaultLayoutManager.attachAxis(axis, isXAxis)
        }

        override fun detachAxis(axis: IAxis) {
            defaultLayoutManager.detachAxis(axis)
        }

        override fun onAxisPlacementChanged(
            axis: IAxis,
            oldAxisAlignment: AxisAlignment,
            oldIsCenterAxis: Boolean,
            newAxisAlignment: AxisAlignment,
            newIsCenterAxis: Boolean,
        ) {
            defaultLayoutManager.onAxisPlacementChanged(
                axis,
                oldAxisAlignment,
                oldIsCenterAxis,
                newAxisAlignment,
                newIsCenterAxis
            )
        }

        override fun attachTo(services: IServiceContainer) {
            defaultLayoutManager.attachTo(services)
            // need to perform 2 layout passes during first layout of chart
            isFirstLayout = true
        }

        override fun detach() {
            defaultLayoutManager.detach()
        }

        override fun isAttached(): Boolean {
            return defaultLayoutManager.isAttached
        }

        override fun onLayoutChart(width: Int, height: Int): Size {
            // need to perform additional layout pass if it is a first layout pass
            // because we don't know correct size of axes during first layout pass
            if (isFirstLayout) {
                defaultLayoutManager.onLayoutChart(width, height)
                isFirstLayout = false
            }
            return defaultLayoutManager.onLayoutChart(width, height)
        }

        init {
            // need to override default inner layout strategies for bottom and right aligned axes
            // because xAxis has right axis alignment and yAxis has bottom axis alignment
        }
    }


    private class CenteredTopAlignmentInnerAxisLayoutStrategy(private val yAxis: IAxis) :
        TopAlignmentInnerAxisLayoutStrategy() {
        override fun layoutAxes(left: Int, top: Int, right: Int, bottom: Int) {
            // find the coordinate of 0 on the Y Axis in pixels
            // place the stack of the top-aligned X Axes at this coordinate
            val topCoordinate = yAxis.currentCoordinateCalculator.getCoordinate(0.0)
            layoutFromTopToBottom(left, topCoordinate.toInt(), right, axes)
        }
    }

    private class CenteredLeftAlignmentInnerAxisLayoutStrategy(private val xAxis: IAxis) :
        LeftAlignmentInnerAxisLayoutStrategy() {
        override fun layoutAxes(left: Int, top: Int, right: Int, bottom: Int) {
            // find the coordinate of 0 on the X Axis in pixels
            // place the stack of the left-aligned Y Axes at this coordinate
            val leftCoordinate = xAxis.currentCoordinateCalculator.getCoordinate(0.0)
            layoutFromLeftToRight(leftCoordinate.toInt(), top, bottom, axes)
        }
    }


}