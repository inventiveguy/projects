package com.agvahealthcare.ventilator_ext.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

import com.agvahealthcare.ventilator_ext.model.AlarmModel
import com.agvahealthcare.ventilator_ext.model.DataStoreModel
import java.util.*
import kotlin.collections.ArrayList

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

class DashBoardViewModel(application: Application) : AndroidViewModel(application) {
    //private val repository: EventRepository
    /*init {
        val eventDao = AgVaDatabase.getInstance(application).eventDao()
        repository= EventRepository(eventDao)
    }*/

    //setter of the ackList
    val alarms = MutableLiveData(arrayListOf<AlarmModel>())
    fun addAlarm(alarm: AlarmModel){
        alarms.value?.let {
            it.add(alarm)
            Collections.sort(it, PriorityComparator())
        }
        alarms.notifyObserver()
    }

    fun removeAlarm(alarm: AlarmModel){
        alarms.value?.remove(alarm)
        alarms.notifyObserver()
    }


    //setter of the selected value
    val selected=MutableLiveData<Int>()
    fun select(v:Int){
        selected.value=v
        Log.d("the_value", selected.value.toString())
    }

    val numberOfDataEntries=MutableLiveData<Int>()
    fun numbrDataEntry(v:Int){
        numberOfDataEntries.value=v
        Log.d("value",numberOfDataEntries.value.toString())
    }
    val entryInflatedCounter=MutableLiveData<Int>()
    fun entryInflatCountr(v:Int){
        entryInflatedCounter.value=v
        Log.d("value",entryInflatedCounter.value.toString())
    }
    //scoped up values for the information regarding Battery
    val ventBatteryLevel : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    fun setVentBatteryLevel(batteryLevel: Int) {
        ventBatteryLevel.value = batteryLevel
    }

    val ventBatteryHealth : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    fun setVentBatteryHealth(ventBattHealth: Int) {
        ventBatteryHealth.value = ventBattHealth
    }

    val ventBatteryRemainingTime : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    fun setVentBatteryRemainingTime(ventBR: Int) {
        ventBatteryRemainingTime.value = ventBR
    }
    val isBatteryConnected=MutableLiveData<Boolean>()
    fun setBAtteryConnectedFlag(isConnected:Boolean){
        isBatteryConnected.value=isConnected
    }
    val breathData=MutableLiveData<DataStoreModel>()
    fun setBreathData(model: DataStoreModel){
        breathData.value=model
    }
    //values required for the LogsTableFragment
    val dataStoreList=MutableLiveData<ArrayList<DataStoreModel>>()
    fun updateDataStoreList(list: ArrayList<DataStoreModel>){
        dataStoreList.value=list
    }
    val listOfDataStoreList = MutableLiveData<ArrayList<ArrayList<DataStoreModel>>>()
    fun updateListOfDataStoreList(list: ArrayList<ArrayList<DataStoreModel>>){
        listOfDataStoreList.value=list
    }




    class PriorityComparator : Comparator<AlarmModel> {
        override fun compare(
            o1: AlarmModel,
            o2: AlarmModel
        ): Int {
            return o2.priority.compareTo(o1.priority)
        }
    }
}