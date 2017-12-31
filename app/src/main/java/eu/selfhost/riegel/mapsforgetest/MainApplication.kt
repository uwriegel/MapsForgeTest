package eu.selfhost.riegel.mapsforgetest

import android.app.Application
import org.mapsforge.map.android.graphics.AndroidGraphicFactory

/**
 * Created by urieg on 31.12.2017.
 *
 * Application, um Grafik-Library zu initialisieren
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidGraphicFactory.createInstance(this)
    }
}