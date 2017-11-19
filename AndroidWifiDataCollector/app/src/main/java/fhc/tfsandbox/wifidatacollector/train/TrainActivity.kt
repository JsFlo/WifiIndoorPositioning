package fhc.tfsandbox.wifidatacollector.train

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fhc.tfsandbox.wifidatacollector.R
import fhc.tfsandbox.wifidatacollector.data.WifiScanResult
import fhc.tfsandbox.wifidatacollector.train.output.FileCounterStreamProvider
import fhc.tfsandbox.wifidatacollector.train.output.ListOutputListener
import fhc.tfsandbox.wifidatacollector.train.output.OutputList
import fhc.tfsandbox.wifidatacollector.train.output.WifiFileOutputWriter
import fhc.tfsandbox.wifidatacollector.receiver.WifiScanResultsBroadcastReceiver
import fhc.tfsandbox.wifidatacollector.ui.RoomAdapter
import kotlinx.android.synthetic.main.activity_train.*

/**
 * Registers a [WifiScanResultsBroadcastReceiver] which returns a single [WifiScanResult] periodically while scanning.
 *
 * The [WifiScanResult] is added to the [OutputList].
 *
 * When the [OutputList] reaches a certain size([BATCH_SIZE]) it returns a List<[WifiScanResult]> through
 * a [ListOutputListener]
 *
 *  We register (1) [ListOutputListener] with our [OutputList]:
 *   [WifiFileOutputWriter]: Writes out to a file
 *
 */
class TrainActivity : AppCompatActivity(),
        WifiScanResultsBroadcastReceiver.WifiScanResultsListener, FileCounterStreamProvider.FileNameProvider {

    companion object {
        private const val BATCH_SIZE = 1000
        private const val PREF_FILE_NAME = "session_prefs"
        private const val PREF_SESSION_COUNTER = "PREF_SESSION_COUNTER"
    }

    private lateinit var outputList: OutputList<WifiScanResult>
    private lateinit var wifiScanReceiver: WifiScanResultsBroadcastReceiver
    private lateinit var roomAdapter: RoomAdapter

    @SuppressLint("WifiManagerLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)
        setupViews()

        // session prefix used for the file names
        val prefs = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val sessionPrefix = prefs.getInt(PREF_SESSION_COUNTER, 0)
        prefs.edit().putInt(PREF_SESSION_COUNTER, sessionPrefix + 1).apply()

        // provides the file output stream and handles naming the files
        val fileCounterStreamProvider = FileCounterStreamProvider(this,
                sessionPrefix.toString(), "wifi_train_data", fileExt = ".json", fileNameProvider = this)
        // uses a stream provider to write to the files provided
        val fileWriter = WifiFileOutputWriter(fileCounterStreamProvider)
        // list that will output a list every time the capacity reaches the batch size
        outputList = OutputList(listOf(fileWriter), BATCH_SIZE)

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
        // Add the label to the scan coming in
        wifiScanResult.label = label_spinner.selectedItemPosition + 1
        // add to the output list
        outputList.add(wifiScanResult)

        updateBatchSizeProgressUi(outputList.size())
    }

    override fun getFileName(internalCounter: Int, sessionPrefix: String, fileName: String, fileExt: String): String {
        roomAdapter.incrementCounter(label_spinner.selectedItemPosition)
        return "${sessionPrefix}_${fileName}_label_${label_spinner.selectedItemPosition}_$internalCounter$fileExt"
    }

    private fun updateBatchSizeProgressUi(batchProgress: Int) {
        batch_size_tv.text = "$batchProgress/${BATCH_SIZE}"
    }

}
