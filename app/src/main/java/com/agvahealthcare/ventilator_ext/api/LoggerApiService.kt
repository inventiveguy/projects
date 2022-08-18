package com.agvahealthcare.ventilator_ext.api

import com.agvahealthcare.ventilator_ext.api.model.LogResponseModel
import com.agvahealthcare.ventilator_ext.api.model.alarmDataModel.AlarmRequestBodyModel

import com.agvahealthcare.ventilator_ext.api.model.datamodel.RequestBodyModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoggerApiService {

    //interface for the retrofit to make a crash update on the server
    @Headers( "Content-Type: application/json" )
    @POST("api/logger/logs/SBXMH")
    fun updateServerWithCrash(@Body requestBodyModel: RequestBodyModel):Call<LogResponseModel>

    @Headers("Content-Type: application/json")
    @POST("api/logger/logs/alerts/SBXMH")
    fun updateServerWithAlarms(@Body alarmRequestBodyModel: AlarmRequestBodyModel):Call<LogResponseModel>
}