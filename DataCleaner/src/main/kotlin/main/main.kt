package main

import data.WifiScanResult
import filterData
import getCountMap
import java.io.File
import java.io.PrintWriter
import java.util.*

private const val TRAIN_DATA_FILE_PATH = "./src/main/resources/file/"
private const val COUNT_MAP_OUTPUT_FILE_PATH = "./src/main/out/results.txt"

private const val FILTERED_TRAIN_DATA_FEATURES_PATH = "./src/main/filtered_out/features/train_features"
private const val FILTERED_TRAIN_DATA_LABELS_PATH = "./src/main/filtered_out/labels/train_labels"

private const val FILTERED_TEST_DATA_FEATURES_PATH = "./src/main/filtered_out/features/test_features"
private const val FILTERED_TEST_DATA_LABELS_PATH = "./src/main/filtered_out/labels/test_labels"

private const val PERCENT_OF_TEST_DATA = .20

// Hand picked after writing the count map
val listOfBssidsChosen =
        setOf(
                "48:5d:36:53:30:10",
                "00:7f:28:ca:6a:1f",
                "48:5d:36:4a:20:22",
                "c8:a7:0a:ae:e7:16",
                "c8:a7:0a:ae:e7:14",
                "48:5d:36:4a:20:24",
                "c4:04:15:4a:16:3a",
                "48:5d:36:53:30:0e")


public fun main(args: Array<String>) {

    val needToSelectBssids = listOfBssidsChosen.isEmpty()

    // write the count map if we need to handpick out bssids
    if (needToSelectBssids) {
        val sortedResultMap = getCountMap(TRAIN_DATA_FILE_PATH)
        writeToFile(COUNT_MAP_OUTPUT_FILE_PATH, {
            sortedResultMap.forEach { k, v -> it.println("Key: $k --- Value: $v  \n") }
        })
    } else {

        // filter data
        val filteredData = filterData(TRAIN_DATA_FILE_PATH, listOfBssidsChosen)

        // scramble (random sort)
        Collections.shuffle(filteredData)

        val testIndex: Int = ((filteredData.size * PERCENT_OF_TEST_DATA).toInt())

        val listOfResultList = filteredData.withIndex().groupBy { it.index < testIndex }.map { it.value.map { it.value } }
        if (listOfResultList.size == 2) {
            val testData = listOfResultList[0]
            val trainData = listOfResultList[1]

            // save to train
            formatAndSave(FILTERED_TEST_DATA_LABELS_PATH, FILTERED_TEST_DATA_FEATURES_PATH, testData)
            formatAndSave(FILTERED_TRAIN_DATA_LABELS_PATH, FILTERED_TRAIN_DATA_FEATURES_PATH, trainData)
        } else {
            println("List of grouped results does not equal 2")
        }
    }

}

fun formatAndSave(labelsPath: String, featuresPath: String, filteredData: List<WifiScanResult>) {
    writeToFile(labelsPath, { label_writer ->
        writeToFile(featuresPath, { features_writer ->
            filteredData.forEach {
                var a4810 = ""
                var a001f = ""
                var a4822 = ""
                var ac816 = ""
                var ac814 = ""
                var a4824 = ""
                var ac43a = ""
                var a480e = ""
                it.wifiStateData.forEach {
                    val rssi = it.rssi.toString()
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

                features_writer.println("$a4810, $a001f, $a4822, $ac816, $ac814, $a4824, $ac43a, $a480e".format())
                label_writer.println("${it.label}")
            }
        })
    })
}

private fun writeToFile(outputFileRelativeFilePath: String, write: (PrintWriter) -> Unit) {
    val file = File(outputFileRelativeFilePath)
    file.parentFile.mkdirs()
    file.createNewFile()
    file.printWriter().use {
        write(it)
    }
}