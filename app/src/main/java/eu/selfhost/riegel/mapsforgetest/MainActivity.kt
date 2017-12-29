package eu.selfhost.riegel.mapsforgetest

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkPermissions())
            initializeMapView()
    }

    override fun onResume() {
        super.onResume()

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val decorView = window.decorView
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    initializeMapView()
                else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            return false
        }
        else
            return true
    }

    private fun initializeMapView() {
        AndroidGraphicFactory.createInstance(application)

        mapView = MapView(this)
        setContentView(mapView)

        mapView!!.isClickable = true
        mapView!!.mapScaleBar.isVisible = true
        mapView!!.setBuiltInZoomControls(true)
        mapView!!.setZoomLevelMin(6.toByte())
        mapView!!.setZoomLevelMax(20.toByte())

        // create a tile cache of suitable size
        val tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView!!.getModel().displayModel.tileSize, 1f,
                mapView!!.getModel().frameBufferModel.overdrawFactor)

        // tile renderer layer using internal render theme
        val mapDataStore = MapFile(File(Environment.getExternalStorageDirectory(), MAP_FILE))
        val tileRendererLayer = TileRendererLayer(tileCache, mapDataStore, mapView!!.model.mapViewPosition,
                AndroidGraphicFactory.INSTANCE)
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)

        // only once a layer is associated with a mapView the rendering starts
        mapView!!.layerManager.layers.add(tileRendererLayer)

        mapView!!.setCenter(LatLong(52.517037, 13.38886))
        mapView!!.setZoomLevel(12.toByte())
    }

    // name of the map file in the external storage
    private val MAP_FILE = "germany.map"

    // TODO: initialize here?
    private var mapView: MapView? = null

    companion object {
        val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1000
    }
}
