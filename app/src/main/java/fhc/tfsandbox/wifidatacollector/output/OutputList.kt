package fhc.tfsandbox.wifidatacollector.output

class OutputList<in T>(private val listOutputListener: ListOutputListener<T>,
                       private val outputFrequency: Int = 10) {
    private val dataList = arrayListOf<T>()

    fun add(element: T) {
        dataList.add(element)
        checkIfShouldOutput(dataList.size)
    }

    private fun checkIfShouldOutput(count: Int) {
        if (count >= outputFrequency) {
            listOutputListener.outputData(dataList.clone() as List<T>)
            dataList.clear()
        }
    }
}