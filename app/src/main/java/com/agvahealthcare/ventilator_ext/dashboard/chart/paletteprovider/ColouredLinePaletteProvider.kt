package com.agvahealthcare.ventilator_ext.dashboard.chart.paletteprovider

import android.util.Log
import com.scichart.charting.visuals.annotations.IAnnotation
import com.scichart.charting.visuals.renderableSeries.XyRenderableSeriesBase
import com.scichart.charting.visuals.renderableSeries.data.XyRenderPassData
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.core.model.IntegerValues
import com.scichart.drawing.utility.ColorUtil



class ColouredLinePaletteProvider(private val limitAnnotation: IAnnotation, private val color: Int = ColorUtil.Red, private val colorA: Int = ColorUtil.Green, private val colorC: Int = ColorUtil.Yellow): PaletteProviderBase<XyRenderableSeriesBase>(XyRenderableSeriesBase::class.java), IFillPaletteProvider {

    private val colorValues = IntegerValues()
    /*override fun getFillColors(): IntegerValues {
        return colorValues
    }*/

    override fun update() {
//        Log.i("PaletteCheck", "update is running")
        val renderableSeries: XyRenderableSeriesBase = this.renderableSeries
        val currentRenderPassData = renderableSeries.currentRenderPassData as XyRenderPassData
        val xValues = currentRenderPassData.xValues
        val yValues = currentRenderPassData.yValues

        val size = currentRenderPassData.pointsCount()
        colorValues.setSize(size)

        val limit =  limitAnnotation.y1 as Float
        Log.i("PaletteCheck", "limit is $limit")

        val colorArray = colorValues.itemsArray
        val valueArray = yValues.itemsArray

        valueArray.forEachIndexed { index, y ->
            colorArray[index] = (
                    if(y > limit) {
                color
                    }
                    else ColorUtil.White
                    ).apply { Log.i("PaletteCheck", "color is ${if(this == color) "Red" else "Default"}") }
        }
    }

    override fun getFillColors(): IntegerValues {
        return colorValues
    }


   /* override fun getStrokeColors(): IntegerValues {
        return colorValues
    }*/




}