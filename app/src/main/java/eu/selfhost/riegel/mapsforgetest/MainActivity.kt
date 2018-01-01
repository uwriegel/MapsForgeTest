package eu.selfhost.riegel.mapsforgetest

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.rotation.RotateView
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File
import org.mapsforge.map.android.util.MapViewerTemplate
import org.mapsforge.map.rendertheme.XmlRenderTheme
import org.mapsforge.map.scalebar.DefaultMapScaleBar
import org.mapsforge.map.scalebar.ImperialUnitAdapter
import org.mapsforge.map.scalebar.MetricUnitAdapter



// TODO: Compass for heading or heading null
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

    @SuppressLint("MissingPermission")
    protected override fun createControls() {
        val rotateButton = findViewById<Button>(R.id.rotateButton)
        rotateButton.setOnClickListener {
            val rotateView = findViewById<RotateView>(R.id.rotateView)
            rotateView.heading = rotateView.heading - 45f
            rotateView.postInvalidate()
        }

        val gpsButton = findViewById<Button>(R.id.gpsButton)
        gpsButton.setOnClickListener {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener)
        }

        val trackButton = findViewById<Button>(R.id.trackButton)
        trackButton.setOnClickListener {
            polyline = AlternatingLine(AndroidGraphicFactory.INSTANCE)

            val latLongs = polyline.latLongs
            var latLong: LatLong

            val gpxFile = "${getExternalStorageDirectory(this)}/Maps/track.gpx"
            for (trackPoint in TrackGpxParser(File(gpxFile))) {
                latLong = trackPoint
                latLongs.add(latLong)
            }
            // add: mapView.getModel().mapViewPosition.setMapPosition(new MapPosition(bb.getCenterPoint(), LatLongUtils.zoomForBounds(dimension, bb, mapView.getModel().displayModel.getTileSize())));
            // warp to track
            mapView.model.mapViewPosition.center = latLongs[0]
            mapView.layerManager.layers.add(polyline)
        }

        val zoomInButton = findViewById<ImageButton>(R.id.zoomInButton)
        zoomInButton.setOnClickListener { mapView.model.mapViewPosition.zoomIn() }

        val zoomOutButton = findViewById<ImageButton>(R.id.zoomOutButton)
        zoomOutButton.setOnClickListener { mapView.model.mapViewPosition.zoomOut() }
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
        findViewById<RotateView>(R.id.rotateView).setLayerType(View.LAYER_TYPE_SOFTWARE, null)

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

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    }

    /**
     * Creates the tile cache with the AndroidUtil helper
     */
    override fun createTileCaches() {
        this.tileCaches.add(AndroidUtil.createTileCache(this, persistableId,
                mapView.model.displayModel.tileSize, this.screenRatio,
                mapView.model.frameBufferModel.overdrawFactor))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = javaClass.simpleName
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

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (location.hasBearing()) {
                val affe = 2
                val aff = affe +8
            }
            mapView.setCenter(LatLong(location.latitude, location.longitude))
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }
    }

    private lateinit var locationManager: LocationManager
    private lateinit var polyline: AlternatingLine

    companion object {
        val LOCATION_REFRESH_TIME = 1000L
        val LOCATION_REFRESH_DISTANCE = 0.0F
    }
}


