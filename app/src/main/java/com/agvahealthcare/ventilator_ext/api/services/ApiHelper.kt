package com.agvahealthcare.ventilator_ext.api.services

import com.agvahealthcare.ventilator_ext.api.model.LogRequestModel
import com.agvahealthcare.ventilator_ext.api.model.LogResponseModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiHelper {

   @POST("/api/logger/projects/makeLog/MF7OW")
    fun createLog(@Header("Content-Type") content_type: String,
                  @Body logServer: LogRequestModel
    ): Observable<LogResponseModel>

}
