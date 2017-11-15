import com.google.gson.Gson
import data.WifiScanResult
import java.io.File

fun readAndParse(filePaths: String, wifiScanListener: (Array<WifiScanResult>) -> Unit, gson: Gson = Gson()) {
    File(filePaths).walk().filter { !it.isDirectory }.forEach {
        wifiScanListener(parseWifiScanResults(gson, readFileAsString(it)))
    }
}

private fun parseWifiScanResults(gson: Gson, rawString: String): Array<WifiScanResult> =
        gson.fromJson(rawString, Array<WifiScanResult>::class.java)

private fun readFileAsString(file: File): String =
        file.inputStream().bufferedReader().use { it.readText() }

