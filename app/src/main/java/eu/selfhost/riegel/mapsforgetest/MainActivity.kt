package eu.selfhost.riegel.mapsforgetest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.widget.Toast
import org.mapsforge.map.android.util.MapViewerTemplate
import org.mapsforge.map.rendertheme.XmlRenderTheme

// TODO: Permission request
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
        super.createMapViews()
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
//
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
//                val perms = HashMap<String, Int>()
//                // Initial
//                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED)
//                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED)
//                // Fill with results
//                for ((index, value) in permissions.withIndex())
//                    perms.put(value, grantResults[index])
//                // Check for ACCESS_FINE_LOCATION
//                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
//                    // All Permissions Granted
//                    initializeMapView()
//                else
//                    // Permission Denied
//                    Toast.makeText(this, "Some Permission is Denied", Toast.LENGTH_SHORT).show()
//            }
//            else ->
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        }
//    }
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
//    private fun checkPermissions(): Boolean {
//        val permissionsList = ArrayList<String>()
//        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION)
//        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//
//        val permissions = permissionsList.toTypedArray()
//        if (permissions.count() > 0) {
//            requestPermissions(permissions, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
//            return false
//        }
//        return true
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun initializeMapView() {
//        // create a tile cache of suitable size
//        val tileCache = AndroidUtil.createTileCache(this, "mapcache",
//                mapView.model.displayModel.tileSize, 1f,
//                mapView.model.frameBufferModel.overdrawFactor)
//
//        // tile renderer layer using internal render theme
//        val mapDataStore = MapFile(File("$externalDrive/Maps", MAP_FILE))
//        tileRendererLayer = TileRendererLayer(tileCache, mapDataStore, mapView.model.mapViewPosition,
//                AndroidGraphicFactory.INSTANCE)
//        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)
//
//        // only once a layer is associated with a mapView the rendering starts
//        mapView.layerManager.layers.add(tileRendererLayer)
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
