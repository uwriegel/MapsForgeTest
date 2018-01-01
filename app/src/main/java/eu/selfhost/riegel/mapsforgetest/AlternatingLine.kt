package eu.selfhost.riegel.mapsforgetest

import org.mapsforge.core.graphics.*
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.Point
import org.mapsforge.core.util.MercatorProjection
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.layer.overlay.Polyline

/**
 * Created by urieg on 01.01.2018.
 */
class AlternatingLine(internal var graphicFactory: GraphicFactory) : Polyline(null, graphicFactory) {

    @Synchronized
    override fun draw(boundingBox: BoundingBox, zoomLevel: Byte, canvas: Canvas, topLeftPoint: Point) {
        if (latLongs.isEmpty())
            return

        val iterator = latLongs.iterator()
        if (!iterator.hasNext()) {
            return
        }
        val mapSize = MercatorProjection.getMapSize(zoomLevel, displayModel.tileSize)
        var from = iterator.next()
        while (iterator.hasNext()) {
            val to = iterator.next()
            if (boundingBox.contains(to) || boundingBox.contains(from)) {
                val paint = getPaintStroke(from, to)
                val x1 = (MercatorProjection.longitudeToPixelX(from.longitude, mapSize) - topLeftPoint.x).toInt()
                val y1 = (MercatorProjection.latitudeToPixelY(from.latitude, mapSize) - topLeftPoint.y).toInt()
                val x2 = (MercatorProjection.longitudeToPixelX(to.longitude, mapSize) - topLeftPoint.x).toInt()
                val y2 = (MercatorProjection.latitudeToPixelY(to.latitude, mapSize) - topLeftPoint.y).toInt()
                canvas.drawLine(x1, y1, x2, y2, paint)
            }
            from = to
        }
    }

    @Synchronized
    fun getPaintStroke(from: LatLong, to: LatLong?): Paint {
        val paint = AndroidGraphicFactory.INSTANCE.createPaint()
        paint.strokeWidth = 16F
        paint.setStyle(Style.STROKE)
        val alt = if (to == null) 0 else (to as TrackGpxParser.TrackPoint).altitude
        when (alt % 3) {
            0 -> paint.color = AndroidGraphicFactory.INSTANCE.createColor(Color.GREEN)
            1 -> paint.color = AndroidGraphicFactory.INSTANCE.createColor(Color.RED)
            else -> paint.color = AndroidGraphicFactory.INSTANCE.createColor(Color.BLUE)
        }
        return paint
    }

    internal class BB(minLatitude: Double, minLongitude: Double, maxLatitude: Double, maxLongitude: Double) : BoundingBox(minLatitude, minLongitude, maxLatitude, maxLongitude) {

        fun extend(latLong: LatLong): BoundingBox {
            return BB(
                    Math.min(minLatitude, latLong.latitude),
                    Math.min(minLongitude, latLong.longitude),
                    Math.max(maxLatitude, latLong.latitude),
                    Math.max(maxLongitude, latLong.longitude))
        }
    }
}
