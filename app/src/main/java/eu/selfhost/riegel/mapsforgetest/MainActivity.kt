package eu.selfhost.riegel.mapsforgetest

import android.Manifest
import android.annotation.SuppressLint
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

// TODO: Cache tiles
// TODO: Grant permissions ohne eigenen Dialog
// TODO: Speichere letzten Standort
// TODO: overlay track
// TODO: Bewebungsrichtung oben

class MainActivity() : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val externalStorageFiles = ContextCompat.getExternalFilesDirs(this,null)
        externalDrive =
                externalStorageFiles
                        .map { getRootOfExternalStorage(it, this) }
                        .filter { !it.contains("emulated") }.first()

        AndroidGraphicFactory.createInstance(application)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        mapView = MapView(this)
        setContentView(mapView)

        mapView.isClickable = true
        mapView.mapScaleBar.isVisible = true
        mapView.setBuiltInZoomControls(true)
        mapView.setZoomLevelMin(6.toByte())
        mapView.setZoomLevelMax(20.toByte())

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
            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED)
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED)
                // Fill with results
                for ((index, value) in permissions.withIndex())
                    perms.put(value, grantResults[index]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
//                    // All Permissions Granted
                    initializeMapView()
                else
                    // Permission Denied
                    Toast.makeText(this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();
            }
            else ->
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            mapView.setCenter(LatLong(location.latitude, location.longitude))
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissions(): Boolean {
        val permissionsNeeded = ArrayList<String>()
        val permissionsList = ArrayList<String>()
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Zugriff auf persönliche Daten");

        if (permissionsList.size > 0) {
            if (permissionsNeeded.size > 0) {
                // Need Rationale
//                var message = "You need to grant access to " + permissionsNeeded.get(0)
//                for ((index, value) in permissionsNeeded.withIndex()) {
//                    message = message + ", " + permissionsNeeded.get(index)
//                    showMessageOKCancel(message,
//                        new DialogInterface . OnClickListener () {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                requestPermissions(permissionsList.toArray(new String [permissionsList.size()]),
//                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
//                            }
//                        });
//                return;
            }
            requestPermissions(permissionsList.toTypedArray(), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

//        var result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//        result = result && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//        return result
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            return false
//        }
//        else
//            return true

    @RequiresApi(Build.VERSION_CODES.M)
    private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false
        }
        return true
    }

    @SuppressLint("MissingPermission")
    private fun initializeMapView() {
        // create a tile cache of suitable size
        val tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.tileSize, 1f,
                mapView.getModel().frameBufferModel.overdrawFactor)

        // tile renderer layer using internal render theme
        val mapDataStore = MapFile(File("$externalDrive/Maps", MAP_FILE))
        val tileRendererLayer = TileRendererLayer(tileCache, mapDataStore, mapView.model.mapViewPosition,
                AndroidGraphicFactory.INSTANCE)
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)

        // only once a layer is associated with a mapView the rendering starts
        mapView.layerManager.layers.add(tileRendererLayer)
        mapView.setZoomLevel(12.toByte())

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener)
    }

    // name of the map file in the external storage
    private val MAP_FILE = "germany.map"

    private lateinit var mapView: MapView
    private lateinit var locationManager: LocationManager
    private lateinit var externalDrive: String

    companion object {
        val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1000
        val LOCATION_REFRESH_TIME = 1000L
        val LOCATION_REFRESH_DISTANCE = 1.0F
    }
}
