package fhc.tfsandbox.wifidatacollector.data

data class WifiStateData(val bssid: String, val ssid: String, val rssi: Int, val label: Int)

data class WifiScanResult(val wifiStateData: List<WifiStateData>, val timeStamp: Long = System.currentTimeMillis())
