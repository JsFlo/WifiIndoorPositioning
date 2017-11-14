import data.WifiScanResult

data class ScanResultStats(val bssid: String, val ssid: String)

fun getCountMap(trainingDataFilePath: String): Map<ScanResultStats, Int> {

    val scanResultsMap = hashMapOf<ScanResultStats, Int>()
    readAndParse(trainingDataFilePath, { arrayOfWifiScanResults ->
        addOrUpdateMap(scanResultsMap, arrayOfWifiScanResults)
    })
    return scanResultsMap.toList().sortedBy { (_, value) -> -value }.toMap()
}

private fun addOrUpdateMap(scanResultsMap: HashMap<ScanResultStats, Int>, wifiScanResults: Array<WifiScanResult>) {
    wifiScanResults.flatMap {
        it.wifiStateData.map { ScanResultStats(it.bssid, it.ssid) }
    }.forEach {
        if (scanResultsMap.containsKey(it)) {
            scanResultsMap[it] = scanResultsMap[it]!!.plus(1)
        } else {
            scanResultsMap.put(it, 1)
        }
    }
}
