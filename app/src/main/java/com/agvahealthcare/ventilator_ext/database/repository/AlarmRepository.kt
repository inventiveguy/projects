package com.agvahealthcare.ventilator_ext.database.repository

import androidx.lifecycle.LiveData
import com.agvahealthcare.ventilator_ext.database.daos.AlarmDao
import com.agvahealthcare.ventilator_ext.database.entities.AlarmDBModel


class AlarmRepository(private val alarmDao: AlarmDao) {

    fun readAllAlarmsData(): LiveData<List<AlarmDBModel>> = alarmDao.readAllAlarms()

    suspend fun addAlarmData(alarmDBModel: AlarmDBModel) {
        alarmDao.addAlarmData(alarmDBModel)
    }



}