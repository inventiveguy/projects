
package com.agvahealthcare.ventilator_ext.dashboard.chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.dashboard.TraceArc
import com.agvahealthcare.ventilator_ext.utility.FIFO_CAPACITY
import com.agvahealthcare.ventilator_ext.utility.GRAPH_THRESHOLD
import com.agvahealthcare.ventilator_ext.utility.KEY_MAX_VALUE_VIEW
import com.agvahealthcare.ventilator_ext.utility.KEY_MIN_VALUE_VIEW
 import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode
import com.scichart.charting.visuals.annotations.HorizontalLineAnnotation
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.axes.IAxis
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries
import com.scichart.core.framework.UpdateSuspender
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.FontStyle
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.drawing.utility.ColorUtil
import kotlinx.android.synthetic.main.fragment_chart.*
import java.util.*

class FlowChartFragment private constructor(type: GraphType) : GraphFragment(type) {

    companion object {
        const val TAG = "FlowChartFragment"

        fun newInstance(
            type: GraphType,
            minValue: Int,
            maxValue: Int,
        ): FlowChartFragment {
            val args = Bundle()
            args.putInt(KEY_MIN_VALUE_VIEW, minValue)
            args.putInt(KEY_MAX_VALUE_VIEW, maxValue)

            val fragment = FlowChartFragment(type)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var dataSeries0: IXyDataSeries<Int, Float>
    private lateinit var dataSeries1: IXyDataSeries<Int, Float>
    private var whichTrace = TraceArc.TraceA
    private var minValue: Int? = 0
    private var maxValue: Int? = 0
    val titleStyle = FontStyle(14.0f, ColorUtil.White)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_chart, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        minValue = arguments?.getInt(KEY_MIN_VALUE_VIEW)
        maxValue = arguments?.getInt(KEY_MAX_VALUE_VIEW)

        textViewChartType.text = requireContext().getString(R.string.flow_l_min)
        initGraph()
    }

    private fun initGraph() {


        val xPrimaryAxis: IAxis = sciChartBuilder.newNumericAxis()
            .withVisibleRange(DoubleRange(0.0, GRAPH_THRESHOLD.toDouble()))
            .withAutoRangeMode(AutoRange.Never)
            .withTickLabelStyle(titleStyle)
            .withAxisId("Visible Axis")
            .withMaxAutoTicks(10)
            .build()
        val xSecondaryAxis: IAxis = sciChartBuilder.newNumericAxis()
            .withVisibleRange(DoubleRange(0.0, 12.0))
            .withAutoRangeMode(AutoRange.Never)
            .withTickLabelStyle(titleStyle)
            .withAxisId("Hidden XAxis")
            .withMaxAutoTicks(8)
            .build()

        val yAxis: IAxis = sciChartBuilder.newNumericAxis()
            .withAxisAlignment(AxisAlignment.Left)
            .withVisibleRange(minValue?.toDouble()!!, maxValue?.toDouble()!!)
            .withMaxAutoTicks(5)
            .withTickLabelStyle(titleStyle)
            .withAutoRangeMode(AutoRange.Never)
            .build()


        chartSurface.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
        chartSurface.renderableSeriesAreaBorderStyle = sciChartBuilder.newPen().withColor(ColorUtil.Transparent).build();
        xPrimaryAxis.visibility = View.GONE
        xSecondaryAxis.visibility = View.VISIBLE

        xPrimaryAxis.drawMajorGridLines = false
        xPrimaryAxis.drawMinorGridLines = false
        xPrimaryAxis.drawMajorBands = false
        xPrimaryAxis.drawMajorTicks = false
        xPrimaryAxis.drawMinorTicks = false

        yAxis.drawMajorGridLines = false
        yAxis.drawMinorGridLines = false
        yAxis.drawMajorBands = false
        yAxis.drawMajorTicks = false
        yAxis.drawMajorTicks = false

        dataSeries0 = sciChartBuilder.newXyDataSeries(
            Int::class.javaObjectType,
            Float::class.javaObjectType
        ).withFifoCapacity(FIFO_CAPACITY).build()

        dataSeries1 = sciChartBuilder.newXyDataSeries(
            Int::class.javaObjectType,
            Float::class.javaObjectType
        ).withFifoCapacity(FIFO_CAPACITY).build()

        val rs1: IRenderableSeries = sciChartBuilder.newMountainSeries()
            .withDataSeries(dataSeries0)
            .withXAxisId("Visible Axis")
            .withStrokeStyle(sciChartBuilder.newPen().withColor(Color.parseColor("#FFFFFF")).withThickness(1f).build())
            .withAreaFillColor(Color.parseColor("#FFFFFF"))
            .build()

        val rs2: IRenderableSeries = sciChartBuilder.newMountainSeries()
            .withDataSeries(dataSeries1)
            .withXAxisId("Visible Axis")
            .withStrokeStyle(sciChartBuilder.newPen().withColor(Color.parseColor("#FFFFFF")).withThickness(1f).build())
            .withAreaFillColor(Color.parseColor("#FFFFFF"))
            .build()


        val horizontalLineAnnotation = HorizontalLineAnnotation(activity)
        horizontalLineAnnotation.x1 = 5.0
        horizontalLineAnnotation.y1 = 0.0
        horizontalLineAnnotation.stroke = SolidPenStyle(ColorUtil.Grey, false, 0.07f, floatArrayOf(0f, 0f))
        // horizontalLineAnnotation.horizontalGravity = Gravity.RIGHT
        chartSurface.annotations.add(horizontalLineAnnotation)

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


        Collections.addAll(chartSurface.annotations, horizontalLine, verticalLine)

        Log.i("Horizantlelinecheck","Horizantal Line")

        UpdateSuspender.using(chartSurface) {
            Collections.addAll(chartSurface.xAxes, xPrimaryAxis)
            Collections.addAll(chartSurface.xAxes, xSecondaryAxis)
            Collections.addAll(chartSurface.yAxes, yAxis)
            Collections.addAll(chartSurface.renderableSeries, rs1,rs2)

            //Collections.addAll(chartSurface.annotations, horizontalLineAnnotation)

        }

    }


    fun addEntry(x: Int, y: Float) {
        val xAxis = x % (GRAPH_THRESHOLD + 1)
        Log.i("FLOW_CHART", "x = $xAxis , Y = $y")

        if (whichTrace == TraceArc.TraceA) {
            dataSeries0.append(xAxis, y)
            dataSeries1.append(xAxis, Float.NaN)
        } else {
            dataSeries0.append(xAxis, Float.NaN)
            dataSeries1.append(xAxis, y)
        }

        if (xAxis % GRAPH_THRESHOLD == 0) {
            whichTrace = if (whichTrace == TraceArc.TraceA) TraceArc.TraceB else TraceArc.TraceA
            Log.i("FLOW_CHART_VALUE", "x = $xAxis")
        }
    }


}

