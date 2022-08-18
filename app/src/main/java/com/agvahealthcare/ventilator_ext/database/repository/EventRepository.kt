package com.agvahealthcare.ventilator_ext.database.repository

import androidx.lifecycle.LiveData
import com.agvahealthcare.ventilator_ext.database.daos.EventDao
import com.agvahealthcare.ventilator_ext.database.entities.EventDataModel

// User Repository abstracts access to multiple data sources. However this is not the part of the Architecture Component libraries.

class EventRepository(private val eventDao: EventDao) {

    fun readAllData(): LiveData<List<EventDataModel>> = eventDao.readAllEvents()

    suspend fun addEventData(eventDataModel: EventDataModel) {
         eventDao.addEventData(eventDataModel)
    }

    suspend fun updateEventData(eventDataModel: EventDataModel) {
            eventDao.updateEventData(eventDataModel)
    }

    suspend fun deleteEventData(eventDataModel: EventDataModel) {
            eventDao.deleteEventData(eventDataModel)
    }

    suspend fun deleteAllEvents() {
            eventDao.deleteAllEvents()
    }

}