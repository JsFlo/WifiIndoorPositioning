import com.google.gson.Gson
import data.WifiScanResult
import java.io.File


const val TRAIN_DATA_FILE_PATH = "./src/main/resources/file/"

data class ScanResultStats(val bssid: String, val ssid: String)

fun main(args: Array<String>) {
    val gson = Gson()
    File(TRAIN_DATA_FILE_PATH).walk().filter { !it.isDirectory }.forEach {
        onWifiScanResults(parseWifiScanResults(gson, readFileAsString(it)))
    }
}

fun onWifiScanResults(wifiScanResults: Array<WifiScanResult>) {
    val scanResultsMap = hashMapOf<ScanResultStats, Int>()

    wifiScanResults.flatMap {
        it.wifiStateData.map { ScanResultStats(it.bssid, it.ssid) }
    }.forEach {
        if (scanResultsMap.containsKey(it)) {
            scanResultsMap[it] = scanResultsMap[it]!!.plus(1)
        } else {
            scanResultsMap.put(it, 1)
        }
    }

    val sortedMap = scanResultsMap.toList().sortedBy { (key, value) -> -value }.toMap()

    writeToFile(sortedMap)
}

fun writeToFile(sortedMap: Map<ScanResultStats, Int>) {
    sortedMap.forEach { k, v -> println("Key: $k --- Value: $v  \n") }
}

fun parseWifiScanResults(gson: Gson, rawString: String): Array<WifiScanResult> =
        gson.fromJson(rawString, Array<WifiScanResult>::class.java)

fun readFileAsString(file: File): String =
        file.inputStream().bufferedReader().use { it.readText() }