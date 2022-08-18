package com.agvahealthcare.ventilator_ext.logs.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.agvahealthcare.ventilator_ext.database.AgVaDatabase
import com.agvahealthcare.ventilator_ext.database.entities.AlarmDBModel
import com.agvahealthcare.ventilator_ext.database.repository.AlarmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AlarmRepository

    init {
        val alarmDao = AgVaDatabase.getInstance(application).alarmDao()
        repository= AlarmRepository(alarmDao)
    }

    fun readAllAlarms(): LiveData<List<AlarmDBModel>> {
        return repository.readAllAlarmsData()
        /*return runBlocking {
            val defferedEventList = viewModelScope.async {
                repository.readAllData()
            }

            val eventList = defferedEventList.await()
            eventList

        }*/
    }

    fun addAlarm(alarmDBModel: AlarmDBModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addAlarmData(alarmDBModel)
        }
    }

//    fun updateEvent(alarmDataModel: AlarmDataModel) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.updateEventData(eventDataModel)
//        }
//    }
//
//    fun deleteEvent(eventDataModel: EventDataModel) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.deleteEventData(eventDataModel)
//        }
//    }
//
//    fun deleteAllEvent() {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.deleteAllEvents()
//        }
//    }

}