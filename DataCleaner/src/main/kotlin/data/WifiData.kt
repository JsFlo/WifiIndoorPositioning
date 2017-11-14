package data

data class WifiStateData(val bssid: String, val ssid: String, val rssi: Int, var label: Int = -1)

data class WifiScanResult(val wifiStateData: List<WifiStateData>, val timeStamp: Long = System.currentTimeMillis())

