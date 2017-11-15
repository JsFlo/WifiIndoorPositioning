import data.WifiScanResult

public fun filterData(trainDataFilePath: String, listOfBssidsChosen: Set<String>): List<WifiScanResult> {
    val filteredResults = mutableListOf<WifiScanResult>()
    readAndParse(trainDataFilePath, { results ->

        filteredResults.addAll(
                results.map {
                    val filteredList = it.wifiStateData.filter {
                        listOfBssidsChosen.contains(it.bssid)
                    }
                    WifiScanResult(filteredList, it.timeStamp, it.label)
                }.filter {
                    // make sure we don't have a scan result with out any data
                    // sizes should match (only scan results which have values for all in list passed in)
                    !it.wifiStateData.isEmpty() &&
                            it.wifiStateData.size == listOfBssidsChosen.size
                })
    })
    return filteredResults.toList()
}