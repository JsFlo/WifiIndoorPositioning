package fhc.tfsandbox.wifidatacollector.output

import android.content.Context
import java.io.FileOutputStream


interface StreamProvider {
    fun getFileOutputStream(): FileOutputStream
}

/**
 * Provides a [FileOutputStream] using the [context] passed in.
 *
 * Every time the [getFileOutputStream] method is called a different [FileOutputStream] will
 * be created with a different name based on [fileNamePrefix],[counter] and [fileExt].
 *
 * Ex.
 * myData_01.txt
 * myData_02.txt
 * myData_03.txt
 *
 * @property context Context used to open [FileOutputStream]
 * @property fileNamePrefix file name prefix
 * @property fileExt file extension
 * @property counter Counter used to post fix the file name
 */
class FileCounterStreamProvider(private val context: Context,
                                private val sessionPrefix: String,
                                private val fileNamePrefix: String,
                                private val fileExt: String = ".txt",
                                private var counter: Int = 0) : StreamProvider {

    override fun getFileOutputStream(): FileOutputStream {
        counter++
        return context.openFileOutput(getFileName(counter.toString()), Context.MODE_PRIVATE)
    }

    private fun getFileName(postFixCount: String): String {
        return "${sessionPrefix}_${fileNamePrefix}_$postFixCount$fileExt"
    }
}

