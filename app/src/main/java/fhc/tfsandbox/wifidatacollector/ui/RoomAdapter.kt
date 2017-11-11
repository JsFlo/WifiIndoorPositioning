package fhc.tfsandbox.wifidatacollector.ui

import android.content.Context
import android.widget.ArrayAdapter


data class RoomCounterViewModel(val name: String, var counter: Int = 0) {
    override fun toString(): String {
        return "$name: $counter"
    }
}

class RoomAdapter(context: Context, rooms: List<RoomCounterViewModel>)
    : ArrayAdapter<RoomCounterViewModel>(context, android.R.layout.simple_spinner_item, rooms) {


    companion object {
        fun getRoomAdapter(context: Context, roomsStringArray: Array<String>): RoomAdapter {
            return RoomAdapter(context, roomsStringArray.map { RoomCounterViewModel(it) })
        }
    }

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    fun incrementCounter(position: Int) {
        getItem(position).counter += 1
        notifyDataSetChanged()
    }
}