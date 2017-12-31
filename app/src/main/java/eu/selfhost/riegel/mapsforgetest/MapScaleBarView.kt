package eu.selfhost.riegel.mapsforgetest

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.model.common.Observer

/**
 * Created by urieg on 31.12.2017.
 */
class MapScaleBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs), Observer {

    private var mapScaleBar: MapScaleBarImpl? = null

    override fun onChange() {
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val graphicContext = AndroidGraphicFactory.createGraphicContext(canvas)
        mapScaleBar!!.draw(graphicContext)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mapScaleBar!!.getMapScaleBitmap().width, mapScaleBar!!.getMapScaleBitmap().height)
    }

    fun setMapScaleBar(mapScaleBar: MapScaleBarImpl) {
        this.mapScaleBar = mapScaleBar
    }
}
