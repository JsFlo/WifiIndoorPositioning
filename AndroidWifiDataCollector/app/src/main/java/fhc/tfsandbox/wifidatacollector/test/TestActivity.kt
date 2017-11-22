package fhc.tfsandbox.wifidatacollector.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fhc.tfsandbox.wifidatacollector.MyApplication
import fhc.tfsandbox.wifidatacollector.R
import fhc.tfsandbox.wifidatacollector.common.debugPrint
import fhc.tfsandbox.wifidatacollector.data.WifiScanResult
import fhc.tfsandbox.wifidatacollector.receiver.WifiScanResultsBroadcastReceiver
import kotlinx.android.synthetic.main.activity_test.*
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

class TestActivity : AppCompatActivity(), WifiScanResultsBroadcastReceiver.WifiScanResultsListener {

    private lateinit var wifiScanReceiver: WifiScanResultsBroadcastReceiver
    private lateinit var tfInference: TensorFlowInferenceInterface
    private lateinit var roomLabels: Array<String>

    @SuppressLint("WifiManagerLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        roomLabels = resources.getStringArray(R.array.room_labels)

        // Get a wifi scan receiver
        wifiScanReceiver = WifiScanResultsBroadcastReceiver(this, (application as MyApplication).wifiManager,
                this, false)

        tfInference = TensorFlowInferenceInterface(assets, "wifi_position.pb")

        scan_button.setOnClickListener {
            scan_button.isEnabled = false
            wifiScanReceiver.startScanning()
        }
    }

    override fun onWifiScanResult(wifiScanResult: WifiScanResult) {
        val modelInput = ModelInput.getModelInput(wifiScanResult)
        val inputString = StringBuilder()
        modelInput.toFormattedOutput().forEach { inputString.append("$it, ") }
        inputString.toString().debugPrint()
        input_et.text = inputString.toString()
        //feed
        tfInference.feed("export_input", modelInput)

        // fetch
        val floatOutputs = FloatArray(4)
        tfInference.run(arrayOf("export_output"))
        tfInference.fetch("export_output", floatOutputs)

        val labelIndex = floatOutputs.indexOfFirst { it > 0 }
        output_et.text = roomLabels[labelIndex]

        scan_button.isEnabled = true
    }

    private fun TensorFlowInferenceInterface.feed(name: String, modelInput: ModelInput) {
        return feed(name, modelInput.toFormattedOutput(), *(reshapeToLongArray(intArrayOf(modelInput.shape.rows, modelInput.shape.columns))))
    }

    private fun reshapeToLongArray(intArray: IntArray) = intArray.map { it.toLong() }.toLongArray()
}
