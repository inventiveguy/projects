package com.agvahealthcare.ventilator_ext.api.services

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.agvahealthcare.ventilator_ext.BuildConfig
import com.agvahealthcare.ventilator_ext.api.LoggerApiService
import com.agvahealthcare.ventilator_ext.api.model.datamodel.RequestBodyModel
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ScheduleUpload (context: Context, workerParameters: WorkerParameters): Worker(context,workerParameters) {
    var workStatus=0

    override fun doWork(): Result {
        var data=inputData.getString("TASK")
        var requestBodyModel=Gson().fromJson(data,RequestBodyModel::class.java)
        return networkCallForUpload(requestBodyModel)
    }
    private fun networkCallForUpload(requestBodyModel: RequestBodyModel): Result{
        val okHttpClient= OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100,TimeUnit.SECONDS).build()
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),"\n{\n    \"version\" : \"${requestBodyModel.version}\",\n    \"type\" : \"${requestBodyModel.type}\",\n    \"log\" : {\n        \"file\" : \"${requestBodyModel.log?.file}\",\n        \"date\" : \"${requestBodyModel.log?.date}\",\n        \"msg\" : \"${requestBodyModel.log?.msg}\",\n        \"type\" : \"error\"\n    },\n    \"device\" : {\n        \"did\" : \"${requestBodyModel.device?.did}\",\n        \"name\" : \"${requestBodyModel.device?.name}\",\n        \"manufacturer\" : \"${requestBodyModel.device?.manufacturer}\", \n        \"battery\" : null,\n        \"os\" : {\n            \"name\" : \"${requestBodyModel.device?.os?.name}\",\n            \"type\" : \"${requestBodyModel.device?.os?.type}\"\n        }\n    }\n}\n")

        val retrofit= Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service = retrofit.create(LoggerApiService::class.java)
         try {
            val response = service.updateServerWithCrash(requestBodyModel).execute()
            Log.d("responseerror",response.message())
            return if (response.isSuccessful) Result.success() else Result.failure()
        } catch (e: Exception){
            e.printStackTrace()
             Log.d("responseerror",e.printStackTrace().toString())
             return Result.failure()
        }
    }


}