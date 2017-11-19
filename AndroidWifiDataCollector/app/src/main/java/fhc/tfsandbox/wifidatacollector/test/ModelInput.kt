package fhc.tfsandbox.wifidatacollector.test

import fhc.tfsandbox.wifidatacollector.data.WifiScanResult

data class ModelInput(private val a4810: Float,
                      private val a001f: Float,
                      private val a4822: Float,
                      private val ac816: Float,
                      private val ac814: Float,
                      private val a4824: Float,
                      private val ac43a: Float,
                      private val a480e: Float,
                      val shape: Shape = Shape(1, 8)) {

    companion object {
        fun getModelInput(scanResult: WifiScanResult): ModelInput {
            var a4810 = 0f
            var a001f = 0f
            var a4822 = 0f
            var ac816 = 0f
            var ac814 = 0f
            var a4824 = 0f
            var ac43a = 0f
            var a480e = 0f
            scanResult.wifiStateData.forEach {
                val rssi: Float = it.rssi.toFloat()
                when (it.bssid) {
                    "48:5d:36:53:30:10" -> a4810 = rssi
                    "00:7f:28:ca:6a:1f" -> a001f = rssi
                    "48:5d:36:4a:20:22" -> a4822 = rssi
                    "c8:a7:0a:ae:e7:16" -> ac816 = rssi
                    "c8:a7:0a:ae:e7:14" -> ac814 = rssi
                    "48:5d:36:4a:20:24" -> a4824 = rssi
                    "c4:04:15:4a:16:3a" -> ac43a = rssi
                    "48:5d:36:53:30:0e" -> a480e = rssi
                }
            }
            return ModelInput(a4810, a001f, a4822, ac816, ac814, a4824, ac43a, a480e)

        }
    }

    fun toFormattedOutput(): FloatArray {
        return arrayListOf(a4810, a001f, a4822, ac816, ac814, a4824, ac43a, a480e).toFloatArray()
    }
}

data class Shape(val rows: Int, val columns: Int)