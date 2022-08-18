package com.agvahealthcare.ventilator_ext.api.services

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.agvahealthcare.ventilator_ext.BuildConfig
import com.agvahealthcare.ventilator_ext.api.LoggerApiService
import com.agvahealthcare.ventilator_ext.api.model.alarmDataModel.AlarmRequestBodyModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AlarmUploadWorker (context: Context,workerParameters: WorkerParameters):Worker(context,workerParameters) {
    override fun doWork(): Result {
        var data=inputData.getString("DATABODY")
        var requestAlarmBodyModel= Gson().fromJson(data, AlarmRequestBodyModel::class.java)
        return networkAlarmUpload(requestAlarmBodyModel)
    }
    private fun networkAlarmUpload(alarmRequestBodyModel:AlarmRequestBodyModel):Result{
        val okHttpClient= OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build()
        //val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),"\n{\n    \"version\" : \"${requestBodyModel.version}\",\n    \"type\" : \"${requestBodyModel.type}\",\n    \"log\" : {\n        \"file\" : \"${requestBodyModel.log?.file}\",\n        \"date\" : \"${requestBodyModel.log?.date}\",\n        \"msg\" : \"${requestBodyModel.log?.msg}\",\n        \"type\" : \"error\"\n    },\n    \"device\" : {\n        \"did\" : \"${requestBodyModel.device?.did}\",\n        \"name\" : \"${requestBodyModel.device?.name}\",\n        \"manufacturer\" : \"${requestBodyModel.device?.manufacturer}\", \n        \"battery\" : null,\n        \"os\" : {\n            \"name\" : \"${requestBodyModel.device?.os?.name}\",\n            \"type\" : \"${requestBodyModel.device?.os?.type}\"\n        }\n    }\n}\n")

        val retrofit= Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val response = retrofit.create(LoggerApiService::class.java)
        /*.updateServerWithCrash(requestBodyModel).execute()*/
        try {
            val responseAlarm=response.updateServerWithAlarms(alarmRequestBodyModel).execute()
            Log.d("responsealarm",responseAlarm.message())
            return if (responseAlarm.isSuccessful) Result.success() else Result.failure()
        /*
            response.updateServerWithAlarms(alarmRequestBodyModel).enqueue(object :
                Callback<LogResponseModel> {
                override fun onResponse(
                    call: Call<LogResponseModel>,
                    response: Response<LogResponseModel>
                ) {
                    Log.d("responseofalarm",response.code().toString())
                }

                override fun onFailure(call: Call<LogResponseModel>, t: Throwable) {
                    Log.d("responseofalarm",t.message.toString())
                    return Result.failure()
                }
            })
*/
        } catch (e:Exception){
            e.printStackTrace()
            Log.d("responseerror",e.printStackTrace().toString())
            return Result.failure()
        }

    }
}