package eu.selfhost.riegel.mapsforgetest

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File
import org.mapsforge.map.android.util.MapViewerTemplate
import org.mapsforge.map.rendertheme.XmlRenderTheme
import org.mapsforge.map.scalebar.DefaultMapScaleBar
import org.mapsforge.map.scalebar.ImperialUnitAdapter
import org.mapsforge.map.scalebar.MetricUnitAdapter

// TODO: Eigene Controls für Zoom, Maßstab und StartGps
// TODO: RotateViewer
// TODO: overlay track
class MainActivity() : MapViewerTemplate() {

    /**
     * This MapViewer uses the built-in Osmarender theme.
     *
     * @return the render theme to use
     */
    override fun getRenderTheme(): XmlRenderTheme {
        return InternalRenderTheme.OSMARENDER
    }

    /**
     * This MapViewer uses the standard xml layout in the Samples app.
     */
    override fun getLayoutId(): Int {
        return R.layout.mapviewer
    }

    /**
     * The id of the mapview inside the layout.
     *
     * @return the id of the MapView inside the layout.
     */
    override fun getMapViewId(): Int {
        return R.id.mapView
    }

    /**
     * The name of the map file.
     *
     * @return map file name
     */
    override fun getMapFileName(): String {
        return "germany.map"
    }

    override fun getMapFileDirectory(): File {
        val dir = getExternalStorageDirectory(this)
        return File(dir + "/Maps")
    }

    /**
     * Creates a simple tile renderer layer with the AndroidUtil helper.
     */
    override fun createLayers() {
        val tileRendererLayer = AndroidUtil.createTileRendererLayer(this.tileCaches[0],
                this.mapView.model.mapViewPosition, mapFile, renderTheme, false, true, false)
        this.mapView.layerManager.layers.add(tileRendererLayer)
    }

    override fun createMapViews() {
        mapView = getMapView()

        mapView.model.frameBufferModel.overdrawFactor = 1.0
        mapView.model.init(this.preferencesFacade)
        mapView.isClickable = true

        // Use external scale bar
        mapView.mapScaleBar.isVisible = false

        val mapScaleBar = MapScaleBarImpl(
                mapView.model.mapViewPosition,
                mapView.model.mapViewDimension,
                AndroidGraphicFactory.INSTANCE, mapView.model.displayModel)
        mapScaleBar.isVisible = true
        mapScaleBar.scaleBarMode = DefaultMapScaleBar.ScaleBarMode.BOTH
        mapScaleBar.distanceUnitAdapter = MetricUnitAdapter.INSTANCE
        mapScaleBar.secondaryDistanceUnitAdapter = ImperialUnitAdapter.INSTANCE
        val mapScaleBarView = findViewById(R.id.mapScaleBarView) as MapScaleBarView
        mapScaleBarView.setMapScaleBar(mapScaleBar)
        mapView.model.mapViewPosition.addObserver(mapScaleBarView)


        mapView.setBuiltInZoomControls(false)
        mapView.mapZoomControls.zoomLevelMin = zoomLevelMin
        mapView.mapZoomControls.zoomLevelMax = zoomLevelMax
        initializePosition(mapView.model.mapViewPosition)
    }

    /**
     * Creates the tile cache with the AndroidUtil helper
     */
    override fun createTileCaches() {
        this.tileCaches.add(AndroidUtil.createTileCache(this, persistableId,
                this.mapView.model.displayModel.tileSize, this.screenRatio,
                this.mapView.model.frameBufferModel.overdrawFactor))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = javaClass.simpleName

        //this.mapView.setCenter(LatLong(52.5, 13.4))
    }

    override fun onResume() {
        super.onResume()

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
}


//        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
//
//    private val locationListener = object : LocationListener {
//        override fun onLocationChanged(location: Location) {
//            mapView.setCenter(LatLong(location.latitude, location.longitude))
//        }
//
//        override fun onProviderEnabled(p0: String?) {
//        }
//
//        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
//        }
//
//        override fun onProviderDisabled(p0: String?) {
//        }
//    }
//
//
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener)
//    }
//
//    private lateinit var locationManager: LocationManager
//    private lateinit var tileRendererLayer: TileRendererLayer
//
//    companion object {
//        val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1000
//        val LOCATION_REFRESH_TIME = 1000L
//        val LOCATION_REFRESH_DISTANCE = 1.0F
//        private val SETTINGS = "settings"
//        private val PREF_ZOOMLEVEL = "zoomlevel"
//    }
//}
