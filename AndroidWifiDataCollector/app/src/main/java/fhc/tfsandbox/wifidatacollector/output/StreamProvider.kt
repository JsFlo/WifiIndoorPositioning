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
 * be created with a different name based on [fileName],[counter] and [fileExt].
 *
 * Ex.
 * myData_01.txt
 * myData_02.txt
 * myData_03.txt
 *
 * @property context Context used to open [FileOutputStream]
 * @property fileName file name prefix
 * @property fileExt file extension
 * @property counter Counter used to post fix the file name
 */
class FileCounterStreamProvider(private val context: Context,
                                private val sessionPrefix: String,
                                private val fileName: String,
                                private val fileExt: String = ".txt",
                                private val fileNameProvider: FileNameProvider = getDefaultFileNameProvider(),
                                private var counter: Int = 0) : StreamProvider {

    companion object {
        fun getDefaultFileNameProvider(): FileNameProvider {
            return object : FileNameProvider {
                override fun getFileName(internalCounter: Int, sessionPrefix: String, fileName: String, fileExt: String): String {
                    return "${sessionPrefix}_${fileName}_$internalCounter$fileExt"
                }

            }
        }
    }

    interface FileNameProvider {
        fun getFileName(internalCounter: Int, sessionPrefix: String, fileName: String, fileExt: String): String
    }

    override fun getFileOutputStream(): FileOutputStream {
        counter++
        return context.openFileOutput(fileNameProvider.getFileName(counter, sessionPrefix, fileName, fileExt)
                , Context.MODE_PRIVATE)
    }
}

