package fhc.tfsandbox.wifidatacollector.output

import com.google.gson.Gson
import fhc.tfsandbox.wifidatacollector.common.debugPrint
import fhc.tfsandbox.wifidatacollector.data.WifiScanResult
import java.io.OutputStreamWriter

interface ListOutputListener<in T> {
    fun outputData(data: List<T>)
}

abstract class ListOutputStreamWriter<in T>(private val streamProvider: StreamProvider) : ListOutputListener<T> {

    override fun outputData(data: List<T>) {
        OutputStreamWriter(streamProvider.getFileOutputStream()).use { writer ->
            write(writer, data)
        }
    }

    abstract protected fun write(writer: OutputStreamWriter, data: List<T>)
}

class WifiFileOutputWriter(streamProvider: StreamProvider) :
        ListOutputStreamWriter<WifiScanResult>(streamProvider) {
    override fun write(writer: OutputStreamWriter, data: List<WifiScanResult>) {
        val gson = Gson()
        "Writer received ${data.size} of data".debugPrint()
        writer.write(gson.toJson(data))
    }
}

