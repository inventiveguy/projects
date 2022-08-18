package com.agvahealthcare.ventilator_ext.dashboard.chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.agvahealthcare.ventilator_ext.R
import com.agvahealthcare.ventilator_ext.callback.OnChartSwapListener
import com.agvahealthcare.ventilator_ext.dashboard.TraceArc
import com.agvahealthcare.ventilator_ext.dashboard.chart.paletteprovider.ColouredLinePaletteProvider
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.utility.*
import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.visuals.annotations.AnnotationCoordinateMode
import com.scichart.charting.visuals.annotations.HorizontalLineAnnotation
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.axes.IAxis

import com.scichart.charting.visuals.renderableSeries.IRenderableSeries
import com.scichart.core.framework.UpdateSuspender
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.drawing.utility.ColorUtil
import kotlinx.android.synthetic.main.fragment_chart.*
import java.util.*


class PressureChartFragment private constructor(type: GraphType): GraphFragment(type) {

    companion object {

        const val TAG = "PressureChartFragment"

        fun newInstance(
            type: GraphType,
            minValue: Int,
            maxValue: Int,
            cc: ColourContainer? = null
//            onChartSelectListener: ChartOptionListener? = null
        ): PressureChartFragment {
            val args = Bundle()
            args.putInt(KEY_MIN_VALUE_VIEW, minValue)
            args.putInt(KEY_MAX_VALUE_VIEW, maxValue)

            val fragment = PressureChartFragment(type)
            fragment.arguments = args
//            fragment.onChartSelectListener = onChartSelectListener
            return fragment
        }
    }


    private var onChartSelectListener: OnChartSwapListener? = null

    private lateinit var dataSeries0: IXyDataSeries<Int, Float>
    private lateinit var dataSeries1: IXyDataSeries<Int, Float>
    private var whichTrace = TraceArc.TraceA
    private var minValue: Int? = 0
    private var maxValue: Int? = 0
    private var cc: ColourContainer? = null
    private var prefManager: PreferenceManager? = null
    var horizontalLineAnnotation:HorizontalLineAnnotation? = null

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
       // cc = arguments?.getSerializable(KEY_COLOR_CONTAINER) as? ColourContainer

       // textViewChartType.text =  requireContext().getString(R.string.pressure_time_chart)

        prefManager = PreferenceManager(requireContext())
        horizontalLineAnnotation= HorizontalLineAnnotation(requireContext())

        initGraph()

//        spinnerChartType?.adapter = ArrayAdapter(requireContext(), R.layout.layout_chart_option, R.id.tvChartName, chartOptions)


//        spinnerChartType?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>?, itemView: View?, index: Int, id: Long) {
//                if(type != chartOptions[index]) onChartSelectListener?.onSelectChart(type, chartOptions[index])
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {}
//        }
    }

    private fun initGraph() {

        val xPRimaryAxis: IAxis = sciChartBuilder.newNumericAxis()
            .withVisibleRange(DoubleRange(0.0, GRAPH_THRESHOLD.toDouble()))
            .withMaxAutoTicks(4)
            .withAxisId("Visible Axis")
            .withAutoRangeMode(AutoRange.Never)
            .build()

        val xsecondaryAxis: IAxis = sciChartBuilder.newNumericAxis()
            .withVisibleRange(DoubleRange(0.0, 12.0))
            .withMaxAutoTicks(8)
            .withAxisId("HiddenXAxis")
            .withAutoRangeMode(AutoRange.Never)
            .build()

        val yAxis: IAxis = sciChartBuilder.newNumericAxis()
            .withAxisAlignment(AxisAlignment.Left)
            .withVisibleRange(minValue?.toDouble()!!, maxValue?.toDouble()!!)
            .withMaxAutoTicks(3)
            .withAutoRangeMode(AutoRange.Never)
            .build()


        chartSurface.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
        chartSurface.renderableSeriesAreaBorderStyle = sciChartBuilder.newPen().withColor(ColorUtil.Grey).build();
        xPRimaryAxis.visibility = View.GONE
        xsecondaryAxis.visibility = View.VISIBLE


        xPRimaryAxis.drawMajorGridLines = false
        xPRimaryAxis.drawMinorGridLines = false
        xPRimaryAxis.drawMajorBands = false
        xPRimaryAxis.drawMajorTicks = false
        xPRimaryAxis.drawMinorTicks = false


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


        horizontalLineAnnotation?.stroke = SolidPenStyle(ColorUtil.Red, false, 1.0f, floatArrayOf(0f, 0f))


        val rs1: IRenderableSeries = sciChartBuilder.newLineSeries()
            .withStrokeStyle(sciChartBuilder.newPen().withColor(ColorUtil.White).withThickness(1f).build())
            .withDataSeries(dataSeries0)
            .withXAxisId("Visible Axis")
            .apply {
                cc?.let { this.withPaletteProvider(ColouredLinePaletteProvider(horizontalLineAnnotation!!)) }
            }
            .build()

        val rs2: IRenderableSeries = sciChartBuilder.newLineSeries()
            .withStrokeStyle(sciChartBuilder.newPen().withColor(ColorUtil.White).withThickness(1f).build())
            .withDataSeries(dataSeries1)
            .withXAxisId("Visible Axis")
            .apply {
                cc?.let { this.withPaletteProvider(ColouredLinePaletteProvider(horizontalLineAnnotation!!)) }
            }
            .build()

        // draw desired border using LineAnnotation
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
        /* var limit= prefManager?.readPipLimits()
            val horizontalLineAnnotation = HorizontalLineAnnotation(activity)
            horizontalLineAnnotation.x1 = 5.0
            horizontalLineAnnotation.setIsEditable(true)
            Log.i("limitValue","${ limit?.get(1)}")
            horizontalLineAnnotation.y1 = limit?.get(1)
            horizontalLineAnnotation.stroke = SolidPenStyle(ColorUtil.Red, false, 1.0f, floatArrayOf(0f, 0f))
            // horizontalLineAnnotation.horizontalGravity = Gravity.RIGHT*/


        UpdateSuspender.using(chartSurface) {
            Collections.addAll(chartSurface.xAxes, xPRimaryAxis)
            Collections.addAll(chartSurface.xAxes,xsecondaryAxis)
            Collections.addAll(chartSurface.yAxes, yAxis)
            Collections.addAll(chartSurface.renderableSeries, rs1,rs2)
            chartSurface.annotations.add(horizontalLineAnnotation)

            //sciChartBuilder.newAnimator(rSeries).withWaveTransformation().withInterpolator(DecelerateInterpolator()).withDuration(3000).withStartDelay(MAX_X_AXIS_LIMIT).start()

        }

    }

    fun addEntry(x: Int, y: Float) {
        val xAxis = x % (GRAPH_THRESHOLD + 1)
        val limit= prefManager?.readPipLimits()
        horizontalLineAnnotation?.x1 = 5.0
        horizontalLineAnnotation?.setIsEditable(false)
        Log.i("limitValue","${ limit?.get(1)}")
        horizontalLineAnnotation?.y1 = limit?.get(1)
        // horizontalLineAnnotation.horizontalGravity = Gravity.RI


        if (whichTrace == TraceArc.TraceA) {
            dataSeries0.append(xAxis, y)
            dataSeries1.append(xAxis, Float.NaN)

        } else {
            dataSeries0.append(xAxis, Float.NaN)
            dataSeries1.append(xAxis, y)
        }

        if (xAxis % GRAPH_THRESHOLD == 0) {
            whichTrace = if (whichTrace == TraceArc.TraceA) TraceArc.TraceB else TraceArc.TraceA
        }
    }


}

