package eu.selfhost.riegel.mapsforgetest

import android.content.Context
import android.support.v4.content.ContextCompat
import java.io.File

/**
 * Created by urieg on 29.12.2017.
 * Access map files on SD card
 */
fun getRootOfExternalStorage(file: File, context: Context): String =
    file.absolutePath.replace("/Android/data/${context.packageName}/files".toRegex(), "")

fun getExternalStorageDirectory(context: Context): String {
    val externalStorageFiles = ContextCompat.getExternalFilesDirs(context, null)
    return externalStorageFiles.map { getRootOfExternalStorage(it, context) }.filter { !it.contains("emulated") }.first()
}

