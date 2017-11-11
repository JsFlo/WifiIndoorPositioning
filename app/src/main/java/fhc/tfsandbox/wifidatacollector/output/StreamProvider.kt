package fhc.tfsandbox.wifidatacollector.output

import android.content.Context
import java.io.FileOutputStream


interface StreamProvider {
    fun getFileOutputStream(): FileOutputStream
}

// should be weak ref to context
class FileCounterStreamProvider(private val context: Context,
                                private val fileNamePrefix: String,
                                private val fileExt: String = ".txt",
                                private var counter: Int = 0) : StreamProvider {

    override fun getFileOutputStream(): FileOutputStream {
        counter++
        return context.openFileOutput(getFileName(counter.toString()), Context.MODE_PRIVATE)
    }

    private fun getFileName(postFixCount: String): String {
        return "${fileNamePrefix}_$postFixCount$fileExt"
    }
}

