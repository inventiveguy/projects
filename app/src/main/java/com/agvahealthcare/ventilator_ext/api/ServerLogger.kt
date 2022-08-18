package com.agvahealthcare.ventilator_ext.api

import android.content.Context
import android.util.Log
import androidx.work.*
import com.agvahealthcare.ventilator_ext.VentilatorApp
import com.agvahealthcare.ventilator_ext.api.model.alarmDataModel.Ack
import com.agvahealthcare.ventilator_ext.api.model.alarmDataModel.AlarmRequestBodyModel
import com.agvahealthcare.ventilator_ext.api.model.datamodel.LogType
import com.agvahealthcare.ventilator_ext.api.model.datamodel.RequestBodyModel
import com.agvahealthcare.ventilator_ext.api.services.AlarmUploadWorker
import com.agvahealthcare.ventilator_ext.api.services.ScheduleUpload
import com.agvahealthcare.ventilator_ext.logging.FileLogger
import com.agvahealthcare.ventilator_ext.utility.utils.AppUtils
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import com.google.gson.Gson
import java.util.concurrent.TimeUnit


class ServerLogger {

    companion object{
        fun sendAlarm(ctx: Context, ackValue: String){
            var messg = Configs.MessageFactory.getAckMessage(ctx, ackValue)
            if (messg == null ){
                messg=""
            }
            var alarmRequestBodyModel= AlarmRequestBodyModel()
            alarmRequestBodyModel.did=AppUtils.getMacAddress()
            alarmRequestBodyModel.type="002"  // ToDO : add to build config
            alarmRequestBodyModel.ack.add(Ack(messg,ackValue,AppUtils.getCurrentDateReverse()))
            sendAlarmRequest(alarmRequestBodyModel)
        }
        fun d(ctx: Context, err: Throwable,filename:String) = d(ctx, err.stackTraceToString(),filename)

        fun d(ctx: Context, data: String,filename: String) {
            val requestBodyModel=RequestBodyModel()
            requestBodyModel.apply{
                log?.msg=data
                log?.file=filename
                log?.type= LogType.DEBUG.value
               }
            apiRequest(ctx, requestBodyModel)
        }

        fun i(ctx: Context, err: Throwable,filename: String) = i(ctx, err.stackTraceToString(),filename)

        fun i(ctx: Context, data: String,filename: String) {
            val requestBodyModel=RequestBodyModel()
            requestBodyModel.apply{
                log?.msg=data
                log?.file=filename
                log?.type= LogType.INFO.value
            }
            apiRequest(ctx, requestBodyModel)
        }

        fun e(ctx: Context, err: Throwable,filename: String) = e(ctx, err.stackTraceToString(),filename)

        fun e(ctx: Context, data: String,filename: String) {
            val requestBodyModel=RequestBodyModel()
            requestBodyModel.apply{
                log?.msg=data
                log?.file=filename
                log?.type= LogType.ERROR.value
            }
            apiRequest(ctx, requestBodyModel)
        }

        fun w(ctx: Context, err: Throwable,filename: String) = w(ctx, err.stackTraceToString(),filename)

        fun w(ctx: Context, data: String,filename: String) {

            val requestBodyModel=RequestBodyModel()
            requestBodyModel.apply{
                log?.msg=data
                log?.file=filename
                log?.type= LogType.WARN.value
            }
            apiRequest(ctx, requestBodyModel)
        }




        private fun apiRequest(ctx: Context, logRequestBodyModel: RequestBodyModel) {
           try{
               Log.i("SERVER_CHECK", "Request initiated")
               sendRequest(logRequestBodyModel)
           } catch(e: Exception){
               Log.i("SERVER_CHECK", "Unable to request")

               e.printStackTrace()
               FileLogger.d(ctx, e)
           }
        }

        private fun sendAlarmRequest(alarmRequestBodyModel:AlarmRequestBodyModel){
            val ctx=VentilatorApp.getInstance()?.applicationContext
            val dataBody=Gson().toJson(alarmRequestBodyModel)
            val sendData:Data = Data.Builder()
                .putString("DATABODY",dataBody)
                .build()
            val constraints:Constraints=Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
            val scheduleAlarmUpload:WorkRequest= OneTimeWorkRequestBuilder<AlarmUploadWorker>()
                .setInputData(sendData)
                .setInitialDelay(10,TimeUnit.SECONDS)
                .setConstraints(constraints)
                .build()
            ctx?.let {
                val workManagerAlarm=WorkManager.getInstance(it)
                workManagerAlarm.apply {
                    enqueue(scheduleAlarmUpload)
                }

            }
        }
        @Throws(Exception::class)
        private fun sendRequest(requestBodyModel: RequestBodyModel) {
            val ctx= VentilatorApp.getInstance()?.applicationContext
            val task=Gson().toJson(requestBodyModel)
            val inputData: Data = Data.Builder()
                .putString("TASK", task)
                .putString("REQUESTTYPE","error")
                .build()
            val constraints: Constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
            val scheduleUpload: WorkRequest = OneTimeWorkRequestBuilder<ScheduleUpload>()
                                                    .setInputData(inputData)
                                                    .setInitialDelay(10, TimeUnit.SECONDS)
                                                    .setConstraints(constraints)
                                                    .build()
           ctx?.let {
               val workManager = WorkManager.getInstance(it)
               workManager.apply {
                   enqueue(scheduleUpload)
               }
           }

        }
    }
}