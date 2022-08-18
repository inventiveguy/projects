package com.agvahealthcare.ventilator_ext.logs.event

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.agvahealthcare.ventilator_ext.database.AgVaDatabase
import com.agvahealthcare.ventilator_ext.database.entities.EventDataModel
import com.agvahealthcare.ventilator_ext.database.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EventRepository

    init {
        val eventDao = AgVaDatabase.getInstance(application).eventDao()
        repository = EventRepository(eventDao)
    }

    fun readAllEvents(): LiveData<List<EventDataModel>> {
        return repository.readAllData()
    }

    fun addEvent(eventDataModel: EventDataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEventData(eventDataModel)
        }
    }

    fun updateEvent(eventDataModel: EventDataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEventData(eventDataModel)
        }
    }

    fun deleteEvent(eventDataModel: EventDataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEventData(eventDataModel)
        }
    }

    fun deleteAllEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllEvents()
        }
    }

}

/*return runBlocking {
           val defferedEventList = viewModelScope.async {
               repository.readAllData()
           }

           val eventList = defferedEventList.await()
           eventList

       }*/