package com.agvahealthcare.ventilator_ext.api.services

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.agvahealthcare.ventilator_ext.logging.DataLogger

class ScheduleDataBaseUpdate (private val context: Context, private val workerParameters: WorkerParameters)
    :Worker(context, workerParameters){
    override fun doWork(): Result {
        val dataLogger=DataLogger(context)
        dataLogger.deleteDataFromSqlite()
        return Result.success()
    }
}