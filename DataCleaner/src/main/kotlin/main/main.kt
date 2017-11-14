package main

import filterData
import getCountMap
import java.io.File
import java.io.PrintWriter

private const val TRAIN_DATA_FILE_PATH = "./src/main.main/resources/file/"
private const val COUNT_MAP_OUTPUT_FILE_PATH = "./src/main.main/out/results.txt"
private const val FILTERED_DATA_OUTPUT_FILE_PATH = "./src/main.main/filtered_out/filtered_data_set"

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

public class App {
    companion object {
        @JvmStatic
        public fun main(args: Array<String>) {

            val needToSelectBssids = listOfBssidsChosen.isEmpty()

            // write the count map if we need to handpick out bssids
            if (needToSelectBssids) {
                val sortedResultMap = getCountMap(TRAIN_DATA_FILE_PATH)
                writeToFile(COUNT_MAP_OUTPUT_FILE_PATH, {
                    sortedResultMap.forEach { k, v -> it.println("Key: $k --- Value: $v  \n") }
                })
            }

            // filter data
            val filteredData = filterData(TRAIN_DATA_FILE_PATH, listOfBssidsChosen)
            println("Finished!")
        }
    }
}

private fun writeToFile(outputFileRelativeFilePath: String, write: (PrintWriter) -> Unit) {
    val file = File(outputFileRelativeFilePath)
    file.parentFile.mkdirs()
    file.createNewFile()
    file.printWriter().use {
        write(it)
    }
}
