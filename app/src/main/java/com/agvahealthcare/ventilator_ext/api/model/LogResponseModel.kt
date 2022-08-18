package com.agvahealthcare.ventilator_ext.api.model

import Data
import com.google.gson.annotations.SerializedName

data class LogResponseModel (
    @SerializedName("status") val status : Int,
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : Data
)