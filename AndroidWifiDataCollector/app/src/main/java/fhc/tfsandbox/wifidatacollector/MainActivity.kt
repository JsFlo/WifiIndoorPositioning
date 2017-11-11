package fhc.tfsandbox.wifidatacollector

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fhc.tfsandbox.wifidatacollector.data.WifiScanResult
import fhc.tfsandbox.wifidatacollector.output.FileCounterStreamProvider
import fhc.tfsandbox.wifidatacollector.output.ListOutputListener
import fhc.tfsandbox.wifidatacollector.output.OutputList
import fhc.tfsandbox.wifidatacollector.output.WifiFileOutputWriter
import fhc.tfsandbox.wifidatacollector.receiver.WifiScanResultsBroadcastReceiver
import fhc.tfsandbox.wifidatacollector.ui.RoomAdapter
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Registers a [WifiScanResultsBroadcastReceiver] which returns a single [WifiScanResult] periodically while scanning.
 *
 * The [WifiScanResult] is added to the [OutputList].
 *
 * When the [OutputList] reaches a certain size([BATCH_SIZE]) it returns a List<[WifiScanResult]> through
 * a [ListOutputListener]
 *
 *  We register 2 [ListOutputListener] with our [OutputList]:
 *   [WifiFileOutputWriter]: Writes out to a file
 *   [MainActivity]: Updates UI counters
 *
 */
class MainActivity : AppCompatActivity(),
        ListOutputListener<WifiScanResult>,
        WifiScanResultsBroadcastReceiver.WifiScanResultsListener {

    companion object {
        private const val BATCH_SIZE = 10
        private const val PREF_FILE_NAME = "session_prefs"
        private const val PREF_SESSION_COUNTER = "PREF_SESSION_COUNTER"
    }

    private lateinit var outputList: OutputList<WifiScanResult>
    private lateinit var wifiScanReceiver: WifiScanResultsBroadcastReceiver
    private lateinit var roomAdapter: RoomAdapter

    @SuppressLint("WifiManagerLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViews()

        // session prefix used for the file names
        val prefs = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val sessionPrefix = prefs.getInt(PREF_SESSION_COUNTER, 0)
        prefs.edit().putInt(PREF_SESSION_COUNTER, sessionPrefix + 1).apply()

        // provides the file output stream and handles naming the files
        val fileCounterStreamProvider = FileCounterStreamProvider(this,
                sessionPrefix.toString(), "debug_test", fileExt = ".json")
        // uses a stream provider to write to the files provided
        val fileWriter = WifiFileOutputWriter(fileCounterStreamProvider)
        // list that will output a list every time the capacity reaches the batch size
        outputList = OutputList(listOf(fileWriter, this), BATCH_SIZE)

        // Get a wifi scan receiver
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiScanReceiver = WifiScanResultsBroadcastReceiver(this, wifiManager, this)

        // setup start and stop button
        start_button.setOnClickListener {
            wifiScanReceiver.startScanning()
        }
        stop_button.setOnClickListener {
            outputList.clear()
            wifiScanReceiver.stopScanning()
            updateBatchSizeProgressUi(outputList.size())
        }
    }

    override fun onStop() {
        super.onStop()
        wifiScanReceiver.stopScanning()
    }

    private fun setupViews() {
        updateBatchSizeProgressUi(0)

        val rooms = resources.getStringArray(R.array.room_labels)
        roomAdapter = RoomAdapter.getRoomAdapter(this, rooms)
        label_spinner.adapter = roomAdapter
    }

    // one training row (one scan)
    override fun onWifiScanResult(wifiScanResult: WifiScanResult) {
        // Add the label to the all the data coming in
        wifiScanResult.wifiStateData.map { it.label = label_spinner.selectedItemPosition + 1 }
        // add to the output list
        outputList.add(wifiScanResult)

        updateBatchSizeProgressUi(outputList.size())
    }

    // file has been written
    override fun outputData(data: List<WifiScanResult>) {
        roomAdapter.incrementCounter(label_spinner.selectedItemPosition)
    }

    private fun updateBatchSizeProgressUi(batchProgress: Int) {
        batch_size_tv.text = "$batchProgress/$BATCH_SIZE"
    }

}
