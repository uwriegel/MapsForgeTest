package eu.selfhost.riegel.mapsforgetest

import android.content.Context
import java.io.File

/**
 * Created by urieg on 29.12.2017.
 */
fun getRootOfExternalStorage(file: File, context: Context): String =
    file.absolutePath.replace("/Android/data/${context.packageName}/files".toRegex(), "")
