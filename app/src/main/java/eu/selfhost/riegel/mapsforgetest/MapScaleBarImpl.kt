package eu.selfhost.riegel.mapsforgetest

import org.mapsforge.core.graphics.Bitmap
import org.mapsforge.core.graphics.GraphicContext
import org.mapsforge.core.graphics.GraphicFactory
import org.mapsforge.map.model.DisplayModel
import org.mapsforge.map.model.MapViewDimension
import org.mapsforge.map.model.MapViewPosition
import org.mapsforge.map.scalebar.DefaultMapScaleBar

/**
 * Created by urieg on 31.12.2017.
 */
class MapScaleBarImpl(mapViewPosition: MapViewPosition,
                      private val mapViewDimension: MapViewDimension, graphicFactory: GraphicFactory,
                      displayModel: DisplayModel) : DefaultMapScaleBar(mapViewPosition, mapViewDimension, graphicFactory, displayModel) {

    fun getMapScaleBitmap(): Bitmap { return this.mapScaleBitmap }

    override fun draw(graphicContext: GraphicContext) {
        if (!this.isVisible) {
            return
        }

        if (this.mapViewDimension.dimension == null) {
            return
        }

        if (this.isRedrawNecessary) {
            redraw(this.mapScaleCanvas)
            this.redrawNeeded = false
        }

        graphicContext.drawBitmap(this.mapScaleBitmap, 0, 0)
    }
}