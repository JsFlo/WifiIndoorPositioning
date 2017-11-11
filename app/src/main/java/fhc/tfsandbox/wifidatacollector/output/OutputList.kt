package fhc.tfsandbox.wifidatacollector.output

class OutputList<in T>(private val listOutputListeners: List<ListOutputListener<T>>,
                       private val outputFrequency: Int = 10) {
    private val dataList = arrayListOf<T>()

    fun add(element: T) {
        dataList.add(element)
        checkIfShouldOutput(dataList.size)
    }

    private fun checkIfShouldOutput(count: Int) {
        if (count >= outputFrequency) {
            val clonedDataList = dataList.clone()
            listOutputListeners.forEach { it.outputData(clonedDataList as List<T>) }
            dataList.clear()
        }
    }
}