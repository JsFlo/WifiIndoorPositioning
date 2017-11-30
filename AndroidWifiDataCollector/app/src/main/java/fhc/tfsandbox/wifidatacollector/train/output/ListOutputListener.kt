package fhc.tfsandbox.wifidatacollector.train.output

import com.google.gson.Gson
import fhc.tfsandbox.wifidatacollector.models.WifiScanResult
import java.io.OutputStreamWriter

interface ListOutputListener<in T> {
    fun outputData(data: List<T>)
}

/**
 * Extends the [ListOutputListener] to open a stream provided by [streamProvider] and exposes
 * the write method [write].
 *
 * @property streamProvider Stream Provider that will provide the file output stream
 */
abstract class ListOutputStreamWriter<in T>(private val streamProvider: StreamProvider) : ListOutputListener<T> {

    override fun outputData(data: List<T>) {
        OutputStreamWriter(streamProvider.getFileOutputStream()).use { writer ->
            write(writer, data)
        }
    }

    abstract protected fun write(writer: OutputStreamWriter, data: List<T>)
}

/**
 * Extends the [ListOutputStreamWriter] and writes a List<[WifiScanResult]> using [Gson]
 *
 * @param streamProvider Stream Provider that will provide the file output stream used to write
 */
class WifiFileOutputWriter(streamProvider: StreamProvider) : ListOutputStreamWriter<WifiScanResult>(streamProvider) {
    private val gson = Gson()
    override fun write(writer: OutputStreamWriter, data: List<WifiScanResult>) = writer.write(gson.toJson(data))
}

