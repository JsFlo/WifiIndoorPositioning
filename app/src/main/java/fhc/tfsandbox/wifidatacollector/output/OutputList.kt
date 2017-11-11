package fhc.tfsandbox.wifidatacollector.output

/**
 * Collection wrapper that only allows adding one element at a time.
 * Returns an array list of [T] when the list size reaches [outputFrequency]
 *
 * @param T data type used in internal ArrayList
 * @property listOutputListeners callback interfaces that will receive the array list of [T]
 * @property outputFrequency max list size, will trigger a callback to [listOutputListeners]
 */
class OutputList<in T>(private val listOutputListeners: List<ListOutputListener<T>>,
                       private val outputFrequency: Int = 10) {
    private val dataList = arrayListOf<T>()

    fun add(element: T) {
        dataList.add(element)
        checkIfShouldOutput(dataList.size)
    }

    fun clear() {
        dataList.clear()
    }

    fun size(): Int {
        return dataList.size
    }

    private fun checkIfShouldOutput(count: Int) {
        if (count >= outputFrequency) {
            val clonedDataList = dataList.clone()
            listOutputListeners.forEach { it.outputData(clonedDataList as List<T>) }
            dataList.clear()
        }
    }
}